package org.xxpay.core.service;

import org.xxpay.core.entity.PayPassage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 18/5/3
 * @description: 支付通道
 */
public interface IPayPassageService {

    int add(PayPassage payPassage);

    int update(PayPassage payPassage);

    int updateRate(PayPassage payPassage);

    PayPassage findById(Integer id);

    List<PayPassage> select(int offset, int limit, PayPassage payPassage);

    Integer count(PayPassage payPassage);

    List<PayPassage> selectAll(PayPassage payPassage);

    /**
     * 根据支付类型查询所有支付通道列表
     * @param payType
     * @return
     */
    List<PayPassage> selectAllByPayType(String payType ,Byte status) ;

    List<PayPassage> selectAllByIdsAndTimeAvailable(ArrayList<Integer> ids,Byte status);

    PayPassage selectByIdAndStatus(Integer payPassageId, Byte status);

    /**
     * 根据passageName 获取到 PayPassage
     * @param passageName
     * @return
     */
    PayPassage getChannelIdByName(String passageName);
}
