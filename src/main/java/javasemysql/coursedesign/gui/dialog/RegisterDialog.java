package javasemysql.coursedesign.gui.dialog;

import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.UserService;
import javasemysql.coursedesign.service.impl.UserServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 用户注册对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class RegisterDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(RegisterDialog.class.getName());

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton cancelButton;
    private JCheckBox termsCheckBox;

    private UserService userService;
    private boolean registrationSuccessful = false;
    private User registeredUser = null;

    /**
     * 构造函数
     *
     * @param parent      父窗口
     * @param userService
     */
    public RegisterDialog(JFrame parent, UserService userService) {
        super(parent, "用户注册", true);
        this.userService = new UserServiceImpl();

        initComponents();
        setupListeners();

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("创建新账户");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(66, 139, 202));

        titlePanel.add(titleLabel);

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 用户名
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setToolTipText("用户名长度为3-20个字符，只能包含字母、数字和下划线");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(usernameField, gbc);

        // 密码
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setToolTipText("密码长度至少为6个字符，必须包含字母和数字");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(passwordField, gbc);

        // 确认密码
        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        confirmPasswordLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(confirmPasswordField, gbc);

        // 邮箱
        JLabel emailLabel = new JLabel("邮箱:");
        emailLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        emailField.setToolTipText("请输入有效的电子邮箱地址");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(emailField, gbc);

        // 手机号
        JLabel phoneLabel = new JLabel("手机号:");
        phoneLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneField.setToolTipText("请输入11位手机号码");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(phoneField, gbc);

        // 使用条款复选框
        termsCheckBox = new JCheckBox("我已阅读并同意《用户协议》和《隐私政策》");
        termsCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        formPanel.add(termsCheckBox, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(66, 139, 202));
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);

        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // 创建底部面板（包含登录链接）
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        JLabel loginPromptLabel = new JLabel("已有账号？");
        loginPromptLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JLabel loginLinkLabel = new JLabel("<html><u>点击这里登录</u></html>");
        loginLinkLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        loginLinkLabel.setForeground(new Color(66, 139, 202));
        loginLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(loginPromptLabel);
        footerPanel.add(loginLinkLabel);

        // 添加组件到主面板
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 设置内容面板
        setContentPane(contentPanel);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 注册按钮点击事件
        registerButton.addActionListener(e -> register());

        // 取消按钮点击事件
        cancelButton.addActionListener(e -> dispose());

        // 为输入字段添加回车键监听
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    register();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
        emailField.addKeyListener(enterKeyListener);
        phoneField.addKeyListener(enterKeyListener);

        // 窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    /**
     * 执行注册操作
     */
    private void register() {
        // 获取输入值
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        char[] confirmPasswordChars = confirmPasswordField.getPassword();
        String confirmPassword = new String(confirmPasswordChars);
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // 验证用户名
        if (username.isEmpty()) {
            showErrorMessage("请输入用户名");
            usernameField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidUsername(username)) {
            showErrorMessage("用户名格式不正确，长度为3-20个字符，只能包含字母、数字和下划线");
            usernameField.requestFocus();
            return;
        }

        // 验证密码
        if (password.isEmpty()) {
            showErrorMessage("请输入密码");
            passwordField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showErrorMessage("密码格式不正确，长度至少为6个字符，必须包含字母和数字");
            passwordField.requestFocus();
            return;
        }

        // 验证确认密码
        if (confirmPassword.isEmpty()) {
            showErrorMessage("请确认密码");
            confirmPasswordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("两次输入的密码不一致");
            confirmPasswordField.requestFocus();
            return;
        }

        // 验证邮箱
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            showErrorMessage("邮箱格式不正确");
            emailField.requestFocus();
            return;
        }

        // 验证手机号
        if (!phone.isEmpty() && !ValidationUtils.isValidPhoneNumber(phone)) {
            showErrorMessage("手机号格式不正确");
            phoneField.requestFocus();
            return;
        }

        // 验证用户协议
        if (!termsCheckBox.isSelected()) {
            showErrorMessage("请阅读并同意用户协议和隐私政策");
            return;
        }

        // 创建用户对象
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);


        try {
            // 检查用户名是否已存在
            boolean userExists = userService.isUsernameExists(username);
            if (userExists) {
                showErrorMessage("用户名已存在，请更换一个用户名");
                usernameField.requestFocus();
                return;
            }

            // 检查邮箱是否已存在
            if (!email.isEmpty()) {
                boolean emailExists = userService.isEmailExists(email);
                if (emailExists) {
                    showErrorMessage("邮箱已被注册，请更换一个邮箱");
                    emailField.requestFocus();
                    return;
                }
            }

            // 执行注册
            boolean success = userService.register(user);

            if (success) {
                // 注册成功
                registrationSuccessful = true;
                registeredUser = user;

                JOptionPane.showMessageDialog(
                        this,
                        "注册成功！",
                        "注册成功",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();
            } else {
                showErrorMessage("注册失败，请稍后重试");
            }
        } catch (Exception e) {
            LogUtils.error("注册失败", e);
            showErrorMessage("注册过程中发生错误: " + e.getMessage());
        } finally {
            // 清除密码字符数组
            clearPasswordFields(passwordChars, confirmPasswordChars);
        }
    }

    /**
     * 显示错误消息
     *
     * @param message 错误消息
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * 清除密码字段
     *
     * @param passwordChars 密码字符数组
     * @param confirmPasswordChars 确认密码字符数组
     */
    private void clearPasswordFields(char[] passwordChars, char[] confirmPasswordChars) {
        // 安全地清除密码字符数组
        for (int i = 0; i < passwordChars.length; i++) {
            passwordChars[i] = 0;
        }

        for (int i = 0; i < confirmPasswordChars.length; i++) {
            confirmPasswordChars[i] = 0;
        }
    }

    /**
     * 判断注册是否成功
     *
     * @return 是否注册成功
     */
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }

    /**
     * 获取注册的用户
     *
     * @return 注册的用户
     */
    public User getRegisteredUser() {
        return registeredUser;
    }

    public String getRegisteredUsername() {
        return registeredUser != null ? registeredUser.getName() : null;
    }
}