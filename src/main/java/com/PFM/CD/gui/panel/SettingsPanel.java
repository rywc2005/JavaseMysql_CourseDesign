package com.PFM.CD.gui.panel;

import com.PFM.CD.gui.Frame.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 系统设置面板
 * 支持主题切换、语言切换、数据备份与恢复、系统信息，返回登录等
 */
public class SettingsPanel extends JPanel {
    private JComboBox<String> themeCombo;
    private JComboBox<String> languageCombo;
    private JButton backupBtn;
    private JButton restoreBtn;
    private JButton backBtn;
    private JLabel statusLabel;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // 标题
        JLabel titleLabel = new JLabel("系统设置", JLabel.LEFT);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 102, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 0));

        add(titleLabel, BorderLayout.NORTH);

        // 主体内容
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 48, 18, 48));

        // 主题切换
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setOpaque(false);
        JLabel themeLabel = new JLabel("主题风格：");
        themeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        themeCombo = new JComboBox<>(new String[]{
                "浅色主题 (FlatLightLaf)", "深色主题 (FlatDarkLaf)", "系统默认"
        });
        themeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        themeCombo.setMaximumSize(new Dimension(180, 32));
        themePanel.add(themeLabel);
        themePanel.add(themeCombo);

        // 语言切换
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        langPanel.setOpaque(false);
        JLabel langLabel = new JLabel("界面语言：");
        langLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        languageCombo = new JComboBox<>(new String[]{"简体中文", "English"});
        languageCombo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        languageCombo.setMaximumSize(new Dimension(150, 32));
        langPanel.add(langLabel);
        langPanel.add(languageCombo);

        // 数据备份与恢复
        JPanel dataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        dataPanel.setOpaque(false);
        backupBtn = createFlatButton("数据备份", new Color(51, 102, 255), new Color(80, 130, 255));
        restoreBtn = createFlatButton("数据恢复", new Color(40, 167, 69), new Color(80, 200, 110));


        dataPanel.add(backupBtn);
        dataPanel.add(restoreBtn);

        // 系统信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder("系统信息"));
        JLabel versionLabel = new JLabel("系统版本：PFM v1.0.0");
        JLabel authorLabel = new JLabel("开发团队：PFM团队");
        JLabel tipLabel = new JLabel("如有疑问请联系 support@pfm.com");
        versionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        authorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoPanel.add(versionLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(tipLabel);

        // 状态栏
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(90, 140, 90));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 6, 2, 6));

        // 返回登录按钮
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        logoutPanel.setOpaque(false);
        JButton logoutBtn = createFlatButton("返回登录", new Color(220, 53, 69), new Color(255, 99, 115));  // 红色系按钮
        logoutPanel.add(logoutBtn);

        //上面系统信息完毕

        // 依次加入主面板
        mainPanel.add(themePanel);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(langPanel);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(dataPanel);
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(logoutPanel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(statusLabel);

        add(mainPanel, BorderLayout.CENTER);

        // 事件绑定
        themeCombo.addActionListener(e -> switchTheme());
        languageCombo.addActionListener(e -> switchLanguage());
        backupBtn.addActionListener(e -> doBackup());
        restoreBtn.addActionListener(e -> doRestore());
        logoutBtn.addActionListener(e -> onLogout());

    }
    // 返回登录逻辑
    private void onLogout() {
        // 确认提示
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要返回登录吗？当前会话将结束", "退出确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 关闭当前主窗口
            Window mainWindow = SwingUtilities.getWindowAncestor(this);
            mainWindow.dispose();

            // 打开登录窗口（假设登录窗口类为LoginFrame）
            new LoginFrame().setVisible(true);
        }
    }

    private JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    // 切换主题
    private void switchTheme() {
        String theme = (String) themeCombo.getSelectedItem();
        try {
            if (theme.contains("浅色")) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } else if (theme.contains("深色")) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(this));
            statusLabel.setText("主题切换成功！");
        } catch (Exception e) {
            statusLabel.setText("主题切换失败...");
        }
    }

    // 切换语言（仅界面提示示例
    private void switchLanguage() {
        String lang = (String) languageCombo.getSelectedItem();
        statusLabel.setText("已切换为：" + lang);
        // 可结合ResourceBundle等实现
    }

    // 数据备份（演示）
    private void doBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择备份保存路径");
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            // TODO: 实际备份逻辑
            statusLabel.setText("数据备份成功！（模拟）");
        }
    }

    // 数据恢复（演示）
    private void doRestore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择恢复文件");
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            // TODO: 实际恢复逻辑
            statusLabel.setText("数据恢复成功！（模拟）");
        }
    }
}