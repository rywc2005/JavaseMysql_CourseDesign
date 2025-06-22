package javasemysql.coursedesign.gui.dialog;

import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 账户编辑对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class AccountDialog extends JDialog {

    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JTextField balanceField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    private Account account;
    private int userId;
    private AccountService accountService;
    private boolean accountSaved = false;

    /**
     * 构造函数
     *
     * @param parent 父窗口
     * @param account 账户对象（为null表示新增）
     * @param userId 用户ID
     */
    public AccountDialog(JFrame parent, Account account, int userId) {
        super(parent, account == null ? "添加账户" : "编辑账户", true);
        this.account = account;
        this.userId = userId;
        this.accountService = new AccountServiceImpl();

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

        // 账户名称
        JLabel nameLabel = new JLabel("账户名称:");
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        // 账户类型
        JLabel typeLabel = new JLabel("账户类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(typeLabel, gbc);

        typeComboBox = new JComboBox<>(new String[]{"现金", "银行卡", "信用卡", "支付宝", "微信", "其他"});
        typeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(typeComboBox, gbc);

        // 账户余额
        JLabel balanceLabel = new JLabel("账户余额:");
        balanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(balanceLabel, gbc);

        balanceField = new JTextField(20);
        balanceField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(balanceField, gbc);

        // 账户说明
        JLabel descriptionLabel = new JLabel("账户说明:");
        descriptionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
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
        saveButton.addActionListener(e -> saveAccount());

        // 取消按钮点击事件
        cancelButton.addActionListener(e -> dispose());

        // 余额输入限制为数字和小数点
        balanceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0' && c <= '9') || c == '.' || c == '-' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }

                // 确保只有一个小数点
                if (c == '.' && balanceField.getText().contains(".")) {
                    e.consume();
                }

                // 确保只有一个负号，且只能在开头
                if (c == '-' && (balanceField.getText().contains("-") || balanceField.getCaretPosition() != 0)) {
                    e.consume();
                }
            }
        });
    }

    /**
     * 填充字段值
     */
    private void populateFields() {
        if (account != null) {
            nameField.setText(account.getName());
            typeComboBox.setSelectedItem(account.getType());
            balanceField.setText(String.valueOf(account.getBalance()));
            descriptionArea.setText(account.getDescription());
        } else {
            // 新增账户，设置默认值
            typeComboBox.setSelectedItem("现金");
            balanceField.setText("0.00");
        }
    }

    /**
     * 保存账户
     */
    private void saveAccount() {
        // 获取输入值
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String balanceStr = balanceField.getText().trim();
        String description = descriptionArea.getText().trim();

        // 验证输入
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入账户名称", "错误", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (balanceStr.isEmpty() || !ValidationUtils.isValidAmount(balanceStr)) {
            JOptionPane.showMessageDialog(this, "请输入有效的账户余额", "错误", JOptionPane.ERROR_MESSAGE);
            balanceField.requestFocus();
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "账户余额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            balanceField.requestFocus();
            return;
        }

        try {
            // 保存账户
            if (account == null) {
                // 新增账户
                Account newAccount = new Account();
                newAccount.setUserId(userId);
                newAccount.setName(name);
                newAccount.setType(type);
                newAccount.setBalance(balance);
                newAccount.setDescription(description);

                boolean success = accountService.addAccount(newAccount);
                if (success) {
                    accountSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "账户添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新账户
                account.setName(name);
                account.setType(type);
                account.setBalance(balance);
                account.setDescription(description);

                boolean success = accountService.updateAccount(account);
                if (success) {
                    accountSaved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "账户更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            LogUtils.error("保存账户失败", e);
            JOptionPane.showMessageDialog(this, "操作失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 检查账户是否已保存
     *
     * @return 是否已保存
     */
    public boolean isAccountSaved() {
        return accountSaved;
    }
}