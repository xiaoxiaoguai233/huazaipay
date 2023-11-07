package org.xxpay.core.common.constant;

/**
 * 返回码枚举类
 */
public enum RetEnum {

    // 公共返回码10开头
    RET_COMM_SUCCESS(0, "success"),
    RET_COMM_PARAM_ERROR(10001, "参数错误"),
    RET_COMM_PARAM_NOT_FOUND(10002, "缺少参数"),
    RET_COMM_OPERATION_FAIL(10003, "操作失败"),
    RET_COMM_RECORD_NOT_EXIST(10004, "记录不存在"),
    RET_COMM_UNKNOWN_ERROR(10999, "未知错误"),

    // 业务中心返回码11开头
    RET_SERVICE_MCH_NOT_EXIST(11000, "商户不存在"),
    RET_SERVICE_ACCOUNT_NOT_EXIST(11001, "账户不存在"),
    RET_SERVICE_BANK_ACCOUNT_NOT_EXIST(11002, "银行账号不存在"),
    RET_SERVICE_SETT_AMOUNT_NOT_SETT(11003, "可用余额不足"),
    RET_SERVICE_FREEZE_AMOUNT_OUT_LIMIT(11004, "冻结金额超限"),
    RET_SERVICE_ACCOUNT_FROZEN_AMOUNT_FAIL(11005, "冻结金额失败"),
    RET_SERVICE_UN_FREEZE_AMOUNT_OUT_LIMIT(11006, "解冻金额超限"),
    RET_SERVICE_ACCOUNT_AMOUNT_UPDATE_FAIL(11007, "更新账户金额失败"),
    RET_SERVICE_ACCOUNT_BALANCE_OUT_LIMIT(11008, "余额不足,减款超限"),
    RET_SERVICE_SETT_RECORD_NOT_EXIST(11009, "结算记录不存在"),
    RET_SERVICE_SETT_STATUS_ERROR(11010, "结算状态错误"),
    RET_SERVICE_AMOUNT_ERROR(11011, "金额不符"),
    RET_SERVICE_DRAW_AMOUNT_MIN_LIMIT(11012, "金额超过最小限制"),
    RET_SERVICE_DRAW_AMOUNT_MAX_LIMIT(11013, "金额超过最大限制"),
    RET_SERVICE_DRAW_TIMES_LIMIT(11014, "超过当日提现次数"),
    RET_SERVICE_DRAW_TIME_LIMIT(11014, "不在可提现时间范围内"),
    RET_SERVICE_DRAW_DAY_NOT(11015, "当天不允许提现"),
    RET_SERVICE_DRAW_CLOSE(11016, "系统当前关闭提现"),
    RET_SERVICE_DRAW_DAY_AMOUNT_MAX_LIMIT(11017, "超过当日可提现最大金额"),
    RET_SERVICE_AGENTPAY_OUT_ERROR(11018, "没有配置代付出款账户类型"),
    RET_SERVICE_ACCOUNT_TYPE_NOT_EXIST(11019, "不存在的账户类型"),
    RET_SERVICE_CASHIER_CONFIG_NOT_EXIST(11020, "收银台没有配置"),
    RET_SERVICE_CASHIER_CONFIG_NOT_OPEN(11021, "收银台未开启"),
    RET_SERVICE_REPEAT_PHONE(11022, "添加的手机号码重复"),
    RET_SERVICE_Error_PHONE_And_SMS(11023, "手机号或验证码填写有误，请检查后重试"),

