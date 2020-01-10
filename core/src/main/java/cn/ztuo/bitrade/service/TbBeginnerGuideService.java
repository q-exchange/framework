package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.dao.MemberDao;
import cn.ztuo.bitrade.dao.MemberTransactionDao;
import cn.ztuo.bitrade.dao.TbBeginnerGuideDao;
import cn.ztuo.bitrade.entity.Member;
import cn.ztuo.bitrade.entity.TbBeginnerGuide;
import cn.ztuo.bitrade.vo.BeginnerGuideVO;
import cn.ztuo.bitrade.vo.NoviceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/10/15 9:42
 * @Version: 1.0
 */
@Service
@Slf4j
public class TbBeginnerGuideService {

    @Autowired
    private TbBeginnerGuideDao guideDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberTransactionDao transactionDao;

    public BeginnerGuideVO getAllForSort(Long userId) {
        boolean status = false;
        List<TbBeginnerGuide> sortList = new ArrayList<>();
        if (userId != null) {
            Member member = memberDao.findOne(userId);
            int count = transactionDao.getCountByMemberIdAndType(member.getId(), 3);
            log.info("----查询出币币交易次数--->count={}", count);
            // 如果用户币币交易大于十次再显示新手指南部分
//            if (count > 10) {
                sortList = guideDao.getAllBySort();
                // 如果查询出数据长度大于0再显示
//                if (sortList.size() > 0) {
                    status = true;
//                }
//            }
        }
        return new BeginnerGuideVO(status, sortList);
    }


    public List<TbBeginnerGuide> findAllByCreateTimeDesc() {
        return guideDao.findByCreateTimeDesc();
    }

    public void update(TbBeginnerGuide beginnerGuide) {
        TbBeginnerGuide tbBeginnerGuide = guideDao.findOne(beginnerGuide.getId());
        beginnerGuide.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        beginnerGuide.setCreateTime(tbBeginnerGuide.getCreateTime());
        guideDao.save(beginnerGuide);
    }


    public void delete(Long id) {
        guideDao.delete(id);
    }

    public TbBeginnerGuide insert(TbBeginnerGuide beginnerGuide) {
        beginnerGuide.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return guideDao.save(beginnerGuide);
    }
}
