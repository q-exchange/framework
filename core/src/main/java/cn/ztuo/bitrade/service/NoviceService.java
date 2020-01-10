package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.dao.MemberTransactionDao;
import cn.ztuo.bitrade.vo.NoviceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/10/14 19:25
 * @Version: 1.0
 */
@Service
public class NoviceService {

    @Autowired
    private MemberTransactionDao memberTransactionDao;

    public NoviceVO getProcess(Long userId) {
        boolean status = false;
        int type = 1;
        if (userId != null) {
            int buyCount = memberTransactionDao.getCountByMemberIdAndType(userId, 4);
            if (buyCount > 1) {
                status = true;
                type = 2;
                int transactionCount = memberTransactionDao.getCountByMemberIdAndType(userId, 18);
                if (transactionCount > 1) {
                    type = 3;
                    int tradeCount = memberTransactionDao.getCountByMemberIdAndType(userId, 3);
                    if (tradeCount > 1) {
                        status = false;
                    }
                }
            }
        }
        return new NoviceVO(status, type);
    }
}
