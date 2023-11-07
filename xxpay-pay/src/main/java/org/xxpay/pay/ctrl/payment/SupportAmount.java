package org.xxpay.pay.ctrl.payment;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


class SupportAmount {
    String msg;
    Long max;
    Long min;
    List<Long> fixAmount;
    HashMap<String, List<CashierWebController.AmountMaxMinType>> amountType;

    public HashMap<String, List<CashierWebController.AmountMaxMinType>> getAmountType() {
        return amountType;
    }

    public void setAmountType(HashMap<String, List<CashierWebController.AmountMaxMinType>> amountType) {
        this.amountType = amountType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public void setFixAmount(List<Long> fixAmount) {
        this.fixAmount = fixAmount;
    }

    SupportAmount() {
        this.max = Long.MIN_VALUE;
        this.min = Long.MAX_VALUE;
        this.fixAmount = Collections.EMPTY_LIST;
    }

    SupportAmount(Long max, Long min, List<Long> fixAmount) {
        this.max = max;
        this.min = min;
        this.fixAmount = fixAmount;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    public List<Long> getFixAmount() {
        return fixAmount;
    }

    @Override
    public String toString() {
        String result = "最小充值金额:" + min + "\r\n最大充值金额：" + max;
        if (!fixAmount.isEmpty()) {
            result += "\r\n或者其他固定金额：" + fixAmount.toString();
        }
        return result;
    }

    public static void main(String[] args) {
        List<Long> s = new ArrayList<>();
        System.out.println(JSONObject.toJSONString(new SupportAmount(3L, 4L, s)));
    }
}
