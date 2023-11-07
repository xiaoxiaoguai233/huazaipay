package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZFkShop;

import java.util.List;

public interface IZFkKamiShopService {


    /**
     * 获取惠生活的信息
     * @return
     */
    List<ZFkShop> selectListByPay();

    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zFkShop
     * @return
     */
    List<ZFkShop> selectList(ZFkShop zFkShop, int page, int limit);

    /**
     * 计算全数
     * @param parentId
     * @return
     */
    int selectListCount(Long parentId);

    /**
     * 通过手机号码查询是否存在重复
     * @param name
     * @return
     */
    ZFkShop selectByName(String name);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZFkShop selectById(@Param("id") Long id);


    /**
     * 更新数据
     * @param zFkShop
     * @return
     */
    int update(ZFkShop zFkShop);

    /**
     * 添加收款账号
     * @param zFkShop
     * @return
     */
    int insert(ZFkShop zFkShop);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);

}
