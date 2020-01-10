package cn.ztuo.bitrade.controller;

import cn.ztuo.bitrade.entity.transform.AuthMember;
import cn.ztuo.bitrade.service.NoviceService;
import cn.ztuo.bitrade.service.TbBeginnerGuideService;
import cn.ztuo.bitrade.util.MessageResult;
import cn.ztuo.bitrade.vo.BeginnerGuideVO;
import cn.ztuo.bitrade.vo.NoviceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static cn.ztuo.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * @Author: dupinyan
 * @Description:  新手相关接口层
 * @Date: 2019/10/14 18:43
 * @Version: 1.0
 */
@RestController
@RequestMapping("novice")
@Slf4j
public class NoviceController extends BaseController {

    @Autowired
    private NoviceService noviceService;

    @Autowired
    private TbBeginnerGuideService guideService;

    /**
     * 获取新手步骤
     *
     * @param
     * @return
     */
    @RequestMapping(value = "getProcess")
    public MessageResult getProcess(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        log.info("----->获取新手步骤---->member={}", member);
        NoviceVO process = noviceService.getProcess(member.getId());
        return success(process);
    }


    /**
     * 获取新手指南
     *
     * @param
     * @return
     */
    @RequestMapping(value = "getVideo")
    public MessageResult getVideo(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        log.info("----->获取新手指南---->member={}", member);
        BeginnerGuideVO allForSort = guideService.getAllForSort(member.getId());
        return success(allForSort);
    }
}
