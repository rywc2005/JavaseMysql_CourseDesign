package javasemysql.coursedesign.gui;

import javasemysql.coursedesign.gui.dialog.RegisterDialog;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.UserService;
import javasemysql.coursedesign.service.impl.UserServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 登录面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = new UserServiceImpl();

        initComponents();
        setupListeners();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));

        // 创建登录表单面板
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);  // 使用绝对布局
        formPanel.setBackground(new Color(240, 240, 245));

        // 创建标题标签
        JLabel titleLabel = new JLabel("个人财务管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBounds(0, 80, 600, 40);

        // 创建子标题标签
        JLabel subtitleLabel = new JLabel("登录您的账户", JLabel.CENTER);
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(0, 130, 600, 30);

        // 创建用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameLabel.setBounds(150, 180, 100, 30);

        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBounds(150, 210, 300, 35);

        // 创建密码标签和输入框
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordLabel.setBounds(150, 250, 100, 30);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setBounds(150, 280, 300, 35);

        // 创建登录按钮
        loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginButton.setBounds(150, 340, 140, 40);
        loginButton.setBackground(new Color(66, 139, 202));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        // 创建注册按钮
        registerButton = new JButton("注册账号");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        registerButton.setBounds(310, 340, 140, 40);
        registerButton.setBackground(new Color(240, 240, 240));
        registerButton.setFocusPainted(false);

        // 创建状态标签
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(150, 390, 300, 30);

        // 添加组件到表单面板
        formPanel.add(titleLabel);
        formPanel.add(subtitleLabel);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(registerButton);
        formPanel.add(statusLabel);

        // 创建版权信息标签
        JLabel copyrightLabel = new JLabel("© 2025 个人财务管理系统 - 版本 1.0", JLabel.CENTER);
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(150, 150, 150));

        // 添加组件到主面板
        add(formPanel, BorderLayout.CENTER);
        add(copyrightLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 登录按钮点击事件
        loginButton.addActionListener(e -> login());

        // 注册按钮点击事件
        registerButton.addActionListener(e -> showRegisterDialog());

        // 密码框回车事件
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
    }

    /**
     * 执行登录
     */
    private void login() {
        // 获取输入信息
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // 验证输入
        if (username.isEmpty()) {
            statusLabel.setText("请输入用户名");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("请输入密码");
            passwordField.requestFocus();
            return;
        }

        // 禁用登录按钮，防止重复提交
        loginButton.setEnabled(false);
        statusLabel.setText("登录中...");

        // 创建登录线程，避免UI冻结
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // 调用服务进行登录验证
                return userService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        // 登录成功
                        statusLabel.setText("");
                        clearFields();
                        mainFrame.userLoggedIn(user);
                    } else {
                        // 登录失败
                        statusLabel.setText("用户名或密码错误");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    statusLabel.setText("登录过程中发生错误");
                    LogUtils.error("登录过程中发生错误", e);
                } finally {
                    // 恢复登录按钮
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    /**
     * 显示注册对话框
     */
    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(mainFrame, userService);
        dialog.setVisible(true);

        // 如果注册成功，自动填入用户名
        if (dialog.isRegistrationSuccessful()) {
            usernameField.setText(dialog.getRegisteredUsername());
            passwordField.requestFocus();
        }
    }

    /**
     * 清空输入字段
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}