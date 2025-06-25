package com.PFM.CD.gui;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.factory.ServiceFactory;

import javax.swing.*;
import java.awt.*;


/**
 * 个人财务管理系统 - 登录界面
 * 支持登录、注册、忘记密码功能
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("登录 - 个人财务管理系统");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        setElegantLookAndFeel();
        initComponents();
    }

    private void setElegantLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e1) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e2) {
                // 忽略
            }
        }
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(250, 250, 250));

        JLabel titleLabel = new JLabel("个人财务管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("请登录您的账户");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(24));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("用户名：");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField = new JTextField(16);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);
        contentPanel.add(userPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("密  码：");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordField = new JPasswordField(16);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);
        contentPanel.add(passPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = new JButton("登 录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        loginButton.addActionListener(e -> dologin());

        JButton registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        registerButton.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
            this.setVisible(false);
        });

        JButton forgetButton = new JButton("忘记密码");
        forgetButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        forgetButton.setBackground(new Color(255, 193, 7));
        forgetButton.setForeground(Color.WHITE);
        forgetButton.setFocusPainted(false);
        forgetButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        forgetButton.addActionListener(e -> {
            new ForgotPasswordFrame(this).setVisible(true);
            this.setVisible(false);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(forgetButton);
        contentPanel.add(buttonPanel);

        contentPanel.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(statusLabel);

        setContentPane(contentPanel);
    }

    /**
     * 登录按钮事件处理
     */
    public void dologin() {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            if (username.isEmpty()) {
                statusLabel.setText("请输入用户名");
                return;
            }
            if (password.length == 0) {
                statusLabel.setText("请输入密码");
                return;
            }
            try {
                User user = ServiceFactory.getInstance().getUserService().login(username, new String(password));
                if (user != null) {
                    MainFrame mainFrame = new MainFrame(user);
                    mainFrame.setVisible(true);
                    dispose(); // 关闭登录窗口
                } else {
                    statusLabel.setText("用户名或密码错误");
                }
            } catch (ServiceException ex) {
                statusLabel.setText("登录失败：" + ex.getMessage());
            } catch (Exception ex) {
                statusLabel.setText("发生错误，请稍后再试");
            }
        }

    /**
     * 供注册、忘记密码窗口跳转回登录
     */
    public void showLogin() {
        this.setVisible(true);
    }

    // 测试入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}