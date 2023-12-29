<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

<link href="/resources/css/member/login_form.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="/resources/product/css/main-css.css">
<link rel="stylesheet" href="/resources/product/css/footer.css">
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Document</title>
</head>
<body>

<%@ include file="../include/header.jspf" %>

<div class="login">
    <div class="title">로그인</div>
    <div class="login_form">
        <form id="loginform">
            <div class="id_pw">
                <div class="outer">
                    <div class="inner">
                        <input type="text" name="mem_id" value="${input_id}" placeholder="아이디를 입력해주세요">
                    </div>
                </div>
                <div class="outer">
                    <div class="inner">
                        <input  type="password" name="mem_pwd" placeholder="비밀번호를 입력해주세요">
                    </div>
                </div>
                <div class="warm">${errormsg} </div>
            </div>
            <div class="find">
                <a class="find_inner">아이디 찾기</a>
                <span></span>
                <a class="find_inner">비밀번호 찾기</a>
            </div>
            <div class="login_form_button">
                <button type="button" class="login_button">
                    <span class="button_name">로그인</span>
                </button>
                <button type="button" class="sign_in_button">
                    <span class="button_name" id="join">회원가입</span>
                </button>
                <a class="kakao_a active">
                    <img src="/resources/images/kakao_login_button.png"></a>
            </div>
        </form>
    </div>
</div>
<%@ include file="../include/footer.jspf" %>




<script>



    $('.login_button').on("click", function (){
        console.log('클릭')
        abc();

    });
    $('#loginform input').on('keydown', function (event) {
        if (event.which === 13) {
            console.log('엔터');
            abc();
        }
    });


    function abc(){
        let id_reg = /^[a-zA-Z](?=.*[0-9])[a-zA-Z0-9_]{6,16}$/;
        let pwd_reg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{10,}$/;
        let form = $('#loginform');

        let id = $('input[name="mem_id"]').val();
        let pwd = $('input[name="mem_pwd"]').val();

        if(!id_reg.test(id) && !pwd_reg.test(pwd)){
            alert('입력하신 내용을 다시 확인해주세요.');

        }else {
            form.attr("method", "post");
            form.attr("action", "/login").submit();
        }
    }


    $('.sign_in_button').on("click", function (){
        console.log('회원가입')
        let form = $('#loginform');
        form.attr("method", "GET");
        form.attr("action", "/member/join").submit();


    });

    $('.btm').on("click", function (){
        console.log('회원가입')
        window.location.href = "/join";

    });

    $('.kakao_a').on("click", function () {
        var currentId = $(this).attr('class');
        //현재 작업 중인 도메인 주소
        var currentDomain = window.location.origin;
        if(currentId.includes('active')){
            $(this).removeClass('active').addClass('non');
            window.location.href ="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=adb16712b617f7a830213eb50de44a79&redirect_uri="+currentDomain+"/auth/kakao/callback";

        }
    });



</script>
</body>
</html>