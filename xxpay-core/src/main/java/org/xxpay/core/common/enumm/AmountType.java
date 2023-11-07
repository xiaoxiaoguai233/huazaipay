package org.xxpay.core.common.enumm;

public enum AmountType {
    CONTINUERS_AMOUNT(1, "连续任意金额"),
    NOTTENS_AMOUNT(2, "非整十金额"),
    FIX_AMOUNT(3, "固定金额"),
    TENS_AMOUNT(4, "10的倍数"),
    HUNDRED_AMOUNT(5, "100的倍数");

    private Integer id;
    private String name;

    AmountType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AmountType getById(Integer id) {
        AmountType[] values = AmountType.values();
        for (AmountType e : values) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }


}