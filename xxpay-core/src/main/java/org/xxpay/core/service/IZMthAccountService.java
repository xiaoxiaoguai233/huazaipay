package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZMthAccount;

import java.util.List;

public interface IZMthAccountService {



    /*****************************************************************
     *
     *                          支付业务
     *
     *****************************************************************/
    /**
     * 获取最优的收款账号
     * @return
     */
    ZMthAccount selectReceiveAccountByUpdateTime();












    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zMthAccount
     * @return
     */
    List<ZMthAccount> selectList(ZMthAccount zMthAccount, int page, int limit);

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
    ZMthAccount selectByPhone(String phone);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZMthAccount selectById(@Param("id") Long id);


    /**
     * 更新数据
     * @param zMthAccount
     * @return
     */
    int update(ZMthAccount zMthAccount);

    /**
     * 添加收款账号
     * @param zMthAccount
     * @return
     */
    int insert(ZMthAccount zMthAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}
