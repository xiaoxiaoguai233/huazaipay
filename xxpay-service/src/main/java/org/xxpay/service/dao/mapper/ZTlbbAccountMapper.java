package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZTlbbAccount;

import java.util.List;

public interface ZTlbbAccountMapper {


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

    /**
     * 更新数据
     * @param zTlbbAccount
     * @return
     */
    int updateReceiveAccountByUpdateTime(@Param("zTlbbAccount") ZTlbbAccount zTlbbAccount);










    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取当前惠生活的账号
     * @param zMthAccount
     * @return
     */
    List<ZTlbbAccount> selectList(@Param("zTlbbAccount") ZTlbbAccount zMthAccount, @Param("page")int page, @Param("limit") int limit);

    /**
     * 计算全数
     * @param parentId
     * @return
     */
    int selectListCount(@Param("parentId") Long parentId);

    /**
     * 通过手机号码查询是否存在重复
     * @param phone
     * @return
     */
    ZTlbbAccount selectByPhone(@Param("phone") String phone);

    /**
     * 通过手机号码查询是否存在重复
     * @param token
     * @return
     */
    ZTlbbAccount selectByToken(@Param("token") String token);

    /**
     * 通过手机号码查询是否存在重复
     * @param id
     * @return
     */
    ZTlbbAccount selectById(@Param("id") Long id);

    /**
     * 更新数据
     * @param zTlbbAccount
     * @return
     */
    int update(@Param("zTlbbAccount") ZTlbbAccount zTlbbAccount);

    /**
     * 添加收款账号
     * @param zTlbbAccount
     * @return
     */
    int insert(@Param("zTlbbAccount") ZTlbbAccount zTlbbAccount);


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



