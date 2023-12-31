package org.xxpay.core.service;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.common.vo.ManagerPayOrderVO;
import org.xxpay.core.entity.PayOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
public interface IPayOrderService {

    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param phone
     * @return
     */

    int updatePhoneAndChannelUserByOrderId(String payOrderId, String phone, String nickUser, String card);
    /**
     * 自定义订单更新手机号码
     * @param payOrderId
     * @param phone
     * @return
     */
    int updatePhoneAndChannelUserByOrderId(String payOrderId, String phone, String nickUser);


    /**
     * 更新卡密
     * @param payOrderId
     * @param cardPwd
     * @return
     */
    int updateCardPwdByOrderId(@Param("payOrderId") String payOrderId, @Param("cardPwd") String cardPwd);









    PayOrder find(PayOrder payOrder);

    PayOrder findByPayOrderId(String payOrderId);

    PayOrder findByMchIdAndPayOrderId(Long mchId, String payOrderId);

    PayOrder findByMchOrderNo(String mchOrderNo);

    List<PayOrder> select(Long mchId, int offset, int limit, PayOrder payOrder, Date createTimeStart, Date createTimeEnd);

    List<PayOrder> select(int offset, int limit, PayOrder payOrder, Date createTimeStart, Date createTimeEnd);

    Integer count(Long mchId, PayOrder payOrder, Date createTimeStart, Date createTimeEnd);

    Integer count(PayOrder payOrder, Date createTimeStart, Date createTimeEnd);

    Integer count(PayOrder payOrder, List<Byte> statusList);

    int updateByPayOrderId(String payOrderId, PayOrder payOrder);

    Long sumAmount(PayOrder payOrder, List<Byte> statusList);

    List<PayOrder> select(String channelMchId, String billDate, List<Byte> statusList);

    int updateStatus4Ing(String payOrderId, String channelOrderNo);

    int updateStatus4Ing(String payOrderId, String channelOrderNo, String channelAttach);

    int updateStatus4Success(String payOrderId);

    int updateStatus4Fail(String payOrderId);

    int updateStatus4Success(String payOrderId, String channelOrderNo);

    int updateStatus4Success(String payOrderId, String channelOrderNo, String channelAttach);

    int updateStatus4Complete(String payOrderId);

    int createPayOrder(PayOrder payOrder);

    PayOrder selectPayOrder(String payOrderId);

    PayOrder selectByMchIdAndPayOrderId(Long mchId, String payOrderId);

    PayOrder selectByMchIdAndMchOrderNo(Long mchId, String mchOrderNo);

    // 查询订单数据(用于生成商户对账文件使用)
    List<PayOrder> selectAllBill(int offset, int limit, String billDate);

    /**
     * 收入统计
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     */
    Map count4Income(Long agentId, Long mchId, Byte productType, String createTimeStart, String createTimeEnd);

    /**
     * 商户排名统计
     * @param agentId
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     */
    List<Map> count4MchTop(Long agentId, Long mchId, Byte productType, String createTimeStart, String createTimeEnd);

    /**
     * 代理商排名统计
     * @param createTimeStart
     * @param createTimeEnd
     * @param createTimeEnd2
     * @param createTimeStart2
     * @return
     */
    /*List<Map> count4AgentTop(String agentId, String bizType, String createTimeStart, String createTimeEnd);*/

    /**
     * 支付统计(idName可以为passageId,productId,channelType,channelId)
     * @param idName
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     */
    List<Map> count4Pay(String idName, String createTimeStart, String createTimeEnd);

    List<Map> count4PayProduct(String createTimeStart, String createTimeEnd);

    /**
     * 得到某个子账户的交易金额
     * @param payPassageAccountId
     * @param creatTimeStart
     * @param createTimeEnd
     * @return
     */
    Long sumAmount4PayPassageAccount(int payPassageAccountId, Date creatTimeStart, Date createTimeEnd);


    Map count4All(Long agentId, Long mchId, Long productId, String payOrderId, String mchOrderNo, Byte productType, String createTimeStart, String createTimeEnd,String channelId,String channelType,String subMchId, String phone, String cardPwd);

    Map count4Success(Long agentId, Long mchId, Long productId, String payOrderId, String mchOrderNo, Byte productType, String createTimeStart, String createTimeEnd,String channelId,String channelType,String subMchId, String phone, String cardPwd);

    Map count4Fail(Long agentId, Long mchId, Long productId, String payOrderId, String mchOrderNo, Byte productType, String createTimeStart, String createTimeEnd,String channelId,String channelType,String subMchId, String phone, String cardPwd);

    List<Map> daySuccessRate(int offset, int limit, String createTimeStart, String createTimeEnd, Long mchId);

    List<Map> hourSuccessRate(int offset, int limit, String createTimeStart, String createTimeEnd, Long mchId);

    Map<String, Object> countDaySuccessRate(String createTimeStart, String createTimeEnd, Long mchId);

    Map<String, Object> countHourSuccessRate(String createTimeStart, String createTimeEnd, Long mchId);

    Map dateRate(String dayStart, String dayEnd);

    Map hourRate(String dayStart, String dayEnd);

    Map orderDayAmount(Long mchId, String dayStart, String dayEnd);

    /**
     * 计算订单剩余支付时间
     * @param payOrderId
     * @param timeOut       订单超时时间，单位秒
     * @return
     */
    Long getOrderTimeLeft(String payOrderId, Long timeOut);

    /**
     * 获取可用金额(保证金额在支付超时时间内不重复)
     * @param payOrder
     * @return
     */
    Long getAvailableAmount(PayOrder payOrder, Long payTimeOut, Long incrRange, Long incrStep);

    /**
     * 根据金额查找唯一订单(在超时时间内，金额一致，卡号后四位一致，且只能有一个订单)
     * @param amount
     * @param rightCardNo
     * @param payTimeOut
     * @return
     */
    PayOrder findByAmount(Long amount, String rightCardNo, Long payTimeOut);

    /**
     * 导出运营商订单
     * @param payOrder
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     */
    List<ManagerPayOrderVO> downOrder(Long mchId,PayOrder payOrder, Date createTimeStart, Date createTimeEnd);

}
