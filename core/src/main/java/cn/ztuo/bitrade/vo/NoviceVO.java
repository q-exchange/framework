package cn.ztuo.bitrade.vo;

import cn.ztuo.bitrade.entity.TbBeginnerGuide;
import lombok.Data;

import java.util.List;

/**
 * @Author: dupinyan
 * @Description: 新手流程返回体
 * @Date: 2019/10/14 19:34
 * @Version: 1.0
 */
@Data
public class NoviceVO {

    public NoviceVO() {
    }

    public NoviceVO(boolean status, Integer type) {
        this.status = status;
        this.type = type;
    }


    private boolean status;

    private Integer type;


}
