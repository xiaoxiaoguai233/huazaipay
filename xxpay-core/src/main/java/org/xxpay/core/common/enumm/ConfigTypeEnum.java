package org.xxpay.core.common.enumm;

import sun.management.resources.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC调用返回码枚举类
 * 对应方法调用返回值中的rpcRetCode和rpcRetMsg
 * Created by admin on 2016/4/27.
 */
public enum ConfigTypeEnum {

    CONFIG_TYPE_GLOBLE("0", "全局"),
    CONFIG_TYPE_MAERCHANT("1", "商户");

    private String code;
    private String desc;

    private ConfigTypeEnum(String code, String desc) { this.code = code;
        this.desc = desc; }

    public String getCode()
    {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ConfigTypeEnum getRetEnum(String code) {
        if (code == null) {
            return null;
        }

        ConfigTypeEnum[] values = ConfigTypeEnum.values();
        for (ConfigTypeEnum e : values) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static Map<String,String> descMap() {
        return descMap;
    }
    public static Map<String,String> reportDescMap() {
        return reportDescMap;
    }

    /********/
    private static final Map<String,String> descMap = new HashMap<>(8);
    static{
        descMap.put(CONFIG_TYPE_GLOBLE.code, CONFIG_TYPE_GLOBLE.desc);
        descMap.put(CONFIG_TYPE_MAERCHANT.code, CONFIG_TYPE_MAERCHANT.desc);
    }

    /********/
    private static final Map<String,String> reportDescMap = new HashMap<>(8);
    static{
        reportDescMap.put(CONFIG_TYPE_GLOBLE.code, CONFIG_TYPE_GLOBLE.desc);
        reportDescMap.put(CONFIG_TYPE_MAERCHANT.code, CONFIG_TYPE_MAERCHANT.desc);
    }












}
