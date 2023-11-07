package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZHxtAccount;

import java.util.List;

public interface IZHxtAccountService {



    /*****************************************************************
     *
     *                          支付业务
     *
     *****************************************************************/
    /**
     * 获取最优的收款账号
     * @return
     */
    ZHxtAccount selectReceiveAccountByUpdateTime();












    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zHxtAccount
     * @return
     */
    List<ZHxtAccount> selectList(ZHxtAccount zHxtAccount, int page, int limit);

    /**
     * 计算全数
     * @param parentId
     * @return
     */
    int selectListCount(Long parentId);

    /**
     * 通过手机号码查询是否存在重复
     * @param phone
     * @return
     */
    ZHxtAccount selectByPhone(String phone);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZHxtAccount selectById(@Param("id") Long id);


    /**
     * 更新数据
     * @param zHxtAccount
     * @return
     */
    int update(ZHxtAccount zHxtAccount);

    /**
     * 添加收款账号
     * @param zHxtAccount
     * @return
     */
    int insert(ZHxtAccount zHxtAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}

