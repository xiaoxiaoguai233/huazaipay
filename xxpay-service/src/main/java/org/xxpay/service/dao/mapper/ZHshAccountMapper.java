package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.ZHshAccount;

import java.util.List;

public interface ZHshAccountMapper {


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

    /**
     * 更新数据
     * @param zHshAccount
     * @return
     */
    int updateReceiveAccountByUpdateTime(@Param("zHshAccount") ZHshAccount zHshAccount);











    /*****************************************************************
     *
     *                          常规业务
     *
     *****************************************************************/
    /**
     * 获取当前惠生活的账号
     * @param zHshAccount
     * @return
     */
    List<ZHshAccount> selectList(@Param("zHshAccount") ZHshAccount zHshAccount, @Param("page")int page, @Param("limit") int limit);

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
    ZHshAccount selectByPhone(@Param("phone") String phone);

    /**
     * 通过手机号码查询是否存在重复
     * @param id
     * @return
     */
    ZHshAccount selectById(@Param("id") Long id);

    /**
     * 更新数据
     * @param zHshAccount
     * @return
     */
    int update(@Param("zHshAccount") ZHshAccount zHshAccount);

    /**
     * 添加收款账号
     * @param zHshAccount
     * @return
     */
    int insert(@Param("zHshAccount") ZHshAccount zHshAccount);

    /**
     * 通过ID删除单条数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}
