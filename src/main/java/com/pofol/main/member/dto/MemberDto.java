package com.pofol.main.member.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class MemberDto {

    private String mem_id;
    private String gd_name; //등급 이름
    private String mem_code_id;  //회원 상태
    private String mem_pwd;
    private String mem_name;
    private String mem_email;
    private String mem_phone;
    private String mem_gender;

    private String mem_birth;
    private Integer point;
    private String per_info_col;
    private String free_ship_recep;

    private String rg_date;
    private String rg_num;
    private String md_date;
    private String md_num;
    private String update_date;

    private String role;

    public MemberDto() {
        this.point = 0;
        this.gd_name = "씨앗";
        this.mem_code_id = "활성";
        this.free_ship_recep = "N";
        this.per_info_col = "N";
        this.role = "USER";
    }

    //카카오
    public  MemberDto(String mem_id, String mem_name, String mem_pwd){
        this.mem_id = mem_id;
        this.gd_name = "씨앗";
        this.mem_code_id = "활성";
        this.mem_pwd = mem_pwd;
        this.mem_name = mem_name;
        this.mem_email = "카카오";
        this.mem_phone = "카카오";
        this.mem_gender = "카";
        this.mem_birth = "카카오";
        this.point = 0;
        this.per_info_col = "N";
        this.free_ship_recep = "N";
        this.rg_num = "admin123";
        this.md_num = "admin123";
        this.role = "USER";
    }

    public MemberDto(String mem_id, String gd_name, String mem_code_id, String mem_pwd, String mem_name, String mem_email, String mem_phone, String mem_gender, String mem_birth, int point, String per_info_col, String free_ship_recep, String rg_date, String rg_num, String md_date, String md_num, String update_date, String role) {
        this.mem_id = mem_id;
        this.gd_name = gd_name;
        this.mem_code_id = mem_code_id;
        this.mem_pwd = mem_pwd;
        this.mem_name = mem_name;
        this.mem_email = mem_email;
        this.mem_phone = mem_phone;
        this.mem_gender = mem_gender;
        this.mem_birth = mem_birth;
        this.point = point;
        this.per_info_col = per_info_col;
        this.free_ship_recep = free_ship_recep;
        this.rg_date = rg_date;
        this.rg_num = rg_num;
        this.md_date = md_date;
        this.md_num = md_num;
        this.update_date = update_date;
        this.role = role;
    }

    public MemberDto(String mem_id, String mem_pwd, String mem_name, String mem_email, String mem_phone, String mem_gender, String mem_birth, String per_info_col, String free_ship_recep, String rg_date, String md_date) {
        this.mem_id = mem_id;
        this.mem_pwd = mem_pwd;
        this.mem_name = mem_name;
        this.mem_email = mem_email;
        this.mem_phone = mem_phone;
        this.mem_gender = mem_gender;
        this.mem_birth = mem_birth;
        this.per_info_col = per_info_col;
        this.free_ship_recep = free_ship_recep;
        this.rg_date = rg_date;
        this.md_date = md_date;
//        this.update_date = update_date;
    }


    @Override
    public String toString() {
        return "memberDto{" +
                "mem_id='" + mem_id + '\'' +
                ", gd_name='" + gd_name + '\'' +
                ", mem_code_id='" + mem_code_id + '\'' +
                ", mem_pwd='" + mem_pwd + '\'' +
                ", mem_name='" + mem_name + '\'' +
                ", mem_email='" + mem_email + '\'' +
                ", mem_phone='" + mem_phone + '\'' +
                ", mem_gender='" + mem_gender + '\'' +
                ", mem_birth='" + mem_birth + '\'' +
                ", point=" + point +
                ", per_info_col='" + per_info_col + '\'' +
                ", free_ship_recep='" + free_ship_recep + '\'' +
                ", rg_date=" + rg_date +
                ", rg_num='" + rg_num + '\'' +
                ", md_date=" + md_date +
                ", md_num='" + md_num + '\'' +
                ", update_date=" + update_date +
                ", role='" + role + '\'' +
                '}';
    }


    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }


    public void setGd_name(String gd_name) {
        this.gd_name = gd_name;
    }


    public void setMem_pwd(String mem_pwd) {
        this.mem_pwd = mem_pwd;
    }


    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setRg_date(String rg_date) {
        this.rg_date = rg_date;
    }


    public void setRg_num(String rg_num) {
        this.rg_num = rg_num;
    }

    public void setMd_date(String md_date) {
        this.md_date = md_date;
    }

    public void setMd_num(String md_num) {
        this.md_num = md_num;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
