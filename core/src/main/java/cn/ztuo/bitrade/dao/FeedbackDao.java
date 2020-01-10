package cn.ztuo.bitrade.dao;

import cn.ztuo.bitrade.dao.base.BaseDao;
import cn.ztuo.bitrade.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * @author GuoShuai
 * @date 2018年03月19日
 */
public interface FeedbackDao extends JpaRepository<Feedback, String>, JpaSpecificationExecutor<Feedback>, QueryDslPredicateExecutor<Feedback> {
}
