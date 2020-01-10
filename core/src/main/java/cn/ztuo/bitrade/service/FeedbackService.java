package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.constant.PageModel;
import cn.ztuo.bitrade.dao.FeedbackDao;
import cn.ztuo.bitrade.entity.Feedback;
import cn.ztuo.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * @author GuoShuai
 * @date 2018年03月19日
 */
@Service
public class FeedbackService extends BaseService {
    @Autowired
    private FeedbackDao feedbackDao;

    public Feedback save(Feedback feedback){
        return feedbackDao.save(feedback);
    }

    public Page<Feedback> findAll( PageModel pageModel){

        return feedbackDao.findAll(pageModel.getPageable());
    }
}
