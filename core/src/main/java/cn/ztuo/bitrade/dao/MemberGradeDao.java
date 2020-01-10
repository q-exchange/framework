package cn.ztuo.bitrade.dao;

import cn.ztuo.bitrade.dao.base.BaseDao;
import cn.ztuo.bitrade.entity.MemberGrade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @description: MemberGradeDao
 * @author: MrGao
 * @create: 2019/04/25 15:54
 */
@Repository
public interface MemberGradeDao extends BaseDao<MemberGrade> {


//    MemberGrade queryMeberGradeByBound(Integer boundAmount);

    @Query(value = "SELECT exchange_fee_rate FROM member_grade WHERE id = :id", nativeQuery = true)
    BigDecimal getExchangeFeeRateForId(@Param("id") Long id);
}
