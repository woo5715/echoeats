<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval expression="@prop['api.impId']" var="impId" />
<%@ page session="false"%>
<html>
<head>
<meta charset="UTF-8">
<title>checkout</title>
<script src="https://code.jquery.com/jquery-1.11.3.js"></script>
<script src="https://cdn.iamport.kr/v1/iamport.js"></script>
<script type="text/javascript" charset="utf-8">
	sessionStorage.setItem("contextpath", "${pageContext.request.contextPath}");
</script>
<script>
	  $(function(){
		  let msg = "${msg}";
		  if (msg.trim() !== "") { console.log(msg); }
		  
		  $(".btnPay").on("click",payProc);
		  $(".btnRfd").on("click",payCancel);
	  });
	  // context_path 가져오기
	  var ctx = getContextPath();
	  function getContextPath() {
		  console.log("contextpath = "+sessionStorage.getItem("contextpath"));
		  return sessionStorage.getItem("contextpath");
	  }
	  let payDto;
	  // 장바구니에서 주문서정보를 가져온다
	  let checkout = {
		  prdList: [
		        { id: 1, name: "Product1", price: 20, quantity: 2 },
		        { id: 2, name: "Product2", price: 30, quantity: 1 },
		        { id: 3, name: 'Product3', price: 50, quantity: 3 },
		    ],
		  member: { id: "123", name: "정석 코딩", ph: "000-0000-0000", 
			  email: "test@test.com", dlvyAddr: "서울 YMCA", dlvyRep:"문앞에 놓고가주세요" },
		  pay_way: "kakao"
      };
	  // 결제 과정 
	  const payProc = () => {
		  prevVerify()
		  .then(function(result) {
			  if(result){
				  payment();
			  }else
				  alert("비정상적인 결제가 확인되었습니다1");
		  }).catch(function(err) {
		    console.log(err);
		  })
	  };
	  // 총 상품구매금액과 DB Table의 해당상품의 금액을 비교
	  const prevVerify = () => {
		  return new Promise(function(resolve, reject) {
		  console.log("prevVerify");
		  $.ajax({
	            url: ctx + '/payment/verify/prev',
	            type: 'POST',
	            headers : { "content-type": "application/json"},
	            data : JSON.stringify(checkout.prdList),
	            success: function (result) {
	                console.log('/verify/prev OK', result);
	                resolve(result);
	            },
	            error: function () {
	            	console.log('/verify/prev ERR');
	            	reject(false);
	            }
	        });
		  })
	  };
	  // 결제(주문테이블생성, 결제api호출, 결제실패성공에따른 테이블 insert,update )
	  const payment = () => {
		  console.log("payment");
		  //1. 주문 주문상세 주문이력 테이블 insert
		  orderInsert()
		  .then(function(result) {
			//2. 결제 API 호출
			openPayAPI();
		  }).catch(function(err) {
		    console.log(err);
		  })
		  
		  //3. 결제 API 성공 실패에 따른 주문 주문상세 주문이력 테이블 update + 결제 결제이력 테이블 insert
	  };
		// 주문(주문,주문상세,주문이력 insert)
	  const orderInsert = () => {
		  return new Promise(function(resolve, reject) {
			  console.log("orderInsert");
			  $.ajax({
		            url: ctx + '/order/order',
		            type: 'POST',
		            headers : { "content-type": "application/json"},
		            data : JSON.stringify(checkout),
		            success: function (result) {
		                console.log('/order/order OK', result);
		                checkout = result;
		                resolve(result);
		            },
		            error: function () {
		            	console.log('/order/order ERR');
		            	reject(false);
		            }
		        });
			  })
	};
		 // 결제 api 호출
	const openPayAPI = () => {
		var impId = '${impId}';
		IMP.init(impId);
		IMP.request_pay({
			//pg: "kakaopay",
			pg: "tosspayments",
			pay_method: "card",
			amount: checkout.totPayPrice,
			name: checkout.totPrdName,
			merchant_uid: checkout.ord_id,
		}, function (rsp) {
			//alert("imp_uid: " + rsp.imp_uid); //ex) imp_592100262367 imp_480634238960
			const payCheck = new Promise(function(resolve, reject) {
			    $.ajax({
			        type: 'POST',
			        url: ctx + '/verify/' + rsp.imp_uid,
			        headers: { "content-type": "application/json" },
			        //dataType: 'text',
			        data: JSON.stringify({imp_uid: rsp.imp_uid }),
			        success: function(result) {
			            console.log(result);
			            resolve(result);
			        },
			        error: function() {
			            console.error("payCheck Erorr");
			            reject("Payment failed");
			        }
			    });
			});
			payCheck.then(function(result) {
			    if (rsp.success) {
			    	// 결제api후 성공 실패
			        payFinish(result,'CPD'); // completed
			    } else {
			        alert("Payment failed. Error details: " + rsp.error_msg);
			        payFinish(result,'DCL'); // Declined 
			    }
			}).catch(function(error) {
			    alert(error);
			});
		});
	};
	//결제테이블 작성
	const payFinish = (payCheck,sts) => {
		console.log("payFinish");
		
 		payDto = {
			pay_id: payCheck.response.imp_uid,
            ord_id: payCheck.response.merchant_uid,
            pay_way_id: payCheck.response.paid_at,// 임시로넣음
            tot_prod_name: payCheck.response.name,
            tot_prod_price: payCheck.response.amount,
            tot_pay_price: payCheck.response.amount,
            pay_sts: sts, 
        };
 		const payWrite = new Promise(function(resolve, reject) {
 		$.ajax({
	            type:'POST',       // 요청 메서드
	            url: ctx+'/payment/payWrite',  // 요청 URI
	            headers : { "content-type": "application/json"}, // 요청 헤더
	            //dataType : 'text', // 전송받을 데이터의 타입
	            data : JSON.stringify(payDto),  // 서버로 전송할 데이터. stringify()로 직렬화 필요.
	            success : function(result){
	            	console.log(result);//PaymentWrite OK
	            	resolve(payDto);
	            },
	            error : function(){ alert("payFinish error"); reject("payWrite failed"); } // 에러가 발생했을 때, 호출될 함수
	        }); // $.ajax()
 		});
 		//결제테이블 insert
 		payWrite
 		.then(function(result) {
 			// 결제 후 검증
 			return nextVerify(result);
 		})
 		.then(function(result) {
 			if(!result){
 				payDto.pay_sts = 'CNC';
 				$.ajax({
 		            type:'POST',       // 요청 메서드
 		            url: ctx+'/payment/payModify',  // 요청 URI
 		            headers : { "content-type": "application/json"}, // 요청 헤더
 		            //dataType : 'text', // 전송받을 데이터의 타입
 		            data : JSON.stringify(payDto),  // 서버로 전송할 데이터. stringify()로 직렬화 필요.
 		            success : function(result){
 		            	console.log(result);//Payment CNC Write OK
 		            },
 		            error : function(){ alert("Payment CNC error"); } // 에러가 발생했을 때, 호출될 함수
 		        }); // $.ajax()
 				payCancel('비정상적인 결제가 확인되었습니다2');
 			}
		}).catch(function(error) {
		    alert(error);
		});
	};
	// 결제API 실결제금액 + 할인금액 과 DB Table의 해당상품의 금액을 비교
	const nextVerify = (payDto) => {
		return new Promise(function(resolve, reject) {
		  console.log("nextVerify");
		  $.ajax({
	            url: ctx + '/payment/verify/next',
	            type: 'POST',
	            headers : { "content-type": "application/json"},
	            data : JSON.stringify(payDto),
	            success: function (result) {
	                console.log('/verify/next OK', result);
	                resolve(result);
	            },
	            error: function () {
	            	console.log('/verify/next ERR');
	            	reject("nextVerify failed");
	            }
	        });// $.ajax()
		});
	  };
	// 결제취소 api
	const payCancel = (reanson) => {
		let accesstoken;
		$.ajax({
	        type:'POST',       // 요청 메서드
	        url: ctx+'/verify/token',  // 요청 URI
	        headers : { "content-type": "application/json"}, // 요청 헤더
	        success : function(result){
	        	console.log("accesstoken = "+result);
	            accesstoken = result;
	            
	            $.ajax({
			        type:'POST',       // 요청 메서드
			        url: ctx+'/refundRequest',  // 요청 URI
			        headers : { "content-type": "application/json"}, // 요청 헤더
			        data : JSON.stringify({
			            reason: reanson, // 가맹점 클라이언트로부터 받은 환불사유
			            access_token: accesstoken, // imp_uid를 환불 `unique key`로 입력
			            amount: payDto.tot_prod_price, // 가맹점 클라이언트로부터 받은 환불금액
			            checksum: payDto.tot_prod_price, // [권장] 환불 가능 금액 입력
			            merchant_uid: payDto.ord_id,
			          }),
			        success : function(result){
			        	console.log("cancel = "+result);
			        },
			        error : function(){ alert("refund error") } // 에러가 발생했을 때, 호출될 함수
			    }); // $.ajax()
	        },
	        error : function(){ alert("accesstoken error") } // 에러가 발생했을 때, 호출될 함수
	    }); // $.ajax()
	};
</script>
</head>
<body>
	<table>
		<tr>
			<c:forEach var="product" items="${checkout.prdList}">
				<tr>
					<td class="id">${product.id}</td>
					<td class="name">${product.name}</td>
					<td class="price">${product.price}</td>
					<td class="quantity">${product.quantity}</td>
				</tr>
			</c:forEach>
		<tr>
			<td>${checkout.member.name}</td>
			<td>${checkout.member.ph}</td>
			<td>${checkout.member.email}</td>
			<td>${checkout.member.dlvyAddr}</td>
			<td>${checkout.member.dlvyRep}</td>
		</tr>
	</table>
	<button class="btnPay">결제하기</button>
	<button class="btnRfd">환불하기</button>

	<script>
		
	</script>
	<%-- <script src="<c:url value='/resources/order/js/payment.js'/>"></script> --%>
</body>
</html>
