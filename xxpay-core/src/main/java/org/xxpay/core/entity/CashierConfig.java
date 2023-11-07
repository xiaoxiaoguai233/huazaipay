package org.xxpay.core.entity;

import java.io.Serializable;

public class CashierConfig implements Serializable {
    /**
     * 收银台配置ID
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 收银台名称
     *
     * @mbggenerated
     */
    private String name;

    /**
     * logo url地址
     *
     * @mbggenerated
     */
    private String logo;

    /**
     * 收银台开关,1-打开,2-关闭
     *
     * @mbggenerated
     */
    private Byte status;

    /**
     * 回调URL
     *
     * @mbggenerated
     */
    private String callbackUrl;

    /**
     * 前台跳转URL
     *
     * @mbggenerated
     */
    private String returnUrl;

    /**
     * 商户 id
     *
     * @mbggenerated
     */
    private Long merchantid;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public Long getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(Long merchantid) {
        this.merchantid = merchantid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", logo=").append(logo);
        sb.append(", status=").append(status);
        sb.append(", callbackUrl=").append(callbackUrl);
        sb.append(", returnUrl=").append(returnUrl);
        sb.append(", merchantid=").append(merchantid);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CashierConfig other = (CashierConfig) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getLogo() == null ? other.getLogo() == null : this.getLogo().equals(other.getLogo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCallbackUrl() == null ? other.getCallbackUrl() == null : this.getCallbackUrl().equals(other.getCallbackUrl()))
            && (this.getReturnUrl() == null ? other.getReturnUrl() == null : this.getReturnUrl().equals(other.getReturnUrl()))
            && (this.getMerchantid() == null ? other.getMerchantid() == null : this.getMerchantid().equals(other.getMerchantid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getLogo() == null) ? 0 : getLogo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCallbackUrl() == null) ? 0 : getCallbackUrl().hashCode());
        result = prime * result + ((getReturnUrl() == null) ? 0 : getReturnUrl().hashCode());
        result = prime * result + ((getMerchantid() == null) ? 0 : getMerchantid().hashCode());
        return result;
    }
}