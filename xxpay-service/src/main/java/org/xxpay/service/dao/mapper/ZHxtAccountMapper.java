package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZHxtAccount;

import java.util.List;

public interface ZHxtAccountMapper {


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

    /**
     * 更新数据
     * @param zHxtAccount
     * @return
     */
    int updateReceiveAccountByUpdateTime(@Param("zHxtAccount") ZHxtAccount zHxtAccount);









    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取当前惠生活的账号
     * @param zHxtAccount
     * @return
     */
    List<ZHxtAccount> selectList(@Param("zHxtAccount") ZHxtAccount zHxtAccount, @Param("page")int page, @Param("limit") int limit);

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
    ZHxtAccount selectByPhone(@Param("phone") String phone);

    /**
     * 通过手机号码查询是否存在重复
     * @param id
     * @return
     */
    ZHxtAccount selectById(@Param("id") Long id);

    /**
     * 更新数据
     * @param zHxtAccount
     * @return
     */
    int update(@Param("zHxtAccount") ZHxtAccount zHxtAccount);

    /**
     * 添加收款账号
     * @param zHxtAccount
     * @return
     */
    int insert(@Param("zHxtAccount") ZHxtAccount zHxtAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}

