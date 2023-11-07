package org.xxpay.core.common.domain;

import org.xxpay.core.entity.PayPassageAccount;

public class AvailablePassageVO {
    private String msg;
    private Integer code;
    private PayPassageAccount ppa;

    public static AvailablePassageVO success(PayPassageAccount ppa) {
        AvailablePassageVO availablePassageVO = new AvailablePassageVO();
        availablePassageVO.code = 1;
        availablePassageVO.msg = "有可用通道账号";
        availablePassageVO.ppa = ppa;
        return availablePassageVO;
    }

    public static AvailablePassageVO fail(String msg) {
        AvailablePassageVO availablePassageVO = new AvailablePassageVO();
        availablePassageVO.code = 0;
        availablePassageVO.msg = msg;
        return availablePassageVO;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public PayPassageAccount getPpa() {
        return ppa;
    }

    public void setPpa(PayPassageAccount ppa) {
        this.ppa = ppa;
    }
}
