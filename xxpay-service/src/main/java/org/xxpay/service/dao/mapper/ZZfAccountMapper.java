package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZZfAccount;

import java.util.List;

public interface ZZfAccountMapper {


    /*****************************************************************
     *
     *                          支付业务
     *
     *****************************************************************/
    /**
     * 获取最优的收款账号
     * @return
     */
    ZZfAccount selectReceiveAccountByUpdateTime();

    /**
     * 更新数据
     * @param zZfAccount
     * @return
     */
    int updateReceiveAccountByUpdateTime(@Param("zZfAccount") ZZfAccount zZfAccount);










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
    List<ZZfAccount> selectList(@Param("zZfAccount") ZZfAccount zMthAccount, @Param("page")int page, @Param("limit") int limit);

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
    ZZfAccount selectByPhone(@Param("phone") String phone);

    /**
     * 通过手机号码查询是否存在重复
     * @param token
     * @return
     */
    ZZfAccount selectByToken(@Param("token") String token);

    /**
     * 通过手机号码查询是否存在重复
     * @param id
     * @return
     */
    ZZfAccount selectById(@Param("id") Long id);

    /**
     * 更新数据
     * @param zZfAccount
     * @return
     */
    int update(@Param("zZfAccount") ZZfAccount zZfAccount);

    /**
     * 添加收款账号
     * @param zZfAccount
     * @return
     */
    int insert(@Param("zZfAccount") ZZfAccount zZfAccount);


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

