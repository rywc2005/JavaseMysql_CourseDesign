package javasemysql.coursedesign.gui.component.dialog;

import javasemysql.coursedesign.model.Budget;
import javasemysql.coursedesign.service.BudgetService;
import javasemysql.coursedesign.service.impl.BudgetServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 预算编辑对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BudgetDialog extends JDialog {

    private JTextField categoryField;
    private JTextField amountField;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JTextField usedAmountField;
    private JButton saveButton;
    private JButton cancelButton;

    private Budget budget;
    private int userId;
    private BudgetService budgetService;
    private boolean budgetSaved = false;

    /**
     * 构造函数
     *
     * @param parent 父窗口
     * @param budget 预算对象（为null表示新增）
     * @param userId 用户ID
     */
    public BudgetDialog(JFrame parent, Budget budget, int userId) {
        super(parent, budget == null ? "添加预算" : "编辑预算", true);
        this.budget = budget;
        this.userId = userId;
        this.budgetService = new BudgetServiceImpl();

        initComponents();
        setupListeners();
        populateFields();

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 类别
        JLabel categoryLabel = new JLabel("预算类别:");
        categoryLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(categoryLabel, gbc);

        categoryField = new JTextField(20);
        categoryField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(categoryField, gbc);

        // 金额
        JLabel amountLabel = new JLabel("预算金额:");
        amountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(amountField, gbc);

        // 开始日期
        JLabel startDateLabel = new JLabel("开始日期:");
        startDateLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(startDateLabel, gbc);

        // 创建日期格式化输入框
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            startDateField = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            startDateField = new JFormattedTextField();
            LogUtils.error("创建日期格式化器失败", e);
        }

        startDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        startDateField.setToolTipText("格式：YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(startDateField, gbc);

        // 结束日期
        JLabel endDateLabel = new JLabel("结束日期:");
        endDateLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(endDateLabel, gbc);

        // 创建日期格式化输入框
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            endDateField = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            endDateField = new JFormattedTextField();
            LogUtils.error("创建日期格式化器失败", e);
        }

        endDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        endDateField.setToolTipText("格式：YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(endDateField, gbc);

        // 已用金额
        JLabel usedAmountLabel = new JLabel("已用金额:");
        usedAmountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(usedAmountLabel, gbc);

        usedAmountField = new JTextField(20);
        usedAmountField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(usedAmountField, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("保存");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        saveButton.setPreferredSize(new Dimension(80, 30));

        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(80, 30));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // 添加到主面板
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 设置内容面板
        setContentPane(contentPanel);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 保存按钮点击事件
        saveButton.addActionListener(e -> saveBudget());

        // 取消按钮点击事件
        cancelButton.addActionListener(e -> dispose());

        // 金额输入限制为数字和小数点
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || c == '.' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }

                // 确保只有一个小数点
                if (c == '.' && amountField.getText().contains(".")) {
                    e.consume();
                }
            }
        });

        // 已用金额输入限制为数字和小数点
        usedAmountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || c == '.' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }

                // 确保只有一个小数点
                if (c == '.' && usedAmountField.getText().contains(".")) {
                    e.consume();
                }
            }
        });
    }

    /**
     * 填充字段值
     */
    private void populateFields() {
        if (budget != null) {
            categoryField.setText(budget.getCategory());
            amountField.setText(String.valueOf(budget.getAmount()));
            startDateField.setText(DateUtils.formatDate(budget.getStartDate()));
            endDateField.setText(DateUtils.formatDate(budget.getEndDate()));
            usedAmountField.setText(String.valueOf(budget.getUsedAmount()));
        } else {
            // 新增预算，设置默认值
            // 默认开始日期为当前月份的第一天
            Date[] monthRange = DateUtils.getCurrentMonthRange();
            startDateField.setText(DateUtils.formatDate(monthRange[0]));

            // 默认结束日期为当前月份的最后一天
            endDateField.setText(DateUtils.formatDate(monthRange[1]));

            // 默认已用金额为0
            usedAmountField.setText("0.00");
        }
    }

    /**
     * 保存预算
     */
    private void saveBudget() {
        // 获取输入值
        String category = categoryField.getText().trim();
        String amountStr = amountField.getText().trim();
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();
        String usedAmountStr = usedAmountField.getText().trim();

        // 验证输入
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入预算类别", "错误", JOptionPane.ERROR_MESSAGE);
            categoryField.requestFocus();
            return;
        }

        if (amountStr.isEmpty() || !ValidationUtils.isValidAmount(amountStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的预算金额", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidDate(startDateStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的开始日期（格式：YYYY-MM-DD）", "错误", JOptionPane.ERROR_MESSAGE);
            startDateField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidDate(endDateStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的结束日期（格式：YYYY-MM-DD）", "错误", JOptionPane.ERROR_MESSAGE);
            endDateField.requestFocus();
            return;
        }

        if (usedAmountStr.isEmpty() || !ValidationUtils.isValidAmount(usedAmountStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的已用金额", "错误", JOptionPane.ERROR_MESSAGE);
            usedAmountField.requestFocus();
            return;
        }

        // 解析数据
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "预算金额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        double usedAmount;
        try {
            usedAmount = Double.parseDouble(usedAmountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "已用金额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            usedAmountField.requestFocus();
            return;
        }

        Date startDate;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "开始日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            startDateField.requestFocus();
            return;
        }

        Date endDate;
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "结束日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            endDateField.requestFocus();
            return;
        }

        // 验证日期范围
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期", "错误", JOptionPane.ERROR_MESSAGE);
            startDateField.requestFocus();
            return;
        }

        try {
            // 保存预算
            if (budget == null) {
                // 新增预算
                Budget newBudget = new Budget();
                newBudget.setUserId(userId);
                newBudget.setCategory(category);
                newBudget.setAmount(amount);
                newBudget.setStartDate((Timestamp) startDate);
                newBudget.setEndDate((Timestamp) endDate);
                newBudget.setUsedAmount(usedAmount);

                boolean success = budgetService.addBudget(newBudget);
                if (success) {
                    budgetSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "预算添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新预算
                budget.setCategory(category);
                budget.setAmount(amount);
                budget.setStartDate((Timestamp) startDate);
                budget.setEndDate((Timestamp) endDate);
                budget.setUsedAmount(usedAmount);

                boolean success = budgetService.updateBudget(budget);
                if (success) {
                    budgetSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "预算更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LogUtils.error("保存预算失败", e);
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 检查预算是否已保存
     *
     * @return 是否已保存
     */
    public boolean isBudgetSaved() {
        return budgetSaved;
    }
}