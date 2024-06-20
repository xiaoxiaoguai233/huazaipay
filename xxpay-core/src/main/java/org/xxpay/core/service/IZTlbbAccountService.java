package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZTlbbAccount;

import java.util.List;

public interface IZTlbbAccountService {


    /*****************************************************************
     *
     *                          支付业务
     *
     *****************************************************************/
    /**
     * 获取最优的收款账号
     * @return
     */
    ZTlbbAccount selectReceiveAccountByUpdateTime();









    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zTlbbAccount
     * @return
     */
    List<ZTlbbAccount> selectList(ZTlbbAccount zTlbbAccount, int page, int limit);

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
    ZTlbbAccount selectByPhone(String phone);
    /**
     * 通过手机号码查询是否存在重复
     * @param token
     * @return
     */
    ZTlbbAccount selectByToken(String token);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZTlbbAccount selectById(@Param("id") Long id);


    /**
     * 更新数据
     * @param zTlbbAccount
     * @return
     */
    int update(ZTlbbAccount zTlbbAccount);

    /**
     * 添加收款账号
     * @param zMthAccount
     * @return
     */
    int insert(ZTlbbAccount zMthAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);

    /**
     * 当天清零
     * @return
     */
    int clearTodayMoney();
}

