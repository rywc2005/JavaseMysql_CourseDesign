package com.PFM.CD.gui;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.factory.ServiceFactory;
import com.PFM.CD.service.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 个人财务管理系统 - 主界面
 * 采用高端大气的现代UI风格，扁平化按钮、现代配色、渐变背景、圆角、侧边大图标导航等
 */
public class MainFrame extends JFrame {
    private User currentUser;

    // 各功能面板
    private DashboardPanel dashboardPanel;
    private AccountsPanel accountsPanel;
    private TransactionsPanel transactionsPanel;
    private BudgetsPanel budgetsPanel;
    private CatogoryPanel catogoryPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private StatusPanel statusPanel;

    // 主内容区
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // 主题色
    private static final Color SIDEBAR_BG = new Color(32, 34, 51);
    private static final Color MAIN_BG = new Color(245, 247, 251);
    private static final Color ACCENT = new Color(51, 102, 255);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 24);
    private static final Font NAV_FONT = new Font("微软雅黑", Font.PLAIN, 17);

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("PFM - 个人财务管理系统");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1260, 800);
        setMinimumSize(new Dimension(1120, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 定制全局字体
        UIManager.put("Button.font", NAV_FONT);
        UIManager.put("Menu.font", NAV_FONT);
        UIManager.put("MenuItem.font", NAV_FONT);
        UIManager.put("Label.font", NAV_FONT);
        UIManager.put("Table.font", NAV_FONT);
        UIManager.put("TableHeader.font", NAV_FONT);

        setElegantLookAndFeel();

        int userid = currentUser.getUserId();
        AccountService accountService = ServiceFactory.getInstance().getAccountService();
        BudgetService budgetService = ServiceFactory.getInstance().getBudgetService();
        TransactionService transactionService = ServiceFactory.getInstance().getTransactionService();
        CategoryService categoryService = ServiceFactory.getInstance().getCategoryService();
        ReportService reportService = ServiceFactory.getInstance().getReportService();
        StatisticsService statisticsService = ServiceFactory.getInstance().getStatisticsService();

        // 初始化各面板
        dashboardPanel = new DashboardPanel();
        accountsPanel = new AccountsPanel(userid, accountService);
        transactionsPanel = new TransactionsPanel(transactionService);
        budgetsPanel = new BudgetsPanel(userid, budgetService, categoryService);
        catogoryPanel = new CatogoryPanel(categoryService);
        reportsPanel = new ReportsPanel();
        settingsPanel = new SettingsPanel();
        statusPanel = new StatusPanel(currentUser);

        // 顶部栏（渐变背景+LOGO+应用名+用户+设置+退出）
        JPanel topBar = new GradientPanel(new Color(36, 57, 128), ACCENT, 0.7f, 0.3f);
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setLayout(new BorderLayout());

        // 左侧LOGO和应用名
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 12));
        logoPanel.setOpaque(false);
        JLabel title = new JLabel("PFM 财务管家");
        title.setForeground(Color.WHITE);
        title.setFont(TITLE_FONT);
        logoPanel.add(title);

        // 右侧用户与设置
        JPanel userPanelBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        userPanelBar.setOpaque(false);
        JLabel userName = new JLabel("欢迎您，" + (currentUser.getUsername() == null ? "用户" : currentUser.getUsername()));
        userName.setForeground(Color.WHITE);
        JButton btnSettings = createFlatButton("⚙ 设置", ACCENT);
        btnSettings.addActionListener(e -> showPanel("settings"));
        JButton btnLogout = createFlatButton("⎋ 退出", ACCENT);
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "确定要退出系统吗？", "退出确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
        userPanelBar.add(userName);
        userPanelBar.add(btnSettings);
        userPanelBar.add(btnLogout);

        topBar.add(logoPanel, BorderLayout.WEST);
        topBar.add(userPanelBar, BorderLayout.EAST);
        setJMenuBar(createMenuBar(ACCENT));

        add(topBar, BorderLayout.NORTH);

        // 侧边栏导航
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(162, 0));

        sidebar.add(Box.createVerticalStrut(26));
        sidebar.add(createNavButton("📊 仪表盘", "dashboard", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("🏦 账户管理", "accounts", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("💸 交易管理", "transactions", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("📊 预算管理", "budgets", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("📁 分类管理", "catogory", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("📈 报表中心", "reports", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("👤 用户中心", "settings", ACCENT, SIDEBAR_BG));
        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // 主内容区
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(accountsPanel, "accounts");
        contentPanel.add(transactionsPanel, "transactions");
        contentPanel.add(budgetsPanel, "budgets");
        contentPanel.add(catogoryPanel, "catogory");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(settingsPanel, "settings");

        add(contentPanel, BorderLayout.CENTER);

        // 状态栏
        add(createStatusBar(ACCENT), BorderLayout.SOUTH);

        // 默认显示仪表盘
        showPanel("dashboard");

        // 关闭前确认
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int ret = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出系统吗？", "退出确认", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    /**
     * 优雅的LookAndFeel
     */
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

    /**
     * 菜单栏，主功能入口（高端扁平风格）
     */
    private JMenuBar createMenuBar(Color accent) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 251));
        menuBar.setOpaque(true);

        JMenu menuHome = new JMenu("首页");
        menuHome.add(createMenuItem("仪表盘", "dashboard"));
        JMenu menuAccount = new JMenu("账户");
        menuAccount.add(createMenuItem("账户管理", "accounts"));
        JMenu menuTransaction = new JMenu("交易");
        menuTransaction.add(createMenuItem("交易明细", "transactions"));
        JMenu menuBudget = new JMenu("预算");
        menuBudget.add(createMenuItem("预算管理", "budgets"));
        JMenu menuCategory = new JMenu("分类");
        menuCategory.add(createMenuItem("分类管理", "catogory"));
        JMenu menuReport = new JMenu("报表");
        menuReport.add(createMenuItem("报表中心", "reports"));
        JMenu menuSetting = new JMenu("设置");
        menuSetting.add(createMenuItem("系统设置", "settings"));

        menuBar.add(menuHome);
        menuBar.add(menuAccount);
        menuBar.add(menuTransaction);
        menuBar.add(menuBudget);
        menuBar.add(menuCategory);
        menuBar.add(menuReport);
        menuBar.add(menuSetting);

        return menuBar;
    }

    private JMenuItem createMenuItem(String text, String panelName) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> showPanel(panelName));
        return item;
    }

    /**
     * 侧边导航按钮
     */
    private JButton createNavButton(String text, String panelName, Color accent, Color sidebarBg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 17));
        btn.setMaximumSize(new Dimension(170, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> showPanel(panelName));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(accent.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * 扁平高亮按钮
     */
    private JButton createFlatButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btn.setBackground(new Color(255, 255, 255, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    /**
     * 底部状态栏
     */
    private JPanel createStatusBar(Color accent) {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBackground(new Color(245, 247, 251));
        JLabel label = new JLabel("  © 2025 PFM 财务管家 —— 高端·专业·安全");
        label.setForeground(accent.darker());
        statusBar.add(label, BorderLayout.WEST);
        // 可扩展右侧系统时间/状态
        return statusBar;
    }

    /**
     * 切换主内容区显示的面板
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        if(statusPanel != null) {
            statusPanel.setStatus("当前页面：" + getPanelDisplayName(panelName));
        }
    }

    private String getPanelDisplayName(String panelName) {
        switch (panelName) {
            case "dashboard": return "仪表盘";
            case "accounts": return "账户";
            case "transactions": return "交易";
            case "budgets": return "预算";
            case "catogory": return "分类";
            case "reports": return "报表";
            case "settings": return "设置";
            default: return "";
        }
    }

    /**
     * 渐变背景Panel
     */
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