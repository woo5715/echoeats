package com.pofol.main.member.controller;

import com.pofol.main.member.dto.CouponDownloadDto;
import com.pofol.main.member.dto.CouponDto;
import com.pofol.main.member.dto.CouponJoinDto;
import com.pofol.main.member.dto.MemCouponDto;
import com.pofol.main.member.repository.CouponRepository;
import com.pofol.main.member.service.CouponService;
import com.pofol.main.member.service.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.ContentHandler;
import java.text.ParseException;
import java.util.*;

@Controller
public class CouponController {

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    CouponService couponService;


    @GetMapping("/coupon")
    public String boardJoin(Model model) {
        //현재 인증된 회원의 아이디를 가져온다
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        //회원이 보유한 쿠폰들을 가져온다
        List<CouponJoinDto> coupon = couponService.getCouponJoin(id);

        //다운로드 쿠폰 테이블
        List<CouponDownloadDto> couponDownloadDtos = couponService.showDownloadList();

        //내가 다운 받았던 dw_id들
        List<Integer> integers = couponService.select_downloaded_dw_id(authentication.getName());

        model.addAttribute("already_downloaded", integers);
        model.addAttribute("download", couponDownloadDtos);

        model.addAttribute("coupon", coupon);

        model.addAttribute("mypage", "coupon");
        return "member/mypage";
    }


    @GetMapping("/coupon_dw")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> coupon_dwGet(long cp_id, int dw_id, Authentication authentication, HttpServletRequest request)  {


        if(authentication.getName() != null){

            String user_id = authentication.getName();

            //dw_id로 현재 다운 받을 수 있는 수량 확인
            //수량 > 0 면 다운 가능, 수량 <= 0 다운 불가
            int check_coupon_qyy = couponService.check_coupon_qty(dw_id);

            if(check_coupon_qyy > 0) {
                //받은 기록이 있으면 다운x
                //두 개의 브라우저를 열어서 한쪽 다운로드 후 다른 한쪽 다운로드
                //dw_id로 이 회원이 쿠폰을 다운받은 적이 있는지 확인
                //있으면 x, 없으면 밑에 로직 수행
                List<Integer> integers1 = couponService.select_downloaded_dw_id(user_id);
                boolean is_downloaded = integers1.stream().anyMatch(a -> a == dw_id);

                if (!is_downloaded) {

                    //다운받은 쿠폰이 내가보유중이라면 수량+1, 가지고 있지 않다면 insert
                    List<CouponJoinDto> couponJoin = couponService.getCouponJoin(user_id);
                    boolean have_coupon = couponJoin.stream().anyMatch(a -> a.getCp_id().equals(cp_id));

                    //회원에 쿠폰을 넣고, 쿠폰테이블에 있는 수량 -1,쿠폰 다운 이력에 추가
                    couponService.download_cp(have_coupon,user_id, cp_id,dw_id);

                    //쿠폰 다운 이력 테이블과 쿠폰 다운 테이블의 중복된 dw_id들을 반환
                    //숫자들을 jsp로 반환한다
                    List<Integer> integers = couponService.select_downloaded_dw_id(user_id);


                    //jsp에 넘길 쿠폰 수량
                    //한 번 더 서용하는 이유는 위 코드가 실행될 동안 수량이 변경될 수 있기 때문이다
                    //(추가) 그럼 다른 쿠폰들도 최신화해줘야 하는 거 아닌가??
                    int download_cp_qty = couponService.download_cp_qty(dw_id);



                    CouponJoinDto dw_cp_out_view = couponService.dw_cp_out_view(user_id, cp_id);


                    //넘길 데이터
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("downloaded_dw_id_list", integers);   //수정 필요, 이미 쿠폰을 출력했을 때
                    responseMap.put("download_cp_qty", download_cp_qty);  //내가 받은 쿠폰의 남은 수량
                    responseMap.put("download_dw_id", dw_id);  //내가 다운받은 쿠폰 아이디 (수량 최신화를 위해 사용)
                    responseMap.put("coupon_detail", dw_cp_out_view);  //보유중인 쿠폰들에 받은 쿠폰 추가

                    //현재 내가 보유중인 쿠폰 수량 최신화
                    int mem_have_cp_qty = couponService.member_cp_qty_count(user_id);
                    request.getSession().setAttribute("mem_have_cp_qty", mem_have_cp_qty);
                    responseMap.put("mem_have_cp_qty", mem_have_cp_qty);
                    logger.info("보유중인 쿠폰 수량 : {}", mem_have_cp_qty);

                    return new ResponseEntity<>(responseMap, HttpStatus.OK);

                }//if(!is_downloaded)

                //다운 받은 이력이 있는 경우


                return createError("이미 다운받은 쿠폰입니다.", "/coupon", HttpStatus.BAD_REQUEST);

            }//if(check_coupon_qyy > 0)


            return createError("받을 수 있는 쿠폰이 없습니다.", "/coupon", HttpStatus.BAD_REQUEST);

        }//if(authentication.getName() != null)




        return createError("예상치 못한 오류가 발생하였습니다.", "/coupon", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Map<String, Object>> createError(String message, String redirect, HttpStatus status){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", message);
        errorMap.put("redirect", redirect);
        return new ResponseEntity<>(errorMap, status);

    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, Object>> controllerExceptionHandler(Exception e) {
        logger.error(e.getMessage());
        return createError("예상치 못한 오류가 발생하였습니다.", "/coupon", HttpStatus.INTERNAL_SERVER_ERROR);
    }



}

//질문 - 예외
//db에 예외처리 : 사용자에게 -> 예상치 못한 오류 발생, 백에서는 로그를 남긴다
//어떤식으로 예외를 처리해야 하는가?




//문제
//이미 있던 쿠폰을 또 다운받으면 수량이 늘어나는 것이 아닌 새로 출력된다
//신규가입 쿠폰24장, 신규가입쿠폰 다운로드
//신규가입 쿠폰25장
//그래서 만약 다운받은 쿠폰이 내가 보유중이라면 새로 출력이 아닌 수량만 최신화
//태그를 가져와 숫자를 최신화