package com.PFM.CD.gui;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.factory.ServiceFactory;
import com.PFM.CD.service.interfaces.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * 个人财务管理系统 - 忘记密码界面
 */
public class ForgotPasswordFrame extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JTextField confirmPasswordField;
    private JLabel statusLabel;
    private final LoginFrame loginFrame;

    public ForgotPasswordFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("找回密码 - 个人财务管理系统");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 220);
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

        JLabel titleLabel = new JLabel("找回密码");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(18));

        // 用户名面板
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

        // 邮箱面板
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setOpaque(false);
        JLabel emailLabel = new JLabel("邮  箱：");
        emailLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        emailField = new JTextField(16);
        emailField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);

        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        JButton resetButton = new JButton("找回密码");
        resetButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        resetButton.setBackground(new Color(255, 193, 7));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        resetButton.addActionListener(e -> doReset());

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

        contentPanel.add(resetButton);
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

    private void doReset() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

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
            UserService userService= ServiceFactory.getInstance().getUserService();
            userService.resetPassword(username, email, newPassword);
            JOptionPane.showMessageDialog(this, "密码重置成功，请登录", "重置成功", JOptionPane.INFORMATION_MESSAGE);
            loginFrame.showLogin();
            dispose();
        } catch (Exception ex) {
            statusLabel.setText("发生错误，请稍后再试");
        }
    }
}