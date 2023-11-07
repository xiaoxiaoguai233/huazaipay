package org.xxpay.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssistantAccountExample  implements Serializable {

    protected String orderByClause;

    protected boolean distinct;

    protected List<AssistantAccountExample.Criteria> oredCriteria;

    private static final long serialVersionUID = 1L;

    private Integer limit;

    private Integer offset;

    public AssistantAccountExample() {
        oredCriteria = new ArrayList<AssistantAccountExample.Criteria>();
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

    public List<AssistantAccountExample.Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(AssistantAccountExample.Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public AssistantAccountExample.Criteria or() {
        AssistantAccountExample.Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public AssistantAccountExample.Criteria createCriteria() {
        AssistantAccountExample.Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected AssistantAccountExample.Criteria createCriteriaInternal() {
        AssistantAccountExample.Criteria criteria = new AssistantAccountExample.Criteria();
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
        protected List<AssistantAccountExample.Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<AssistantAccountExample.Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<AssistantAccountExample.Criterion> getAllCriteria() {
            return criteria;
        }

        public List<AssistantAccountExample.Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new AssistantAccountExample.Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new AssistantAccountExample.Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new AssistantAccountExample.Criterion(condition, value1, value2));
        }

        public AssistantAccountExample.Criteria andAssistantIdIsNull() {
            addCriterion("AssistantId is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdIsNotNull() {
            addCriterion("AssistantId is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdEqualTo(Long value) {
            addCriterion("AssistantId =", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdNotEqualTo(Long value) {
            addCriterion("AssistantId <>", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdGreaterThan(Long value) {
            addCriterion("AssistantId >", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdGreaterThanOrEqualTo(Long value) {
            addCriterion("AssistantId >=", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdLessThan(Long value) {
            addCriterion("AssistantId <", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdLessThanOrEqualTo(Long value) {
            addCriterion("AssistantId <=", value, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdIn(List<Long> values) {
            addCriterion("AssistantId in", values, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdNotIn(List<Long> values) {
            addCriterion("AssistantId not in", values, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdBetween(Long value1, Long value2) {
            addCriterion("AssistantId between", value1, value2, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAssistantIdNotBetween(Long value1, Long value2) {
            addCriterion("AssistantId not between", value1, value2, "AssistantId");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceIsNull() {
            addCriterion("Balance is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceIsNotNull() {
            addCriterion("Balance is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceEqualTo(Long value) {
            addCriterion("Balance =", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceNotEqualTo(Long value) {
            addCriterion("Balance <>", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceGreaterThan(Long value) {
            addCriterion("Balance >", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceGreaterThanOrEqualTo(Long value) {
            addCriterion("Balance >=", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceLessThan(Long value) {
            addCriterion("Balance <", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceLessThanOrEqualTo(Long value) {
            addCriterion("Balance <=", value, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceIn(List<Long> values) {
            addCriterion("Balance in", values, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceNotIn(List<Long> values) {
            addCriterion("Balance not in", values, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceBetween(Long value1, Long value2) {
            addCriterion("Balance between", value1, value2, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andBalanceNotBetween(Long value1, Long value2) {
            addCriterion("Balance not between", value1, value2, "balance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceIsNull() {
            addCriterion("UnBalance is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceIsNotNull() {
            addCriterion("UnBalance is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceEqualTo(Long value) {
            addCriterion("UnBalance =", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceNotEqualTo(Long value) {
            addCriterion("UnBalance <>", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceGreaterThan(Long value) {
            addCriterion("UnBalance >", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceGreaterThanOrEqualTo(Long value) {
            addCriterion("UnBalance >=", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceLessThan(Long value) {
            addCriterion("UnBalance <", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceLessThanOrEqualTo(Long value) {
            addCriterion("UnBalance <=", value, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceIn(List<Long> values) {
            addCriterion("UnBalance in", values, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceNotIn(List<Long> values) {
            addCriterion("UnBalance not in", values, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceBetween(Long value1, Long value2) {
            addCriterion("UnBalance between", value1, value2, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUnBalanceNotBetween(Long value1, Long value2) {
            addCriterion("UnBalance not between", value1, value2, "unBalance");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyIsNull() {
            addCriterion("SecurityMoney is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyIsNotNull() {
            addCriterion("SecurityMoney is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyEqualTo(Long value) {
            addCriterion("SecurityMoney =", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyNotEqualTo(Long value) {
            addCriterion("SecurityMoney <>", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyGreaterThan(Long value) {
            addCriterion("SecurityMoney >", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyGreaterThanOrEqualTo(Long value) {
            addCriterion("SecurityMoney >=", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyLessThan(Long value) {
            addCriterion("SecurityMoney <", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyLessThanOrEqualTo(Long value) {
            addCriterion("SecurityMoney <=", value, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyIn(List<Long> values) {
            addCriterion("SecurityMoney in", values, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyNotIn(List<Long> values) {
            addCriterion("SecurityMoney not in", values, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyBetween(Long value1, Long value2) {
            addCriterion("SecurityMoney between", value1, value2, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSecurityMoneyNotBetween(Long value1, Long value2) {
            addCriterion("SecurityMoney not between", value1, value2, "securityMoney");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeIsNull() {
            addCriterion("TotalIncome is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeIsNotNull() {
            addCriterion("TotalIncome is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeEqualTo(Long value) {
            addCriterion("TotalIncome =", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeNotEqualTo(Long value) {
            addCriterion("TotalIncome <>", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeGreaterThan(Long value) {
            addCriterion("TotalIncome >", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeGreaterThanOrEqualTo(Long value) {
            addCriterion("TotalIncome >=", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeLessThan(Long value) {
            addCriterion("TotalIncome <", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeLessThanOrEqualTo(Long value) {
            addCriterion("TotalIncome <=", value, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeIn(List<Long> values) {
            addCriterion("TotalIncome in", values, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeNotIn(List<Long> values) {
            addCriterion("TotalIncome not in", values, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeBetween(Long value1, Long value2) {
            addCriterion("TotalIncome between", value1, value2, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalIncomeNotBetween(Long value1, Long value2) {
            addCriterion("TotalIncome not between", value1, value2, "totalIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendIsNull() {
            addCriterion("TotalExpend is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendIsNotNull() {
            addCriterion("TotalExpend is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendEqualTo(Long value) {
            addCriterion("TotalExpend =", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendNotEqualTo(Long value) {
            addCriterion("TotalExpend <>", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendGreaterThan(Long value) {
            addCriterion("TotalExpend >", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendGreaterThanOrEqualTo(Long value) {
            addCriterion("TotalExpend >=", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendLessThan(Long value) {
            addCriterion("TotalExpend <", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendLessThanOrEqualTo(Long value) {
            addCriterion("TotalExpend <=", value, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendIn(List<Long> values) {
            addCriterion("TotalExpend in", values, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendNotIn(List<Long> values) {
            addCriterion("TotalExpend not in", values, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendBetween(Long value1, Long value2) {
            addCriterion("TotalExpend between", value1, value2, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTotalExpendNotBetween(Long value1, Long value2) {
            addCriterion("TotalExpend not between", value1, value2, "totalExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeIsNull() {
            addCriterion("TodayIncome is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeIsNotNull() {
            addCriterion("TodayIncome is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeEqualTo(Long value) {
            addCriterion("TodayIncome =", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeNotEqualTo(Long value) {
            addCriterion("TodayIncome <>", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeGreaterThan(Long value) {
            addCriterion("TodayIncome >", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeGreaterThanOrEqualTo(Long value) {
            addCriterion("TodayIncome >=", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeLessThan(Long value) {
            addCriterion("TodayIncome <", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeLessThanOrEqualTo(Long value) {
            addCriterion("TodayIncome <=", value, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeIn(List<Long> values) {
            addCriterion("TodayIncome in", values, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeNotIn(List<Long> values) {
            addCriterion("TodayIncome not in", values, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeBetween(Long value1, Long value2) {
            addCriterion("TodayIncome between", value1, value2, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayIncomeNotBetween(Long value1, Long value2) {
            addCriterion("TodayIncome not between", value1, value2, "todayIncome");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendIsNull() {
            addCriterion("TodayExpend is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendIsNotNull() {
            addCriterion("TodayExpend is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendEqualTo(Long value) {
            addCriterion("TodayExpend =", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendNotEqualTo(Long value) {
            addCriterion("TodayExpend <>", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendGreaterThan(Long value) {
            addCriterion("TodayExpend >", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendGreaterThanOrEqualTo(Long value) {
            addCriterion("TodayExpend >=", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendLessThan(Long value) {
            addCriterion("TodayExpend <", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendLessThanOrEqualTo(Long value) {
            addCriterion("TodayExpend <=", value, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendIn(List<Long> values) {
            addCriterion("TodayExpend in", values, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendNotIn(List<Long> values) {
            addCriterion("TodayExpend not in", values, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendBetween(Long value1, Long value2) {
            addCriterion("TodayExpend between", value1, value2, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andTodayExpendNotBetween(Long value1, Long value2) {
            addCriterion("TodayExpend not between", value1, value2, "todayExpend");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountIsNull() {
            addCriterion("SettAmount is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountIsNotNull() {
            addCriterion("SettAmount is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountEqualTo(Long value) {
            addCriterion("SettAmount =", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountNotEqualTo(Long value) {
            addCriterion("SettAmount <>", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountGreaterThan(Long value) {
            addCriterion("SettAmount >", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("SettAmount >=", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountLessThan(Long value) {
            addCriterion("SettAmount <", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountLessThanOrEqualTo(Long value) {
            addCriterion("SettAmount <=", value, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountIn(List<Long> values) {
            addCriterion("SettAmount in", values, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountNotIn(List<Long> values) {
            addCriterion("SettAmount not in", values, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountBetween(Long value1, Long value2) {
            addCriterion("SettAmount between", value1, value2, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andSettAmountNotBetween(Long value1, Long value2) {
            addCriterion("SettAmount not between", value1, value2, "settAmount");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusIsNull() {
            addCriterion("Status is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusIsNotNull() {
            addCriterion("Status is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusEqualTo(Byte value) {
            addCriterion("Status =", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("Status <>", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusGreaterThan(Byte value) {
            addCriterion("Status >", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("Status >=", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusLessThan(Byte value) {
            addCriterion("Status <", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("Status <=", value, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusIn(List<Byte> values) {
            addCriterion("Status in", values, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("Status not in", values, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("Status between", value1, value2, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("Status not between", value1, value2, "status");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeIsNull() {
            addCriterion("AccountUpdateTime is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeIsNotNull() {
            addCriterion("AccountUpdateTime is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeEqualTo(Date value) {
            addCriterion("AccountUpdateTime =", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeNotEqualTo(Date value) {
            addCriterion("AccountUpdateTime <>", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeGreaterThan(Date value) {
            addCriterion("AccountUpdateTime >", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("AccountUpdateTime >=", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeLessThan(Date value) {
            addCriterion("AccountUpdateTime <", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("AccountUpdateTime <=", value, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeIn(List<Date> values) {
            addCriterion("AccountUpdateTime in", values, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeNotIn(List<Date> values) {
            addCriterion("AccountUpdateTime not in", values, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("AccountUpdateTime between", value1, value2, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andAccountUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("AccountUpdateTime not between", value1, value2, "accountUpdateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeIsNull() {
            addCriterion("CreateTime is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeIsNotNull() {
            addCriterion("CreateTime is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("CreateTime =", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("CreateTime <>", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("CreateTime >", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("CreateTime >=", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeLessThan(Date value) {
            addCriterion("CreateTime <", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("CreateTime <=", value, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("CreateTime in", values, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("CreateTime not in", values, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("CreateTime between", value1, value2, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("CreateTime not between", value1, value2, "createTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeIsNull() {
            addCriterion("UpdateTime is null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeIsNotNull() {
            addCriterion("UpdateTime is not null");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("UpdateTime =", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("UpdateTime <>", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("UpdateTime >", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("UpdateTime >=", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("UpdateTime <", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("UpdateTime <=", value, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("UpdateTime in", values, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("UpdateTime not in", values, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("UpdateTime between", value1, value2, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }

        public AssistantAccountExample.Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("UpdateTime not between", value1, value2, "updateTime");
            return (AssistantAccountExample.Criteria) this;
        }
    }

    public static class Criteria extends AssistantAccountExample.GeneratedCriteria implements Serializable {

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