    // 商户系统返回码12开头
    RET_MCH_PERMISSION_ERROR(12001, "权限错误"),
    RET_MCH_PASSWORD_NOT_MATCH(12002, "密码不符"),
    RET_MCH_STATUS_AUDIT_ING(12003, "商户审核中"),
    RET_MCH_STATUS_STOP(12004, "商户已停止使用"),
    RET_MCH_MOBILE_USED(12005, "手机号已被使用"),
    RET_MCH_MOBILE_FORMAT_ERROR(12006, "手机号格式错误"),
    RET_MCH_EMAIL_USED(12007, "邮箱已被使用"),
    RET_MCH_EMAIL_FORMAT_ERROR(12008, "邮箱格式错误"),
    RET_MCH_REGISTER_FAIL(12009, "注册失败"),
    RET_MCH_PASSWORD_FORMAT_FAIL(12010, "密码格式不符,8-16位字母和数字组合,且至少2位数字"),
    RET_MCH_AUTH_FAIL(12011, "鉴权失败"),
    RET_MCH_CHANNEL_NOT_EXIST(12012, "渠道不存在"),
    RET_MCH_QR_CODE_STOP(12013, "二维码已停止使用"),
    RET_MCH_UA_NOT_SUPPORT(12014, "不支持当前客户端"),
    RET_MCH_UA_NOT_EXIST(12015, "无法识别的客户端"),
    RET_MCH_CONFIG_NOT_EXIST(12016, "商户相关配置不存在"),
    RET_MCH_STATUS_CLOSE(12017, "商户相关状态已关闭"),
    RET_MCH_QR_CHANNEL_NOT_EXIST(12018, "商户扫码支付渠道没有配置"),
    RET_MCH_QR_UA_NOT_CONFIG(12019, "商户没有配置当前扫码支付渠道"),
    RET_MCH_CREATE_TRADE_ORDER_FAIL(12020, "创建交易订单失败"),
    RET_MCH_CREATE_PAY_ORDER_FAIL(12021, "创建支付订单失败"),
    RET_MCH_WX_OPENID_NOT_EXIST(12022, "缺少OPENID参数"),
    RET_MCH_UPDATE_TRADE_ORDER_FAIL(12023, "更新交易订单失败"),
    RET_MCH_GET_WX_OPENID_FAIL(12024, "获取微信OpenId异常"),
    RET_MCH_TRADE_ORDER_NOT_EXIST(12025, "交易订单不存在"),
    RET_MCH_PASSAGE_NOT_EXIST(12026, "商户没有对应支付通道"),
    RET_MCH_MOBILE_SEND_TOO_MUCH(12027, "手机发送次数超限"),
    RET_MCH_MOBILE_SEND_ERROR(12028, "手机发送短信失败"),
    RET_MCH_SMS_VERIFY_FAIL(12029, "短信验证失败"),
    RET_MCH_SMS_VERIFY_OVER_TIME(12030, "短信验证超时"),
    RET_MCH_SETT_BATCH_APPLY_AMOUNT(12031, "申请金额与文件中总金额不一致"),
    RET_MCH_SETT_BATCH_APPLY_EMPTY(12032, "文件中账户记录为空"),
    RET_MCH_SETT_BATCH_APPLY_FORMAT_ERROR(12033, "文件内容错误"),
    RET_MCH_SETT_BATCH_APPLY_LIMIT(12034, "超出最大上传记录"),
    RET_MCH_PAP_PASSWORD_NOT_MATCH(12035, "支付密码错误"),
    RET_MCH_OLDPASSWORD_NOT_MATCH(12036, "旧密码不正确"),
    RET_MCH_PASSAGEID_NOT_EXIST(12037, "缺少PASSGEID参数"),
    RET_MCH_SETT_BATCH_APPLY_NUMBER(12038, "申请笔数与文件笔数不一致"),
    RET_MCH_ILLEGAL_LOGIN(12039, "非法登录"),
    RET_MCH_AGENTPAY_PASSAGE_NOT_EXIST(12040, "商户没有可用的代付通道"),
    RET_MCH_AGENTPAY_FEE_ERROR(12041, "商户代付手续费错误"),
    RET_MCH_AGENTPAY_AMOUNT_ERROR(12042, "代付金额不足扣手续费"),
    RET_MCH_PAY_PRODUCT_NOT_EXIST(12043, "商户支付产品不存在"),
    RET_MCH_PAY_ORDER_EXIST(12044, "商户订单已存在,请重新下单"),
    RET_MCH_BANK_ACCOUNTNO_USED(12045, "银行账号已被使用"),
    RET_MCH_TAG_USED(12046, "商户唯一标记已被使用"),
    RET_MCH_GOOGLEAUTH_SECRETKEY_NOT_EXIST(12047, "谷歌验证密钥不存在"),
    RET_MCH_GOOGLEAUTH_SECRETKEY_BIND_FAIL(12048, "谷歌验证绑定失败"),
    RET_MCH_GOOGLECODE_NOT_MATCH(12049, "谷歌验证码不正确"),
    RET_MCH_GOOGLEAUTH_NOT_BIND(12050, "没有绑定谷歌验证"),
    RET_MCH_AGENTPAY_AMOUNT_MAX_LIMIT(12050, "代付金额超过单笔最大限制"),
    RET_MCH_USERNAME_USED(12051, "用户名已被使用"),
    RET_MCH_AGENTPAY_ACCOUNTNO_REPEAT(12051, "代付申请有重复记录"),
    RET_MCH_AGENTPAY_INTERVAL_SHORT(12052, "代付申请过于频繁"),
    RET_MCH_AGENTPAY_NUM_NOT_EQUAL(12053, "代付申请笔数不一致"),
    RET_MCH_IP_NOT_LOGIN(12054, "当前IP不允许登录"),
    RET_MCH_PARENTAGENTID_NOT_EXIST(12055, "输入的一级代理ID不存在"),

