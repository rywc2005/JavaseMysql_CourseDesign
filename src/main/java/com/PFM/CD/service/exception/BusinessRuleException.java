package com.PFM.CD.service.exception;

/**
 * 业务规则异常，当操作违反业务规则时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class BusinessRuleException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String ruleCode;

    /**
     * 构造一个带有错误消息的业务规则异常
     *
     * @param message 错误消息
     */
    public BusinessRuleException(String message) {
        super(message);
        this.ruleCode = "UNKNOWN";
    }

    /**
     * 构造一个带有错误消息和规则代码的业务规则异常
     *
     * @param message 错误消息
     * @param ruleCode 规则代码
     */
    public BusinessRuleException(String message, String ruleCode) {
        super(message);
        this.ruleCode = ruleCode;
    }

    /**
     * 获取规则代码
     *
     * @return 规则代码
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * 创建一个余额不为零不能停用账户的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException nonZeroBalanceDeactivation() {
        return new BusinessRuleException("账户余额不为零，无法停用", "ACCOUNT_BALANCE_NOT_ZERO");
    }

    /**
     * 创建一个不能从非活跃账户支出的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException inactiveAccountExpense() {
        return new BusinessRuleException("只能从活跃账户支出", "INACTIVE_ACCOUNT_EXPENSE");
    }

    /**
     * 创建一个不能向非活跃账户收入的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException inactiveAccountIncome() {
        return new BusinessRuleException("只能向活跃账户存入收入", "INACTIVE_ACCOUNT_INCOME");
    }

    /**
     * 创建一个交易日期不能是未来日期的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException futureDateTransaction() {
        return new BusinessRuleException("交易日期不能是未来日期", "FUTURE_DATE_TRANSACTION");
    }

    /**
     * 创建一个分类类型必须与交易类型匹配的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException categoryTypeMismatch() {
        return new BusinessRuleException("分类类型必须与交易类型匹配", "CATEGORY_TYPE_MISMATCH");
    }

    /**
     * 创建一个预算分配超出总额的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException budgetOverAllocation() {
        return new BusinessRuleException("分配金额超过预算剩余可分配金额", "BUDGET_OVER_ALLOCATION");
    }

    /**
     * 创建一个只能为支出分类分配预算的异常
     *
     * @return 业务规则异常实例
     */
    public static BusinessRuleException nonExpenseCategoryBudget() {
        return new BusinessRuleException("只能为支出分类分配预算", "NON_EXPENSE_CATEGORY_BUDGET");
    }
}