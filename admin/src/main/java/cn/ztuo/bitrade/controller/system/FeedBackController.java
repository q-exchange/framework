package cn.ztuo.bitrade.controller.system;

import cn.ztuo.bitrade.constant.PageModel;
import cn.ztuo.bitrade.controller.common.BaseAdminController;
import cn.ztuo.bitrade.entity.Feedback;
import cn.ztuo.bitrade.service.FeedbackService;
import cn.ztuo.bitrade.util.MessageResult;
import cn.ztuo.bitrade.vo.BaseQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: FeedBackController
 * @author: MrGao
 * @create: 2019/09/11 16:32
 */
@Slf4j
@RestController
@RequestMapping("feed")
public class FeedBackController extends BaseAdminController {


    @Autowired
    private FeedbackService feedbackService ;

    @RequiresPermissions("system:feed:query_all")
    @RequestMapping(value = "query_all",method = RequestMethod.POST)
    public MessageResult queryAllFeedBack(@RequestBody PageModel queryVO){
       Page<Feedback> page =  feedbackService.findAll(queryVO);
        return success(page) ;
    }

}
