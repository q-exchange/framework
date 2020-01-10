package cn.ztuo.bitrade.controller.member;

import cn.ztuo.bitrade.controller.BaseController;
import cn.ztuo.bitrade.entity.TbBeginnerGuide;
import cn.ztuo.bitrade.service.NoviceService;
import cn.ztuo.bitrade.service.TbBeginnerGuideService;
import cn.ztuo.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: dupinyan
 * @Description: 新手指南后台管理
 * @Date: 2019/10/15 10:17
 * @Version: 1.0
 */
@RestController
@RequestMapping("novice")
@Slf4j
public class NoviceController extends BaseController {

    @Autowired
    private TbBeginnerGuideService guideService;


    /**
     * 时间倒序查询所有新手指南
     * @return
     */
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public MessageResult findAll() {
        List<TbBeginnerGuide> beginnerGuideList = guideService.findAllByCreateTimeDesc();
        return success(beginnerGuideList);
    }


    /**
     * 修改某一新手指南
     * @param beginnerGuide
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public MessageResult update(@RequestBody TbBeginnerGuide beginnerGuide) {
        guideService.update(beginnerGuide);
        return success("修改成功");
    }


    /**
     * 删除某一新手指南
     * @param id
     * @return
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public MessageResult delete(@PathVariable Long id) {
        log.info("---->删除视频--->id={}", id);
        guideService.delete(id);
        return success("删除成功");
    }


    /**
     * 新增某一新手指南
     * @param beginnerGuide
     * @return
     */
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public MessageResult insert(@RequestBody TbBeginnerGuide beginnerGuide) {
        TbBeginnerGuide insertRecord = guideService.insert(beginnerGuide);
        if (insertRecord == null) {
            return error("新增失败");
        }
        return success("新增成功");
    }
}
