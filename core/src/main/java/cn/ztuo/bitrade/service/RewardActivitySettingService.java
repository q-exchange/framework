package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.constant.ActivityRewardType;
import cn.ztuo.bitrade.constant.BooleanEnum;
import cn.ztuo.bitrade.dao.RewardActivitySettingDao;
import cn.ztuo.bitrade.entity.RewardActivitySetting;
import cn.ztuo.bitrade.service.Base.TopBaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author GuoShuai
 * @date 2018年03月08日
 */
@Service
public class RewardActivitySettingService extends TopBaseService<RewardActivitySetting,RewardActivitySettingDao> {

    @Autowired
    public void setDao(RewardActivitySettingDao dao) {
        this.dao = dao ;
    }


    public RewardActivitySetting findByType(ActivityRewardType type){
        return dao.findByStatusAndType(BooleanEnum.IS_TRUE, type);
    }

    public RewardActivitySetting save(RewardActivitySetting rewardActivitySetting){
        return dao.save(rewardActivitySetting);
    }

   /* public List<RewardActivitySetting> page(Predicate predicate){
        Pageable pageable = new PageRequest()
        Iterable<RewardActivitySetting> iterable = rewardActivitySettingDao.findAll(predicate, QRewardActivitySetting.rewardActivitySetting.updateTime.desc());
        return (List<RewardActivitySetting>) iterable ;
    }*/

   public Page<RewardActivitySetting> findAll(Predicate predicate , PageRequest pageRequest){

       return dao.findAll(predicate,pageRequest);
   }


}
