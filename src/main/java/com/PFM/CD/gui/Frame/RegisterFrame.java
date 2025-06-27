package com.PFM.CD.gui.Frame;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.factory.ServiceFactory;
import javax.swing.*;
import java.awt.*;

/**
 * 个人财务管理系统 - 注册界面
 * 高端大气风格：渐变背景、圆角卡片、统一色彩、扁平按钮、现代大字体
 */
public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private final LoginFrame loginFrame;

    // 主题色
    private static final Color ACCENT = new Color(51, 102, 255);
    private static final Color BG_TOP = new Color(36, 57, 128);
    private static final Color BG_BOT = new Color(51, 102, 255);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 26);
    private static final Font FIELD_FONT = new Font("微软雅黑", Font.PLAIN, 16);

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("注册 - 个人财务管理系统");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 470);
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
        // 渐变背景Panel
        JPanel bgPanel = new GradientPanel(BG_TOP, BG_BOT, 0.0f, 0.8f);
        bgPanel.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Logo与标题
      //  JLabel logoLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/logo64.png")));
     //   logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    //    contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel("账户注册");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.black);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(22));

        // 用户名
        JPanel userPanel = new JPanel(new BorderLayout(8, 0));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("用户名：");
        userLabel.setFont(FIELD_FONT);
        userLabel.setForeground(Color.black);
        usernameField = new JTextField(16);
        usernameField.setFont(FIELD_FONT);
        usernameField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                )
        );
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);
        contentPanel.add(userPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 邮箱
        JPanel emailPanel = new JPanel(new BorderLayout(8, 0));
        emailPanel.setOpaque(false);
        JLabel emailLabel = new JLabel("邮  箱：");
        emailLabel.setFont(FIELD_FONT);
        emailLabel.setForeground(Color.black);
        emailField = new JTextField(16);
        emailField.setFont(FIELD_FONT);
        emailField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                )
        );
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 密码
        JPanel passPanel = new JPanel(new BorderLayout(8, 0));
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("密  码：");
        passLabel.setFont(FIELD_FONT);
        passLabel.setForeground(Color.black);
        passwordField = new JPasswordField(16);
        passwordField.setFont(FIELD_FONT);
        passwordField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                )
        );
        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);
        contentPanel.add(passPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        // 确认密码
        JPanel confirmPanel = new JPanel(new BorderLayout(8, 0));
        confirmPanel.setOpaque(false);
        JLabel confirmLabel = new JLabel("确认密码：");
        confirmLabel.setFont(FIELD_FONT);
        confirmLabel.setForeground(Color.black);
        confirmPasswordField = new JPasswordField(16);
        confirmPasswordField.setFont(FIELD_FONT);
        confirmPasswordField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                )
        );
        confirmPanel.add(confirmLabel, BorderLayout.WEST);
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        contentPanel.add(confirmPanel);
        contentPanel.add(Box.createVerticalStrut(18));

        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = createFlatButton("注 册", new Color(40, 167, 69), new Color(40, 187, 99));
        registerButton.addActionListener(e -> doRegister());

        JButton backButton = createFlatButton("返回登录", ACCENT, new Color(51, 102, 255, 180));
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        backButton.addActionListener(e -> {
            loginFrame.showLogin();
            dispose();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 100, 100));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(statusLabel);

        // 圆角卡片效果
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 235));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        cardPanel.add(contentPanel, BorderLayout.CENTER);

        bgPanel.add(cardPanel, new GridBagConstraints());

        setContentPane(bgPanel);
    }

    private JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 26, 8, 26));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
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
            statusLabel.setText("register发生错误，请稍后再试");
        }
    }

    // 支持渐变背景
    static class GradientPanel extends JPanel {
        private final Color from;
        private final Color to;
        private final float fromPct;
        private final float toPct;

        public GradientPanel(Color from, Color to, float fromPct, float toPct) {
            this.from = from;
            this.to = to;
            this.fromPct = fromPct;
            this.toPct = toPct;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(
                    0, (int) (h * fromPct), from,
                    0, (int) (h * toPct), to
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}