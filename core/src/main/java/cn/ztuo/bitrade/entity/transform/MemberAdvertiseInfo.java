package cn.ztuo.bitrade.entity.transform;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.ztuo.bitrade.constant.BooleanEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author GuoShuai
 * @date 2018年01月16日
 */
@Builder
@Data
public class MemberAdvertiseInfo {
    private List<ScanAdvertise> buy;
    private List<ScanAdvertise> sell;
    private String username;
    private String avatar;
    private BooleanEnum realVerified;
    private BooleanEnum emailVerified;
    private BooleanEnum phoneVerified;
    private Integer level;
    private int transactions;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
