package cn.ztuo.bitrade.vo;

import cn.ztuo.bitrade.entity.TbBeginnerGuide;
import lombok.Data;

import java.util.List;

/**
 * @Author: dupinyan
 * @Description: 新手指南返回体
 * @Date: 2019/10/15 11:06
 * @Version: 1.0
 */
@Data
public class BeginnerGuideVO {

    public BeginnerGuideVO() {
    }

    public BeginnerGuideVO(boolean status, List<TbBeginnerGuide> guideList) {
        this.status = status;
        this.guideList = guideList;
    }

    private boolean status;

    private List<TbBeginnerGuide> guideList;
}
