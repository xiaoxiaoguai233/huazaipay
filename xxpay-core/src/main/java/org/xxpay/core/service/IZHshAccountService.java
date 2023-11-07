package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZHshAccount;

import java.util.List;

public interface IZHshAccountService {


    /*****************************************************************
     *
     *                          支付业务
     *
     *****************************************************************/
    /**
     * 获取最优的收款账号
     * @return
     */
    ZHshAccount selectReceiveAccountByUpdateTime();












    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取惠生活的信息
     * @param zHshAccount
     * @return
     */
    List<ZHshAccount> selectList(ZHshAccount zHshAccount, int page, int limit);

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
    ZHshAccount selectByPhone(String phone);

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    ZHshAccount selectById(@Param("id") Long id);


    /**
     * 更新数据
     * @param zHshAccount
     * @return
     */
    int update(ZHshAccount zHshAccount);

    /**
     * 添加收款账号
     * @param zHshAccount
     * @return
     */
    int insert(ZHshAccount zHshAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}
