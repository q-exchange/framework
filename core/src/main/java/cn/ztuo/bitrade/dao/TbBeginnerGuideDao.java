package cn.ztuo.bitrade.dao;

import cn.ztuo.bitrade.dao.base.BaseDao;
import cn.ztuo.bitrade.entity.TbBeginnerGuide;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/10/15 9:41
 * @Version: 1.0
 */
@Repository
public interface TbBeginnerGuideDao extends BaseDao<TbBeginnerGuide> {


    @Query(value = "SELECT * FROM tb_beginner_guide ORDER BY sort ASC", nativeQuery = true)
    List<TbBeginnerGuide> getAllBySort();

    @Query(value = "SELECT * FROM tb_beginner_guide ORDER BY create_time DESC", nativeQuery = true)
    List<TbBeginnerGuide> findByCreateTimeDesc();

}
