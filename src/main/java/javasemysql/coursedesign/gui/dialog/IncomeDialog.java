package javasemysql.coursedesign.gui.dialog;

import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Income;
import javasemysql.coursedesign.service.IncomeService;
import javasemysql.coursedesign.service.impl.IncomeServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 收入编辑对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class IncomeDialog extends JDialog {

    private JComboBox<String> accountComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JFormattedTextField dateField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    private Income income;
    private int userId;
    private List<Account> accounts;
    private IncomeService incomeService;
    private boolean incomeSaved = false;

    /**
     * 构造函数
     *
     * @param parent 父窗口
     * @param income 收入对象（为null表示新增）
     * @param userId 用户ID
     * @param accounts 账户列表
     */
    public IncomeDialog(JFrame parent, Income income, int userId, List<Account> accounts) {
        super(parent, income == null ? "添加收入" : "编辑收入", true);
        this.income = income;
        this.userId = userId;
        this.accounts = accounts;
        this.incomeService = new IncomeServiceImpl();

        initComponents();
        setupListeners();
        populateFields();

        setSize(450, 450);
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

        // 账户
        JLabel accountLabel = new JLabel("账户:");
        accountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(accountLabel, gbc);

        // 创建账户下拉框
        accountComboBox = new JComboBox<>();
        accountComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 填充账户下拉框
        for (Account account : accounts) {
            accountComboBox.addItem(account.getName());
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(accountComboBox, gbc);

        // 类别
        JLabel categoryLabel = new JLabel("类别:");
        categoryLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(categoryLabel, gbc);

        // 创建类别下拉框
        categoryComboBox = new JComboBox<>(new String[]{"工资", "奖金", "投资收益", "利息", "礼金", "兼职", "退款", "其他"});
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(categoryComboBox, gbc);

        // 金额
        JLabel amountLabel = new JLabel("金额:");
        amountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(amountField, gbc);

        // 日期
        JLabel dateLabel = new JLabel("日期:");
        dateLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(dateLabel, gbc);

        // 创建日期格式化输入框
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            dateField = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            dateField = new JFormattedTextField();
            LogUtils.error("创建日期格式化器失败", e);
        }

        dateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dateField.setToolTipText("格式：YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(dateField, gbc);

        // 说明
        JLabel descriptionLabel = new JLabel("说明:");
        descriptionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(scrollPane, gbc);

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
        saveButton.addActionListener(e -> saveIncome());

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
    }

    /**
     * 填充字段值
     */
    private void populateFields() {
        if (income != null) {
            // 设置账户
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getId() == income.getAccountId()) {
                    accountComboBox.setSelectedIndex(i);
                    break;
                }
            }

            // 设置类别
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (categoryComboBox.getItemAt(i).equals(income.getCategory())) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }

            amountField.setText(String.valueOf(income.getAmount()));
            dateField.setText(DateUtils.formatDate(income.getDate()));
            descriptionArea.setText(income.getDescription());
        } else {
            // 新增收入，设置默认值
            dateField.setText(DateUtils.formatDate(new Date())); // 默认今天
        }
    }

    /**
     * 保存收入
     */
    private void saveIncome() {
        // 获取输入值
        int selectedAccountIndex = accountComboBox.getSelectedIndex();
        String category = (String) categoryComboBox.getSelectedItem();
        String amountStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        String description = descriptionArea.getText().trim();

        // 验证输入
        if (selectedAccountIndex == -1) {
            JOptionPane.showMessageDialog(this, "请选择账户", "错误", JOptionPane.ERROR_MESSAGE);
            accountComboBox.requestFocus();
            return;
        }

        if (amountStr.isEmpty() || !ValidationUtils.isValidAmount(amountStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的金额", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidDate(dateStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的日期（格式：YYYY-MM-DD）", "错误", JOptionPane.ERROR_MESSAGE);
            dateField.requestFocus();
            return;
        }

        // 解析数据
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "金额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            dateField.requestFocus();
            return;
        }

        try {
            // 保存收入
            if (income == null) {
                // 新增收入
                Income newIncome = new Income();
                newIncome.setUserId(userId);
                newIncome.setAccountId(accounts.get(selectedAccountIndex).getId());
                newIncome.setAccountName(accounts.get(selectedAccountIndex).getName());
                newIncome.setCategory(category);
                newIncome.setAmount(amount);
                newIncome.setDate(date);
                newIncome.setDescription(description);

                boolean success = incomeService.addIncome(newIncome);
                if (success) {
                    incomeSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "收入添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新收入
                income.setAccountId(accounts.get(selectedAccountIndex).getId());
                income.setAccountName(accounts.get(selectedAccountIndex).getName());
                income.setCategory(category);
                income.setAmount(amount);
                income.setDate(date);
                income.setDescription(description);

                boolean success = incomeService.updateIncome(income);
                if (success) {
                    incomeSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "收入更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LogUtils.error("保存收入失败", e);
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 检查收入是否已保存
     *
     * @return 是否已保存
     */
    public boolean isIncomeSaved() {
        return incomeSaved;
    }
}