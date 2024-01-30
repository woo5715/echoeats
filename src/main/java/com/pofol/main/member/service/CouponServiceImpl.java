package com.pofol.main.member.service;

import com.pofol.main.member.dto.CouponDownloadDto;
import com.pofol.main.member.dto.CouponDto;
import com.pofol.main.member.dto.CouponJoinDto;
import com.pofol.main.member.dto.MemCouponDto;
import com.pofol.main.member.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    @Override
    public List<CouponJoinDto> getCouponJoin(String id) {

       try{
           List<CouponJoinDto> coupon = couponRepository.selectMembersWithCoupons(id);
           for (CouponJoinDto couponJoinDto : coupon) {
               switch (couponJoinDto.getCp_sts()) {
                   case "UNUSED":
                       couponJoinDto.setCp_sts("미사용");
                       break;
                   case "USED":
                       couponJoinDto.setCp_sts("사용 완료");
                       break;
                   case "EXPIRES":
                       couponJoinDto.setCp_sts("기간 만료");
                       break;
               }
           }
           return coupon;

       } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CouponDownloadDto> showDownloadList() {
        return couponRepository.select_downloadList();
    }


    @Override
    public int update_minus_cp_qty(int dw_id) {
        return couponRepository.update_minus_cp_qty(dw_id);
    }

    @Override
    public List<Integer> select_downloaded_dw_id(String mem_id) {
        return couponRepository.select_downloaded_dw_id(mem_id);
    }

        
    

    @Override
    public CouponDto getCoupon(Long coupon_id) {
        try{
            return couponRepository.select_coupon(coupon_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemCouponDto getUnusedCoupon(Long cp_id, String mem_id) {
        try {
            return couponRepository.selectUnusedCoupon(cp_id, mem_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifyCouponStatus(Long cp_id, String mem_id) {
        try {
            MemCouponDto memCoupon = couponRepository.selectUnusedCoupon(cp_id, mem_id);//멤버 쿠폰 데이블 가지고 오기
            int cp_qty = memCoupon.getCp_qty(); //쿠폰 수량

            if(cp_qty > 1){
                memCoupon.setCp_qty(--cp_qty);
            } else if (cp_qty == 1){
                memCoupon.setCp_qty(--cp_qty);
                memCoupon.setCp_sts("USED");
            } else {
                throw new RuntimeException("쿠폰 상태와 수량에 문제가 발생했습니다.");
            }

            couponRepository.updateMemberCouponStatus(memCoupon);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int member_cp_qty_count(String id) {
        try {
            return couponRepository.member_cp_qty_count(id);
        }catch (Exception e){
            logger.error("화원이 보유중인 쿠폰 갯수 조회 불가 : {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public int check_coupon_qty(int dw_id) {
        return couponRepository.select_coupon_download_cp_qty(dw_id);
    }

    @Override
    @Transactional
    public void download_cp(boolean have_coupon, String mem_id, Long cp_id, int dw_id)  {
        try {
            if (have_coupon) {
                couponRepository.update_cp_qty(new CouponJoinDto(mem_id, cp_id));
            } else {
                couponRepository.insert_memCoupon(new MemCouponDto(mem_id, cp_id));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("mem_id", mem_id);
            map.put("dw_id", dw_id);
            couponRepository.update_minus_cp_qty(dw_id);
            couponRepository.insert_cp_dw_hi(map);
        }catch (Exception e){
            throw new RuntimeException(e);
        }



    }

    @Override
    public CouponJoinDto dw_cp_out_view(String mem_id, Long cp_id) {
        CouponJoinDto couponJoinDto = null;
            List<CouponJoinDto> couponJoinDtos = couponRepository.selectMembersWithCoupons(mem_id);
            for (CouponJoinDto joinDto : couponJoinDtos) {
                if (joinDto.getCp_id().equals(cp_id)) {
                    couponJoinDto = joinDto;
                }
            }
            if(couponJoinDto != null){
                return couponJoinDto;
            }else {
                throw new RuntimeException("다운 받은 쿠폰이 들어오지 않음");
            }

    }

    @Override
    public int download_cp_qty(int dw_id) {
        return couponRepository.select_coupon_download_cp_qty(dw_id);
    }


}
