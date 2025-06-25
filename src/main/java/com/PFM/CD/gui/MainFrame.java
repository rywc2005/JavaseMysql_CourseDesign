package com.PFM.CD.gui;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */



import com.PFM.CD.service.factory.ServiceFactory;
import com.PFM.CD.service.interfaces.*;
import com.PFM.CD.entity.User;
import com.PFM.CD.service.interfaces.*;

import javax.swing.*;
import java.awt.*;

/**
 * 个人财务管理系统 - 主界面
 * 界面大气美观优雅，采用CardLayout切换各大功能面板
 */
public class MainFrame extends JFrame {
    User currentUser; // 当前登录用户

    // 各功能面板
    private DashboardPanel dashboardPanel;
    private AccountsPanel accountsPanel;
    private TransactionsPanel transactionsPanel;
    private BudgetsPanel budgetsPanel;
    private com.PFM.CD.gui.CatogoryPanel catogoryPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private StatusPanel statusPanel;

    // 主内容区
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(User user) {
        this.currentUser=user;
        setTitle("个人财务管理系统 Personal Finance Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);

        // 建议使用现代美观的LookAndFeel
        setElegantLookAndFeel();

        int userid=currentUser.getUserId();
        AccountService accountService = ServiceFactory.getInstance().getAccountService();
        BudgetService budgetService = ServiceFactory.getInstance().getBudgetService();
        TransactionService transactionService = ServiceFactory.getInstance().getTransactionService();
        CategoryService categoryService = ServiceFactory.getInstance().getCategoryService();
        ReportService reportService = ServiceFactory.getInstance().getReportService();
        StatisticsService statisticsService = ServiceFactory.getInstance().getStatisticsService();


        // 初始化各面板
        dashboardPanel = new DashboardPanel();
        accountsPanel = new AccountsPanel(userid,accountService);
        transactionsPanel = new TransactionsPanel(transactionService);
        budgetsPanel = new BudgetsPanel(userid,budgetService,categoryService);
        catogoryPanel = new CatogoryPanel(categoryService);
        reportsPanel = new ReportsPanel();
        settingsPanel = new SettingsPanel();
        statusPanel = new StatusPanel(currentUser);

        // 设置主布局
        setLayout(new BorderLayout());

        // 创建菜单栏和工具栏
        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.NORTH);

        // 主内容区（卡片布局）
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(accountsPanel, "accounts");
        contentPanel.add(transactionsPanel, "transactions");
        contentPanel.add(budgetsPanel, "budgets");
        contentPanel.add(catogoryPanel, "catogory");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(settingsPanel, "settings");
        add(contentPanel, BorderLayout.CENTER);

        // 状态栏
        add(statusPanel, BorderLayout.SOUTH);

        // 默认显示仪表盘
        showPanel("dashboard");
    }

    /**
     * 设置优雅的LookAndFeel
     */
    private void setElegantLookAndFeel() {
        try {
            // FlatLaf或Nimbus等现代UI风格
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            // 如果FlatLaf不可用则尝试Nimbus
        } catch (Exception e1) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e2) {
                // 忽略，使用默认
            }
        }
    }

    /**
     * 创建菜单栏
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 首页
        JMenu menuHome = new JMenu("首页");
        JMenuItem dashboardItem = new JMenuItem("仪表盘");
        dashboardItem.addActionListener(e -> showPanel("dashboard"));
        menuHome.add(dashboardItem);

        // 账户
        JMenu menuAccount = new JMenu("账户");
        JMenuItem accountsItem = new JMenuItem("账户管理");
        accountsItem.addActionListener(e -> showPanel("accounts"));
        menuAccount.add(accountsItem);

        // 交易
        JMenu menuTransaction = new JMenu("交易");
        JMenuItem transactionsItem = new JMenuItem("交易明细");
        transactionsItem.addActionListener(e -> showPanel("transactions"));
        menuTransaction.add(transactionsItem);

        // 预算
        JMenu menuBudget = new JMenu("预算");
        JMenuItem budgetsItem = new JMenuItem("预算管理");
        budgetsItem.addActionListener(e -> showPanel("budgets"));
        menuBudget.add(budgetsItem);

        // 分类
        JMenu menuCatogory = new JMenu("分类");
        JMenuItem catogoryItem = new JMenuItem("分类管理");
        catogoryItem.addActionListener(e -> showPanel("catogory"));
        menuCatogory.add(catogoryItem);

        // 报表
        JMenu menuReport = new JMenu("报表");
        JMenuItem reportsItem = new JMenuItem("报表中心");
        reportsItem.addActionListener(e -> showPanel("reports"));
        menuReport.add(reportsItem);

        // 设置
        JMenu menuSetting = new JMenu("设置");
        JMenuItem settingsItem = new JMenuItem("系统设置");
        settingsItem.addActionListener(e -> showPanel("settings"));
        menuSetting.add(settingsItem);

        menuBar.add(menuHome);
        menuBar.add(menuAccount);
        menuBar.add(menuTransaction);
        menuBar.add(menuBudget);
        menuBar.add(menuCatogory);
        menuBar.add(menuReport);
        menuBar.add(menuSetting);

        return menuBar;
    }

    /**
     * 创建工具栏
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton dashboardBtn = createToolbarButton("仪表盘", "dashboard");
        JButton accountsBtn = createToolbarButton("账户", "accounts");
        JButton transactionsBtn = createToolbarButton("交易", "transactions");
        JButton budgetsBtn = createToolbarButton("预算", "budgets");
        JButton catogoryBtn = createToolbarButton("分类", "catogory");
        JButton reportsBtn = createToolbarButton("报表", "reports");
        JButton settingsBtn = createToolbarButton("设置", "settings");

        toolBar.add(dashboardBtn);
        toolBar.add(accountsBtn);
        toolBar.add(transactionsBtn);
        toolBar.add(budgetsBtn);
        toolBar.add(catogoryBtn);
        toolBar.add(reportsBtn);
        toolBar.add(settingsBtn);

        toolBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        toolBar.setBackground(new Color(245, 245, 245));
        return toolBar;
    }

    /**
     * 工具栏按钮
     */
    private JButton createToolbarButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        button.addActionListener(e -> showPanel(panelName));
        return button;
    }

    /**
     * 切换主内容区显示的面板
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        statusPanel.setStatus("当前页面：" + getPanelDisplayName(panelName));
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
}
