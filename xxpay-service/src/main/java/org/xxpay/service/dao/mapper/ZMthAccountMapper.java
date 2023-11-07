package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZMthAccount;

import java.util.List;

public interface ZMthAccountMapper {


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

    /**
     * 更新数据
     * @param zMthAccount
     * @return
     */
    int updateReceiveAccountByUpdateTime(@Param("zMthAccount") ZMthAccount zMthAccount);











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
    List<ZMthAccount> selectList(@Param("zMthAccount") ZMthAccount zMthAccount, @Param("page")int page, @Param("limit") int limit);

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
    ZMthAccount selectByPhone(@Param("phone") String phone);

    /**
     * 通过手机号码查询是否存在重复
     * @param id
     * @return
     */
    ZMthAccount selectById(@Param("id") Long id);

    /**
     * 更新数据
     * @param zMthAccount
     * @return
     */
    int update(@Param("zMthAccount") ZMthAccount zMthAccount);

    /**
     * 添加收款账号
     * @param zMthAccount
     * @return
     */
    int insert(@Param("zMthAccount") ZMthAccount zMthAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}

