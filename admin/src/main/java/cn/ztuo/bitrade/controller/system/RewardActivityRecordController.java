package cn.ztuo.bitrade.controller.system;

import cn.ztuo.bitrade.vo.RewardActivityVO;
import com.alibaba.fastjson.JSONObject;
import cn.ztuo.bitrade.constant.BooleanEnum;
import com.querydsl.core.types.dsl.BooleanExpression;
import cn.ztuo.bitrade.annotation.AccessLog;
import cn.ztuo.bitrade.constant.ActivityRewardType;
import cn.ztuo.bitrade.constant.AdminModule;
import cn.ztuo.bitrade.controller.common.BaseAdminController;
import cn.ztuo.bitrade.entity.QRewardActivitySetting;
import cn.ztuo.bitrade.entity.RewardActivitySetting;
import cn.ztuo.bitrade.service.RewardActivitySettingService;
import cn.ztuo.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@Slf4j
@RestController
@RequestMapping("/system/reward-activity-record")
public class RewardActivityRecordController extends BaseAdminController {

    @Autowired
    private RewardActivitySettingService rewardActivitySettingService;

    @RequiresPermissions("system:reward-activity-record:merge")
    @PostMapping("merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建修改邀请奖励设置")
    public MessageResult merge(@Valid RewardActivitySetting setting) {
        String info = setting.getInfo();
        JSONObject object = new JSONObject();
        object.put("amount",info);
        setting.setInfo(object.toJSONString());
        setting.setStatus(BooleanEnum.IS_TRUE);
        rewardActivitySettingService.save(setting);
        return MessageResult.success("保存成功");
    }

    /**
     * 查询所有未被禁用的（判断type条件）
     * 默认按照id降序
     *
     * @param rewardActivityVO
     * @return
     */
    @RequiresPermissions("system:reward-activity-record:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查询邀请奖励设置")
    public MessageResult pageQuery(@RequestBody RewardActivityVO rewardActivityVO) {
        BooleanExpression predicate = null;
        log.info("查询所有未被禁用的+{}", rewardActivityVO);
        if (StringUtils.isNotEmpty(rewardActivityVO.getType())) {
            predicate = QRewardActivitySetting.rewardActivitySetting.type.eq(ActivityRewardType.valueOfOrdinal(Integer.parseInt(rewardActivityVO.getType())));
        }
        log.info("查询所有未被禁用的+{}", rewardActivityVO);
        PageRequest pageRequest = new PageRequest(rewardActivityVO.getPageNo()-1, rewardActivityVO.getPageSize() );
        Page<RewardActivitySetting> all = rewardActivitySettingService.findAll(predicate, pageRequest);
        return success(all);
    }

    @RequiresPermissions("system:reward-activity-record:deletes")
    @PostMapping("deletes")
    @AccessLog(module = AdminModule.SYSTEM, operation = "批量删除邀请奖励设置")
    public MessageResult deletes(Long[] ids) {
        Assert.notNull(ids, "ids不能为null");
        rewardActivitySettingService.deletes(ids);
        return MessageResult.success("删除成功");
    }


}
