package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.dao.MemberDao;
import cn.ztuo.bitrade.dao.MemberGradeDao;
import cn.ztuo.bitrade.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author: dupinyan
 * @Description: 费率接口
 * @Date: 2019/10/14 15:30
 * @Version: 1.0
 */
@Service
@Slf4j
public class RateService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberGradeDao memberGradeDao;

    /**
     * 获取用户费率
     * @param userId
     * @return
     */
    public BigDecimal[] getFeeRate(Long userId) {
        Member member = memberDao.findOne(userId);
        // 获取用户等级id
        Long memberGradeId = member.getMemberGradeId();
        log.info("---获取用户id---memberGradeId={}", memberGradeId);
        // 根据用户等级id查询费率
        BigDecimal exchangeFeeRate = memberGradeDao.getExchangeFeeRateForId(memberGradeId);
        log.info("---查询出汇率---exchangeFeeRate={}", exchangeFeeRate);
        BigDecimal[] exchangeFeeRates = new BigDecimal[]{exchangeFeeRate, exchangeFeeRate, exchangeFeeRate};
        return exchangeFeeRates;
    }
}
