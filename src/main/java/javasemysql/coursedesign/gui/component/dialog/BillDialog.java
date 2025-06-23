package javasemysql.coursedesign.gui.component.dialog;

import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Bill;
import javasemysql.coursedesign.service.BillService;
import javasemysql.coursedesign.service.impl.BillServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 账单编辑对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BillDialog extends JDialog {

    private JComboBox<String> accountComboBox;
    private JTextField categoryField;
    private JTextField amountField;
    private JFormattedTextField dueDateField;
    private JComboBox<String> statusComboBox;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    private Bill bill;
    private int userId;
    private List<Account> accounts;
    private BillService billService;
    private boolean billSaved = false;

    /**
     * 构造函数
     *
     * @param parent 父窗口
     * @param bill 账单对象（为null表示新增）
     * @param userId 用户ID
     * @param accounts 账户列表
     */
    public BillDialog(JFrame parent, Bill bill, int userId, List<Account> accounts) {
        super(parent, bill == null ? "添加账单" : "编辑账单", true);
        this.bill = bill;
        this.userId = userId;
        this.accounts = accounts;
        this.billService = new BillServiceImpl();

        initComponents();
        setupListeners();
        populateFields();

        setSize(450, 500);
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

        categoryField = new JTextField(20);
        categoryField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(categoryField, gbc);

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

        // 到期日
        JLabel dueDateLabel = new JLabel("到期日:");
        dueDateLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(dueDateLabel, gbc);

        // 创建日期格式化输入框
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            dueDateField = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            dueDateField = new JFormattedTextField();
            LogUtils.error("创建日期格式化器失败", e);
        }

        dueDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dueDateField.setToolTipText("格式：YYYY-MM-DD");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(dueDateField, gbc);

        // 状态
        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(statusLabel, gbc);

        statusComboBox = new JComboBox<>(new String[]{"待付款", "已付款"});
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(statusComboBox, gbc);

        // 说明
        JLabel descriptionLabel = new JLabel("说明:");
        descriptionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 5;
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
        saveButton.addActionListener(e -> saveBill());

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
        if (bill != null) {
            // 设置账户
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getId() == bill.getAccountId()) {
                    accountComboBox.setSelectedIndex(i);
                    break;
                }
            }

            amountField.setText(String.valueOf(bill.getAmount()));
            dueDateField.setText(DateUtils.formatDate(bill.getDueDate()));

            // 设置状态
            if ("paid".equalsIgnoreCase(bill.getStatus())) {
                statusComboBox.setSelectedIndex(1); // 已付款
            } else {
                statusComboBox.setSelectedIndex(0); // 待付款
            }

        } else {
            // 新增账单，设置默认值
            statusComboBox.setSelectedIndex(0); // 默认待付款
            dueDateField.setText(DateUtils.formatDate(new Date())); // 默认今天
        }
    }

    /**
     * 保存账单
     */
    private void saveBill() {
        // 获取输入值
        int selectedAccountIndex = accountComboBox.getSelectedIndex();
        String category = categoryField.getText().trim();
        String amountStr = amountField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();
        boolean isPaid = statusComboBox.getSelectedIndex() == 1;
        String description = descriptionArea.getText().trim();

        // 验证输入
        if (selectedAccountIndex == -1) {
            JOptionPane.showMessageDialog(this, "请选择账户", "错误", JOptionPane.ERROR_MESSAGE);
            accountComboBox.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入类别", "错误", JOptionPane.ERROR_MESSAGE);
            categoryField.requestFocus();
            return;
        }

        if (amountStr.isEmpty() || !ValidationUtils.isValidAmount(amountStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的金额", "错误", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidDate(dueDateStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的日期（格式：YYYY-MM-DD）", "错误", JOptionPane.ERROR_MESSAGE);
            dueDateField.requestFocus();
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

        Date dueDate;
        try {
            dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            dueDateField.requestFocus();
            return;
        }

        try {
            // 保存账单
            if (bill == null) {
                // 新增账单
                Bill newBill = new Bill();
                newBill.setUserId(userId);
                newBill.setAccountId(accounts.get(selectedAccountIndex).getId());
                newBill.setAmount(amount);
                newBill.setStatus(isPaid ? "paid" : "unpaid");

                boolean success = billService.addBill(newBill);
                if (success) {
                    billSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "账单添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新账单
                bill.setAccountId(accounts.get(selectedAccountIndex).getId());
                bill.setAmount(amount);
                bill.setDueDate((java.sql.Date) dueDate);
                bill.setStatus(isPaid ? "paid" : "unpaid");

                boolean success = billService.updateBill(bill);
                if (success) {
                    billSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "账单更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LogUtils.error("保存账单失败", e);
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 检查账单是否已保存
     *
     * @return 是否已保存
     */
    public boolean isBillSaved() {
        return billSaved;
    }
}