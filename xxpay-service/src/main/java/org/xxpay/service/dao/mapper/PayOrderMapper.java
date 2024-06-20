package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.common.vo.ManagerPayOrderVO;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayOrderExample;

import java.util.List;
import java.util.Map;

public interface PayOrderMapper {

    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param phone
     * @return
     */
    int updatePhoneAndChannelUserByOrderId(@Param("payOrderId") String payOrderId, @Param("phone") String phone, @Param("nickUser") String nickUser);
    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param token
     * @return
     */
    int updateTokenAndChannelUserByOrderId(@Param("payOrderId") String payOrderId, @Param("token") String token, @Param("nickUser") String nickUser);
    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param phone
     * @return
     */
    int updatePhoneAndChannelUserAndCardByOrderId(@Param("payOrderId") String payOrderId, @Param("phone") String phone, @Param("nickUser") String nickUser, @Param("card") String card);

    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param token
     * @return
     */
    int updateTokenAndChannelUserAndCardByOrderId(@Param("payOrderId") String payOrderId, @Param("token") String token, @Param("nickUser") String nickUser, @Param("card") String card);

    /**
     * 更新卡密
     * @param payOrderId
     * @param cardPwd
     * @return
     */
    int updateCardPwdByOrderId(@Param("payOrderId") String payOrderId, @Param("cardPwd") String cardPwd);



    int systeminfo();
    int systeminfo1();
    int systeminfo2();


    int countByExample(PayOrderExample example);

    int deleteByExample(PayOrderExample example);

    int deleteByPrimaryKey(String payOrderId);

    int insert(PayOrder record);

    int insertSelective(PayOrder record);

    List<PayOrder> selectByExample(PayOrderExample example);

    List<ManagerPayOrderVO> selectDownLoadOrder(PayOrderExample example);

    PayOrder selectByPrimaryKey(String payOrderId);

    int updateByExampleSelective(@Param("record") PayOrder record, @Param("example") PayOrderExample example);

    int updateByExample(@Param("record") PayOrder record, @Param("example") PayOrderExample example);

    int updateByPrimaryKeySelective(PayOrder record);

    int updateByPrimaryKey(PayOrder record);

    // 金额求和
    long sumAmountByExample(PayOrderExample example);

    /**
     * 统计收入情况
     * @param param
     * @return
     */
    Map count4Income(Map param);

    /**
     * 商户充值排行
     * @param param
     * @return
     */
    List<Map> count4MchTop(Map param);

    /**
     * 代理商充值排行
     * @param param
     * @return
     */
    /*List<Map> count4AgentTop(Map param);*/

    /**
     * 支付统计
     * @param param
     * @return
     */
    List<Map> count4Pay(Map param);

    /**
     * 支付产品统计
     * @param param
     * @return
     */
    List<Map> count4PayProduct(Map param);

    /**
     * 统计所有订单
     * @param param
     * @return
     */
    Map count4All(Map param);

    /**
     * 统计成功订单
     * @param param
     * @return
     */
    Map count4Success(Map param);

    /**
     * 统计未付订单
     * @param param
     * @return
     */
    Map count4Fail(Map param);

    List<Map> daySuccessRate(Map param);

    List<Map> hourSuccessRate(Map param);

    Map<String, Object> countDaySuccessRate(Map param);

    Map<String, Object> countHourSuccessRate(Map param);

    Map dateRate(Map param);

    Map hourRate(Map param);

    Map orderDayAmount(Map param);

    /**
     * 获取订单剩余支付时间
     * @param param
     * @return
     */
    Long getOrderTimeLeft(Map param);

    /**
     * 查询某范围的金额订单记录(订单状态为0或1,时间范围在超时时间内)
     * @param param
     * @return
     */
    List<Map> selectAmountRange(Map param);

    /**
     *
     * @param param
     * @return
     */
    List<PayOrder> selectByAmount(Map param);

}