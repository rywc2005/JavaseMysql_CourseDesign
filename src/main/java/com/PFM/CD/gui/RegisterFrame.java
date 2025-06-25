package com.PFM.CD.gui;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.factory.ServiceFactory;
import javax.swing.*;
import java.awt.*;

/**
 * 个人财务管理系统 - 注册界面
 */
public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("注册 - 个人财务管理系统");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
                // ignore
            }
        }
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(250, 250, 250));

        JLabel titleLabel = new JLabel("账户注册");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(24));

        // 用户名
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("用户名：");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField = new JTextField(16);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);
        contentPanel.add(userPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 邮箱
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setOpaque(false);
        JLabel emailLabel = new JLabel("邮  箱：");
        emailLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        emailField = new JTextField(16);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 密码
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("密  码：");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordField = new JPasswordField(16);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);
        contentPanel.add(passPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 确认密码
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.setOpaque(false);
        JLabel confirmLabel = new JLabel("确认密码：");
        confirmLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        confirmPasswordField = new JPasswordField(16);
        confirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        confirmPanel.add(confirmLabel, BorderLayout.WEST);
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        contentPanel.add(confirmPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        JButton registerButton = new JButton("注 册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        registerButton.addActionListener(e -> doRegister());

        JButton backButton = new JButton("返回登录");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        backButton.setBackground(new Color(230, 230, 230));
        backButton.setForeground(Color.DARK_GRAY);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            loginFrame.showLogin();
            dispose();
        });

        contentPanel.add(registerButton);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(backButton);

        contentPanel.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(statusLabel);

        setContentPane(contentPanel);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();

        if (username.isEmpty()) {
            statusLabel.setText("请输入用户名");
            return;
        }
        if (password.isEmpty()) {
            statusLabel.setText("请输入密码");
            return;
        }
        if (!password.equals(confirm)) {
            statusLabel.setText("两次输入的密码不一致");
            return;
        }
        if (email.isEmpty()) {
            statusLabel.setText("请输入邮箱");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            statusLabel.setText("请输入有效的邮箱地址");
            return;
        }

        try {
            User user = ServiceFactory.getInstance().getUserService().register(username, password, email);
            JOptionPane.showMessageDialog(this, "注册成功！请登录", "注册成功", JOptionPane.INFORMATION_MESSAGE);
            loginFrame.showLogin();
            dispose();
        } catch (ServiceException ex) {
            statusLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            statusLabel.setText("发生错误，请稍后再试");
        }
    }

    private void doReset() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty()) {
            statusLabel.setText("请输入用户名");
            return;
        }
        if (email.isEmpty()) {
            statusLabel.setText("请输入邮箱");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            statusLabel.setText("请输入有效的邮箱地址");
            return;
        }
        if (newPassword.isEmpty()) {
            statusLabel.setText("请输入新密码");
            return;
        }

        try {
            ServiceFactory.getInstance().getUserService().resetPassword(username, email, newPassword);
            JOptionPane.showMessageDialog(this, "密码重置成功，请登录", "重置成功", JOptionPane.INFORMATION_MESSAGE);
            loginFrame.showLogin();
            dispose();
        } catch (Exception ex) {
            statusLabel.setText("发生错误，请稍后再试");
        }
    }
}
