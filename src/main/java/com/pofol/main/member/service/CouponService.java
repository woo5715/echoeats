package com.pofol.main.member.service;

import com.pofol.main.member.dto.CouponDownloadDto;
import com.pofol.main.member.dto.CouponDto;
import com.pofol.main.member.dto.CouponJoinDto;
import com.pofol.main.member.dto.MemCouponDto;

import java.text.ParseException;
import java.util.List;

public interface CouponService {
    List<CouponJoinDto> getCouponJoin(String id);
    List<CouponDownloadDto> showDownloadList();




    int update_minus_cp_qty(int dw_id);
    List<Integer> select_downloaded_dw_id(String mem_id);

    CouponDto getCoupon(Long coupon_id);
    MemCouponDto getUnusedCoupon(Long cp_id, String mem_id);
    void modifyCouponStatus(Long cp_id, String mem_id);
    int member_cp_qty_count(String id);


    int check_coupon_qty(int dw_id);
    int download_cp_qty(int dw_id);

    void download_cp(boolean have_coupon, String mem_id, Long cp_id, int dw_id);

    CouponJoinDto dw_cp_out_view(String mem_id, Long cp_id);
}
