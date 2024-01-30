package com.pofol.main.member.service;

import com.pofol.main.member.dto.MemberDto;
import com.pofol.main.member.repository.MemberRepository;
import com.pofol.main.member.task.GradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);


    @Override
    public MemberDto select(String id)  {
        try {
            return memberRepository.selectMember(id);
        }catch (Exception e){
            logger.error("회원 조회 실패 {}", e.getMessage());
            return null;
        }
    }


    @Override
    public int signin (MemberDto memberDto) throws Exception {
         return memberRepository.insertMember(memberDto);

     }

    @Override
    public List<MemberDto> check_grade(String date) {
        try {
            return memberRepository.checkGrade(date);
        }catch (Exception e){
            logger.error("회원 등급 체크 실패 {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int update_grade(MemberDto memberDto)  {
        try {
            return memberRepository.updateGrade(memberDto);
        }catch (Exception e){
            logger.error("회원 등급 업데이트 실패 {}", e.getMessage());
            return -1;
        }
    }

}
