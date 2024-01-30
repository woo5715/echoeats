package com.pofol.main.member.task;

import com.pofol.main.member.dto.GradeDto;
import com.pofol.main.member.dto.MemberDto;
import com.pofol.main.member.service.CouponServiceImpl;
import com.pofol.main.member.service.GradeService;
import com.pofol.main.member.service.MemberService;
import com.pofol.main.orders.payment.domain.PaymentDto;
import com.pofol.main.orders.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GradeTask {

    private static final Logger logger = LoggerFactory.getLogger(GradeTask.class);


   private final MemberService memberService;

    private final GradeService gradeService;

    private final PaymentRepository paymentRepository;


    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(cron = "*/10 * * * * *")
    public void testMethod()  {
        logger.info("================등급 갱신================");



        //db에서 등급을 가져온다
        List<GradeDto> gradeDtos = gradeService.show_list();

        //오늘 날짜를 가져온다
        LocalDate currentDate = LocalDate.now();

        //밑에 두 날짜는 한 달동안 총결제한 금액을 가져오는데 사용
        //오늘 날짜에서 하루를 더함
        String dayPlusOneStr = String.valueOf(currentDate.plusDays(1));
        //전 달 날짜
        String MonthMinusOneStr = String.valueOf(currentDate.minusMonths(1));


        //등급갱신일(회원이 가입한 날짜의 월,일)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        String format_currentDate = formatter.format(currentDate);

        try {
            //등급갱신일이 오늘 날짜인 회원들을 조회
            List<MemberDto> memberDtos = memberService.check_grade(format_currentDate);
            for (MemberDto memberDto : memberDtos) {
                //회원id, 전 달 날짜, 하루를 더한 날짜를 이용해서 한 달동안 결제한 금액을 가져온다
                PaymentDto paymentDto = new PaymentDto(memberDto.getMem_id(), MonthMinusOneStr, dayPlusOneStr);
                Integer monthPay = paymentRepository.selectTotalPrice(paymentDto);

                if (monthPay != null) {
                    //결제한 금액, 등급dto로 등급을 계산하여 반환된 등급을 memberDto에 넣는다
                    String s = cal_grade(monthPay, gradeDtos);
                    logger.info(memberDto.getMem_id()+" : "+monthPay+" || "+ memberDto.getGd_name() +" -> "+s);
                    memberDto.setGd_name(s);
                    //회원 등급 수정
                    memberService.update_grade(memberDto);
                }
            }
        }catch (Exception e){
            logger.error(e.toString());
        }



    }




    public String cal_grade(int money,List<GradeDto> gradeDtos ){
        String member_grade = "";

        for (GradeDto gradeDto : gradeDtos) {
            if(money>= gradeDto.getGd_condition()){
                member_grade = gradeDto.getGd_name();
                break;
            }
        }


        return member_grade;
    }



}

//i1이 null이면??