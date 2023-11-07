package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZFkKami;

import java.util.List;

public interface ZFkKamiMapper {


    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zFkKami
     * @return
     */
    List<ZFkKami> selectList(@Param("zFkKami") ZFkKami zFkKami, @Param("page")int page, @Param("limit")int limit);

    /**
     * 计算全数
     * @param user_id
     * @return
     */
    int selectListCount(@Param("user_id") Long user_id);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZFkKami selectById(@Param("id") Long id);

    /**
     * 通过ID查询
     * @param card
     * @return
     */
    ZFkKami selectByCard(@Param("card") String card);

    /**
     * 更新数据
     * @param zFkKami
     * @return
     */
    int update(@Param("zFkKami") ZFkKami zFkKami);

    /**
     * 添加收款账号
     * @param zFkKami
     * @return
     */
    int insert(@Param("zFkKami")  ZFkKami zFkKami);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);

}
