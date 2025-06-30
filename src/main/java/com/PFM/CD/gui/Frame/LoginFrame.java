package com.PFM.CD.gui.Frame;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.ServiceFactory;

import javax.swing.*;
import java.awt.*;

/**
 * 个人财务管理系统 - 登录界面
 * 高端大气上档次风格：渐变背景、大Logo、圆角扁平按钮、全局统一色彩、现代字体
 * 支持登录、注册、忘记密码功能
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    // 主题色
    private static final Color ACCENT = new Color(51, 102, 255);
    private static final Color BG_TOP = new Color(36, 57, 128);
    private static final Color BG_BOT = new Color(51, 102, 255);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 28);
    private static final Font SUB_FONT = new Font("微软雅黑", Font.PLAIN, 16);
    private static final Font FIELD_FONT = new Font("微软雅黑", Font.PLAIN, 16);

    public LoginFrame() {
        setTitle("登录 - 个人财务管理系统");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 440);
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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Logo与标题
       // JLabel logoLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/logo64.png")));
        //logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
       // contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel("个人财务管理系统");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.black);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("请登录您的账户");
        subtitleLabel.setFont(SUB_FONT);
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(subtitleLabel);
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
        contentPanel.add(Box.createVerticalStrut(16));

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
        contentPanel.add(Box.createVerticalStrut(22));

        // 按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = createFlatButton("登 录", ACCENT, new Color(34, 139, 230));
        loginButton.addActionListener(e -> dologin());

        JButton registerButton = createFlatButton("注册", new Color(40, 167, 69), new Color(40, 187, 99));
        registerButton.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
            this.setVisible(false);
        });

        JButton forgetButton = createFlatButton("忘记密码", new Color(255, 193, 7), new Color(255, 220, 100));
        forgetButton.addActionListener(e -> {
            new ForgotPasswordFrame(this).setVisible(true);
            this.setVisible(false);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(forgetButton);

        contentPanel.add(buttonPanel);

        contentPanel.add(Box.createVerticalStrut(12));

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
        }
    }

    /**
     * 供注册、忘记密码窗口跳转回登录
     */
    public void showLogin() {
        this.setVisible(true);
    }

    // 渐变背景Panel
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

    // 测试入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}