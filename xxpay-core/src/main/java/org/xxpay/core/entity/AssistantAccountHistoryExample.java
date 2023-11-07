package org.xxpay.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssistantAccountHistoryExample
        implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<AssistantAccountHistoryExample.Criteria> oredCriteria;

    private static final long serialVersionUID = 1L;

    private Integer limit;

    private Integer offset;

    public AssistantAccountHistoryExample() {
        oredCriteria = new ArrayList<AssistantAccountHistoryExample.Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<AssistantAccountHistoryExample.Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(AssistantAccountHistoryExample.Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public AssistantAccountHistoryExample.Criteria or() {
        AssistantAccountHistoryExample.Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public AssistantAccountHistoryExample.Criteria createCriteria() {
        AssistantAccountHistoryExample.Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected AssistantAccountHistoryExample.Criteria createCriteriaInternal() {
        AssistantAccountHistoryExample.Criteria criteria = new AssistantAccountHistoryExample.Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria implements Serializable {
        protected List<AssistantAccountHistoryExample.Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<AssistantAccountHistoryExample.Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<AssistantAccountHistoryExample.Criterion> getAllCriteria() {
            return criteria;
        }

        public List<AssistantAccountHistoryExample.Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new AssistantAccountHistoryExample.Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new AssistantAccountHistoryExample.Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new AssistantAccountHistoryExample.Criterion(condition, value1, value2));
        }

        public AssistantAccountHistoryExample.Criteria andIdIsNull() {
            addCriterion("Id is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdIsNotNull() {
            addCriterion("Id is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdEqualTo(Long value) {
            addCriterion("Id =", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdNotEqualTo(Long value) {
            addCriterion("Id <>", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdGreaterThan(Long value) {
            addCriterion("Id >", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("Id >=", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdLessThan(Long value) {
            addCriterion("Id <", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("Id <=", value, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdIn(List<Long> values) {
            addCriterion("Id in", values, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdNotIn(List<Long> values) {
            addCriterion("Id not in", values, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("Id between", value1, value2, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("Id not between", value1, value2, "id");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdIsNull() {
            addCriterion("AssistantId is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdIsNotNull() {
            addCriterion("AssistantId is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdEqualTo(Long value) {
            addCriterion("AssistantId =", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdNotEqualTo(Long value) {
            addCriterion("AssistantId <>", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdGreaterThan(Long value) {
            addCriterion("AssistantId >", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdGreaterThanOrEqualTo(Long value) {
            addCriterion("AssistantId >=", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdLessThan(Long value) {
            addCriterion("AssistantId <", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdLessThanOrEqualTo(Long value) {
            addCriterion("AssistantId <=", value, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdIn(List<Long> values) {
            addCriterion("AssistantId in", values, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdNotIn(List<Long> values) {
            addCriterion("AssistantId not in", values, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdBetween(Long value1, Long value2) {
            addCriterion("AssistantId between", value1, value2, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAssistantIdNotBetween(Long value1, Long value2) {
            addCriterion("AssistantId not between", value1, value2, "assistantId");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountIsNull() {
            addCriterion("Amount is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountIsNotNull() {
            addCriterion("Amount is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountEqualTo(Long value) {
            addCriterion("Amount =", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountNotEqualTo(Long value) {
            addCriterion("Amount <>", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountGreaterThan(Long value) {
            addCriterion("Amount >", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("Amount >=", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountLessThan(Long value) {
            addCriterion("Amount <", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountLessThanOrEqualTo(Long value) {
            addCriterion("Amount <=", value, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountIn(List<Long> values) {
            addCriterion("Amount in", values, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountNotIn(List<Long> values) {
            addCriterion("Amount not in", values, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountBetween(Long value1, Long value2) {
            addCriterion("Amount between", value1, value2, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAmountNotBetween(Long value1, Long value2) {
            addCriterion("Amount not between", value1, value2, "amount");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceIsNull() {
            addCriterion("Balance is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceIsNotNull() {
            addCriterion("Balance is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceEqualTo(Long value) {
            addCriterion("Balance =", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceNotEqualTo(Long value) {
            addCriterion("Balance <>", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceGreaterThan(Long value) {
            addCriterion("Balance >", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceGreaterThanOrEqualTo(Long value) {
            addCriterion("Balance >=", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceLessThan(Long value) {
            addCriterion("Balance <", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceLessThanOrEqualTo(Long value) {
            addCriterion("Balance <=", value, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceIn(List<Long> values) {
            addCriterion("Balance in", values, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceNotIn(List<Long> values) {
            addCriterion("Balance not in", values, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceBetween(Long value1, Long value2) {
            addCriterion("Balance between", value1, value2, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBalanceNotBetween(Long value1, Long value2) {
            addCriterion("Balance not between", value1, value2, "balance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionIsNull() {
            addCriterion("FundDirection is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionIsNotNull() {
            addCriterion("FundDirection is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionEqualTo(Byte value) {
            addCriterion("FundDirection =", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionNotEqualTo(Byte value) {
            addCriterion("FundDirection <>", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionGreaterThan(Byte value) {
            addCriterion("FundDirection >", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionGreaterThanOrEqualTo(Byte value) {
            addCriterion("FundDirection >=", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionLessThan(Byte value) {
            addCriterion("FundDirection <", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionLessThanOrEqualTo(Byte value) {
            addCriterion("FundDirection <=", value, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionIn(List<Byte> values) {
            addCriterion("FundDirection in", values, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionNotIn(List<Byte> values) {
            addCriterion("FundDirection not in", values, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionBetween(Byte value1, Byte value2) {
            addCriterion("FundDirection between", value1, value2, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andFundDirectionNotBetween(Byte value1, Byte value2) {
            addCriterion("FundDirection not between", value1, value2, "fundDirection");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeIsNull() {
            addCriterion("BizType is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeIsNotNull() {
            addCriterion("BizType is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeEqualTo(Byte value) {
            addCriterion("BizType =", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeNotEqualTo(Byte value) {
            addCriterion("BizType <>", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeGreaterThan(Byte value) {
            addCriterion("BizType >", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("BizType >=", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeLessThan(Byte value) {
            addCriterion("BizType <", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeLessThanOrEqualTo(Byte value) {
            addCriterion("BizType <=", value, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeIn(List<Byte> values) {
            addCriterion("BizType in", values, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeNotIn(List<Byte> values) {
            addCriterion("BizType not in", values, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeBetween(Byte value1, Byte value2) {
            addCriterion("BizType between", value1, value2, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("BizType not between", value1, value2, "bizType");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeIsNull() {
            addCriterion("CreateTime is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeIsNotNull() {
            addCriterion("CreateTime is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("CreateTime =", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("CreateTime <>", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("CreateTime >", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("CreateTime >=", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeLessThan(Date value) {
            addCriterion("CreateTime <", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("CreateTime <=", value, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("CreateTime in", values, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("CreateTime not in", values, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("CreateTime between", value1, value2, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("CreateTime not between", value1, value2, "createTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeIsNull() {
            addCriterion("UpdateTime is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeIsNotNull() {
            addCriterion("UpdateTime is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("UpdateTime =", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("UpdateTime <>", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("UpdateTime >", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("UpdateTime >=", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("UpdateTime <", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("UpdateTime <=", value, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("UpdateTime in", values, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("UpdateTime not in", values, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("UpdateTime between", value1, value2, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("UpdateTime not between", value1, value2, "updateTime");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceIsNull() {
            addCriterion("AfterBalance is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceIsNotNull() {
            addCriterion("AfterBalance is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceEqualTo(Long value) {
            addCriterion("AfterBalance =", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceNotEqualTo(Long value) {
            addCriterion("AfterBalance <>", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceGreaterThan(Long value) {
            addCriterion("AfterBalance >", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceGreaterThanOrEqualTo(Long value) {
            addCriterion("AfterBalance >=", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceLessThan(Long value) {
            addCriterion("AfterBalance <", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceLessThanOrEqualTo(Long value) {
            addCriterion("AfterBalance <=", value, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceIn(List<Long> values) {
            addCriterion("AfterBalance in", values, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceNotIn(List<Long> values) {
            addCriterion("AfterBalance not in", values, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceBetween(Long value1, Long value2) {
            addCriterion("AfterBalance between", value1, value2, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andAfterBalanceNotBetween(Long value1, Long value2) {
            addCriterion("AfterBalance not between", value1, value2, "afterBalance");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemIsNull() {
            addCriterion("BizItem is null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemIsNotNull() {
            addCriterion("BizItem is not null");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemEqualTo(String value) {
            addCriterion("BizItem =", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemNotEqualTo(String value) {
            addCriterion("BizItem <>", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemGreaterThan(String value) {
            addCriterion("BizItem >", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemGreaterThanOrEqualTo(String value) {
            addCriterion("BizItem >=", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemLessThan(String value) {
            addCriterion("BizItem <", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemLessThanOrEqualTo(String value) {
            addCriterion("BizItem <=", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemLike(String value) {
            addCriterion("BizItem like", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemNotLike(String value) {
            addCriterion("BizItem not like", value, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemIn(List<String> values) {
            addCriterion("BizItem in", values, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemNotIn(List<String> values) {
            addCriterion("BizItem not in", values, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemBetween(String value1, String value2) {
            addCriterion("BizItem between", value1, value2, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }

        public AssistantAccountHistoryExample.Criteria andBizItemNotBetween(String value1, String value2) {
            addCriterion("BizItem not between", value1, value2, "bizItem");
            return (AssistantAccountHistoryExample.Criteria) this;
        }
    }

    public static class Criteria extends AssistantAccountHistoryExample.GeneratedCriteria implements Serializable {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion implements Serializable {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }

}