    // 运营系统返回码13开头
    RET_MGR_STATUS_ERROR(13001, "状态错误"),
    RET_MGR_LOGIN_FAIL(13002, "登录失败"),
    RET_MGR_USER_STOP(13003, "用户禁止使用"),
    RET_MGR_PAY_INTERFACE_NOT_EXIST(13005, "支付接口不存在"),
    RET_MGR_PAY_PASSAGE_NOT_EXIST(13006, "支付通道不存在"),
    RET_MGR_PAY_PRODUCT_NOT_EXIST(13007, "支付产品不存在"),
    RET_MGR_PAY_PASSAGE_ACCOUNT_NOT_EXIST(13008, "支付通道账户不存在"),
    RET_MGR_BUILD_NOTIFY_URL_ERROR(13009, "创建通知URL异常"),
    RET_MGR_SUPER_PASSWORD_NOT_MATCH(13010, "超级密码不正确"),
    RET_MGR_AGENTPAY_PASSAGE_NOT_EXIST(13011, "代付通道不存在或不可用"),
    RET_MGR_AGENTPAY_PASSAGE_ACCOUNT_NOT_EXIST(13012, "代付通道账户不存在或不可用"),
    RET_MGR_USERNAME_USED(13013, "用户名已被使用"),
    RET_MGR_TIME_DIFF(13014, "时间差不得大于24小时"),
    RET_MGR_CASH_COLL_LT_MAX_PERCENTAGE(13015, "分账总比例不可大于100%"),
    RET_MGR_CASH_COLL_LE_ZERO(13016, "分账比例不能小于等于0"),
    RET_MGR_CASH_COLL_PID_EXISTS(13017, "该分账账户已存在，请修改"),
    RET_MGR_CASH_COLL_IS_NOT_ALIPAY(13018, "该支付通道不可配置分账记录"),
    RET_MGR_NOT_HANDLE(13019, "当前状态无需处理"),
    RET_MGR_MCH_CONFIG_EXISTS(13020, "收银台限额配置已存在"),

    RET_MGR_WX_ACCOUNT_EXIST(13100, "微信账号已存在"),
    RET_MGR_WX_USER_NOT_EXIST(13101, "微信用户不存在"),
    RET_MGR_WX_USER_LOGIN_FAIL(13102, "微信用户登录失败"),
    RET_MGR_WX_USER_LOGOUT_FAIL(13103, "微信用户退出失败"),
    RET_MGR_WX_API_RETURN_FAIL(13104, "调用微信接口返回错误"),
    RET_MGR_WX_USER_NOT_LOGIN(13105, "微信用户没有登录"),

    // 代理商系统返回码14开头
    RET_SERVICE_AGENT_NOT_EXIST(14000, "代理商不存在"),
    RET_AGENT_STATUS_STOP(14001, "代理商已停止使用"),
    RET_AGENT_USERNAME_USED(14003, "用户名已被使用"),
    RET_AGENT_NOT_MCH_PERMISSION(14004, "没有该商户权限"),
    RET_AGENT_ACCOUNT_NOT_EXIST(14004, "代理商账户不存在"),
    RET_AGENT_AGENTRATE_SET_MIRROR(14005, "商户代理费率应大于总代理商费率"),
    RET_PARENTAGENTRATE_NOT_EXIST(14007, "请先设置总代理商费率"),




    // 码商系统返回码15开头
    RET_SERVICE_ASST_NOT_EXIST(15000, "码商不存在"),
    RET_ASST_STATUS_STOP(15001, "码商已停止使用"),
    RET_ASST_USERNAME_USED(15003, "用户名已被使用"),
    RET_ASST_NOT_MCH_PERMISSION(15004, "没有该商户权限"),
    RET_ASST_ACCOUNT_NOT_EXIST(15004, "码商账户不存在"),
    RET_ASST_ASSTRATE_SET_MIRROR(15005, "商户代理费率应大于总码商费率"),
    RET_PARENTASSTRATE_NOT_EXIST(15007, "请先设置总码商费率");
    
    


    private int code;
    private String message;

    RetEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
