package com.pofol.main.member.controller;

import com.pofol.main.member.dto.GradeDto;
import com.pofol.main.member.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class GradeController {

    @Autowired
    GradeService gradeService;

    @GetMapping("/grade")
    public String gradeJoin(Model model, HttpServletRequest httpServletRequest) {

        model.addAttribute("grade", gradeService.show_list());  //화면에 출력될 등급 리스트

        model.addAttribute("mypage","grade");  //마이페이지에서 등급페이지를 출력하기 위해
        return "member/mypage";
    }

    @GetMapping("/grade_data")
    @ResponseBody
    public GradeDto boardAjax(Authentication authentication) {
        //현재 나의 등급 표시
        return gradeService.show_grade(authentication.getName());
    }




}
