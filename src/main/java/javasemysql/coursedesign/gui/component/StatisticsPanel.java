package javasemysql.coursedesign.gui.component;

import javasemysql.coursedesign.dto.StatisticsQueryParam;
import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.service.IncomeService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.service.impl.ExpenseServiceImpl;
import javasemysql.coursedesign.service.impl.IncomeServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 统计分析面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class StatisticsPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(StatisticsPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;

    private IncomeService incomeService;
    private ExpenseService expenseService;
    private AccountService accountService;

    // UI组件
    private JComboBox<String> chartTypeComboBox;
    private JComboBox<String> timeRangeComboBox;
    private JComboBox<String> accountComboBox;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> groupByComboBox;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JButton applyButton;
    private JButton refreshButton;
    private JPanel chartPanel;
    private JPanel summaryPanel;
    private JPanel totalIncomeLabel;
    private JPanel totalExpenseLabel;
    private JPanel netIncomeLabel;
    private JPanel avgIncomeLabel;
    private JPanel avgExpenseLabel;
    private JPanel maxIncomeLabel;
    private JPanel maxExpenseLabel;

    // 查询参数
    private String selectedChartType = "柱状图";
    private String selectedTimeRange = "本月";
    private String selectedAccount = "所有账户";
    private String selectedCategory = "所有类别";
    private String selectedGroupBy = "按日统计";
    private Date startDate;
    private Date endDate;
    private int selectedAccountId = -1;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public StatisticsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // 初始化服务
        this.incomeService = new IncomeServiceImpl();
        this.expenseService = new ExpenseServiceImpl();
        this.accountService = new AccountServiceImpl();

        // 初始化日期范围
        Date[] monthRange = DateUtils.getCurrentMonthRange();
        startDate = monthRange[0];
        endDate = monthRange[1];

        initComponents();
        setupListeners();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 245));

        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);

        // 创建标题标签
        JLabel titleLabel = new JLabel("统计分析");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        topPanel.add(titleLabel, BorderLayout.WEST);

        // 创建过滤面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);

        // 图表类型
        JLabel chartTypeLabel = new JLabel("图表类型:");
        chartTypeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        chartTypeComboBox = new JComboBox<>(new String[]{"柱状图", "折线图", "饼图", "堆叠柱状图"});
        chartTypeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chartTypeComboBox.setSelectedItem(selectedChartType);

        // 时间范围
        JLabel timeRangeLabel = new JLabel("时间范围:");
        timeRangeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        timeRangeComboBox = new JComboBox<>(new String[]{"今天", "本周", "本月", "上个月", "本季度", "本年度", "自定义"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        timeRangeComboBox.setSelectedItem(selectedTimeRange);

        // 自定义日期范围
        JLabel startDateLabel = new JLabel("开始日期:");
        startDateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            startDateField = new JFormattedTextField(dateFormatter);
            endDateField = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            startDateField = new JFormattedTextField();
            endDateField = new JFormattedTextField();
            LogUtils.error("创建日期格式化器失败", e);
        }

        startDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        startDateField.setPreferredSize(new Dimension(100, 25));
        startDateField.setText(DateUtils.formatDate(startDate));

        JLabel endDateLabel = new JLabel("结束日期:");
        endDateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        endDateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        endDateField.setPreferredSize(new Dimension(100, 25));
        endDateField.setText(DateUtils.formatDate(endDate));

        // 账户
        JLabel accountLabel = new JLabel("账户:");
        accountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        accountComboBox = new JComboBox<>();
        accountComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        accountComboBox.addItem("所有账户");

        // 类别
        JLabel categoryLabel = new JLabel("类别:");
        categoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        categoryComboBox = new JComboBox<>(new String[]{"所有类别", "收入", "支出"});
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryComboBox.setSelectedItem(selectedCategory);

        // 分组方式
        JLabel groupByLabel = new JLabel("分组方式:");
        groupByLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        groupByComboBox = new JComboBox<>(new String[]{"按日统计", "按周统计", "按月统计", "按季度统计", "按年统计", "按类别统计"});
        groupByComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        groupByComboBox.setSelectedItem(selectedGroupBy);

        // 应用按钮
        applyButton = new JButton("应用");
        applyButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        applyButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/apply_icon.png")));
        applyButton.setFocusPainted(false);

        // 刷新按钮
        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/refresh_icon.png")));
        refreshButton.setFocusPainted(false);

        // 添加组件到过滤面板
        filterPanel.add(chartTypeLabel);
        filterPanel.add(chartTypeComboBox);
        filterPanel.add(timeRangeLabel);
        filterPanel.add(timeRangeComboBox);
        filterPanel.add(startDateLabel);
        filterPanel.add(startDateField);
        filterPanel.add(endDateLabel);
        filterPanel.add(endDateField);
        filterPanel.add(accountLabel);
        filterPanel.add(accountComboBox);
        filterPanel.add(categoryLabel);
        filterPanel.add(categoryComboBox);
        filterPanel.add(groupByLabel);
        filterPanel.add(groupByComboBox);
        filterPanel.add(applyButton);
        filterPanel.add(refreshButton);

        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        // 创建汇总面板
        summaryPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "财务汇总",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        summaryPanel.setBackground(Color.WHITE);

        // 总收入
        totalIncomeLabel = createSummaryLabel("总收入", "¥0.00", new Color(92, 184, 92));

        // 总支出
        totalExpenseLabel = createSummaryLabel("总支出", "¥0.00", new Color(217, 83, 79));

        // 净收入
        netIncomeLabel = createSummaryLabel("净收入", "¥0.00", new Color(66, 139, 202));

        // 平均收入
        avgIncomeLabel = createSummaryLabel("平均收入", "¥0.00", new Color(92, 184, 92));

        // 平均支出
        avgExpenseLabel = createSummaryLabel("平均支出", "¥0.00", new Color(217, 83, 79));

        // 最高收入
        maxIncomeLabel = createSummaryLabel("最高收入", "¥0.00", new Color(92, 184, 92));

        // 最高支出
        maxExpenseLabel = createSummaryLabel("最高支出", "¥0.00", new Color(217, 83, 79));

        summaryPanel.add(totalIncomeLabel);
        summaryPanel.add(totalExpenseLabel);
        summaryPanel.add(netIncomeLabel);
        summaryPanel.add(avgIncomeLabel);
        summaryPanel.add(avgExpenseLabel);
        summaryPanel.add(maxIncomeLabel);
        summaryPanel.add(maxExpenseLabel);

        // 创建图表面板
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "统计图表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        chartPanel.setBackground(Color.WHITE);

        // 显示加载提示
        JLabel loadingLabel = new JLabel("正在加载数据...", JLabel.CENTER);
        loadingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chartPanel.add(loadingLabel, BorderLayout.CENTER);

        // 添加组件到内容面板
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        contentPanel.add(chartPanel, BorderLayout.CENTER);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建汇总标签
     *
     * @param title 标题
     * @param value 值
     * @param color 颜色
     * @return 标签面板
     */
    private JPanel createSummaryLabel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        valueLabel.setForeground(color);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> {
            String selectedItem = (String) timeRangeComboBox.getSelectedItem();
            boolean isCustom = "自定义".equals(selectedItem);

            startDateField.setEnabled(isCustom);
            endDateField.setEnabled(isCustom);

            if (!isCustom) {
                Date[] dateRange = getDateRangeBySelection(selectedItem);
                if (dateRange != null) {
                    startDate = dateRange[0];
                    endDate = dateRange[1];
                    startDateField.setText(DateUtils.formatDate(startDate));
                    endDateField.setText(DateUtils.formatDate(endDate));
                }
            }
        });

        // 应用按钮点击事件
        applyButton.addActionListener(e -> {
            if (validateInputs()) {
                selectedChartType = (String) chartTypeComboBox.getSelectedItem();
                selectedTimeRange = (String) timeRangeComboBox.getSelectedItem();
                selectedAccount = (String) accountComboBox.getSelectedItem();
                selectedCategory = (String) categoryComboBox.getSelectedItem();
                selectedGroupBy = (String) groupByComboBox.getSelectedItem();

                // 设置选中的账户ID
                selectedAccountId = -1;
                if (!"所有账户".equals(selectedAccount) && accountComboBox.getSelectedIndex() > 0) {
                    try {
                        // 获取选中账户的ID
                        String accountName = (String) accountComboBox.getSelectedItem();
                        for (Account account : accounts) {
                            if (account.getName().equals(accountName)) {
                                selectedAccountId = account.getId();
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        LogUtils.error("获取账户ID失败", ex);
                    }
                }

                // 如果是自定义时间范围，则解析输入的日期
                if ("自定义".equals(selectedTimeRange)) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        startDate = sdf.parse(startDateField.getText());
                        endDate = sdf.parse(endDateField.getText());
                    } catch (ParseException ex) {
                        LogUtils.error("解析日期失败", ex);
                        JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // 生成图表
                generateStatistics();
            }
        });

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());
    }

    /**
     * 验证输入
     *
     * @return 输入是否有效
     */
    private boolean validateInputs() {
        if ("自定义".equals(timeRangeComboBox.getSelectedItem())) {
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();

            if (!DateUtils.isValidDateFormat(startDateStr)) {
                JOptionPane.showMessageDialog(this, "开始日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                startDateField.requestFocus();
                return false;
            }

            if (!DateUtils.isValidDateFormat(endDateStr)) {
                JOptionPane.showMessageDialog(this, "结束日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                endDateField.requestFocus();
                return false;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDateStr);
                Date end = sdf.parse(endDateStr);

                if (start.after(end)) {
                    JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期", "错误", JOptionPane.ERROR_MESSAGE);
                    startDateField.requestFocus();
                    return false;
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    /**
     * 根据选择获取日期范围
     *
     * @param selection 选择的时间范围
     * @return 日期范围数组[开始日期, 结束日期]，如果选择"所有时间"则返回null
     */
    private Date[] getDateRangeBySelection(String selection) {
        Date[] dateRange = null;

        switch (selection) {
            case "今天":
                dateRange = DateUtils.getTodayRange();
                break;
            case "本周":
                dateRange = DateUtils.getCurrentWeekRange();
                break;
            case "本月":
                dateRange = DateUtils.getCurrentMonthRange();
                break;
            case "上个月":
                dateRange = DateUtils.getLastMonthRange();
                break;
            case "本季度":
                dateRange = DateUtils.getCurrentQuarterRange();
                break;
            case "本年度":
                dateRange = DateUtils.getCurrentYearRange();
                break;
            default:
                dateRange = null;
        }

        return dateRange;
    }

    private List<Account> accounts;

    /**
     * 更新用户数据
     *
     * @param user 用户对象
     */
    public void updateUserData(User user) {
        this.currentUser = user;

        // 如果没有用户登录，清空数据
        if (user == null) {
            clearData();
            return;
        }

        // 加载用户账户
        loadUserAccounts();

        // 加载数据
        refreshData();
    }

    /**
     * 加载用户账户
     */
    private void loadUserAccounts() {
        if (currentUser == null) {
            return;
        }

        try {
            // 获取用户的账户列表
            accounts = accountService.getAccountsByUserId(currentUser.getId());

            // 更新账户下拉框
            accountComboBox.removeAllItems();
            accountComboBox.addItem("所有账户");

            for (Account account : accounts) {
                accountComboBox.addItem(account.getName());
            }
        } catch (Exception e) {
            LogUtils.error("加载用户账户失败", e);
        }
    }

    /**
     * 清空数据
     */
    private void clearData() {
        // 清空汇总数据
        updateSummaryLabels(0, 0, 0, 0, 0, 0);

        // 清空图表
        chartPanel.removeAll();
        JLabel emptyLabel = new JLabel("无数据", JLabel.CENTER);
        emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chartPanel.add(emptyLabel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();

        // 清空账户下拉框
        accountComboBox.removeAllItems();
        accountComboBox.addItem("所有账户");
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (currentUser == null) {
            return;
        }

        // 重置过滤条件
        chartTypeComboBox.setSelectedItem("柱状图");
        timeRangeComboBox.setSelectedItem("本月");
        accountComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedItem("所有类别");
        groupByComboBox.setSelectedItem("按日统计");

        // 设置默认日期范围（本月）
        Date[] monthRange = DateUtils.getCurrentMonthRange();
        startDate = monthRange[0];
        endDate = monthRange[1];
        startDateField.setText(DateUtils.formatDate(startDate));
        endDateField.setText(DateUtils.formatDate(endDate));

        // 生成统计数据
        generateStatistics();
    }

    /**
     * 生成统计数据
     */
    private void generateStatistics() {
        if (currentUser == null) {
            return;
        }

        // 显示加载提示
        chartPanel.removeAll();
        JLabel loadingLabel = new JLabel("正在加载数据...", JLabel.CENTER);
        loadingLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chartPanel.add(loadingLabel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();

        // 创建统计查询参数
        StatisticsQueryParam param = new StatisticsQueryParam(currentUser.getId());
        param.setStartDate(startDate);
        param.setEndDate(endDate);
        param.setChartType(selectedChartType);

        if (selectedAccountId > 0) {
            param.setAccountId(selectedAccountId);
        }

        String groupBy = "day";
        switch (selectedGroupBy) {
            case "按日统计":
                groupBy = "day";
                break;
            case "按周统计":
                groupBy = "week";
                break;
            case "按月统计":
                groupBy = "month";
                break;
            case "按季度统计":
                groupBy = "quarter";
                break;
            case "按年统计":
                groupBy = "year";
                break;
            case "按类别统计":
                groupBy = "category";
                break;
        }
        param.setGroupBy(groupBy);

        // 异步加载数据
        String finalGroupBy = groupBy;
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                Map<String, Object> result = new HashMap<>();

                if ("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory)) {
                    // 获取收入统计数据
                    if ("category".equals(finalGroupBy)) {
                        result.put("incomeCategoryData", incomeService.getIncomeByCategory(currentUser.getId(), startDate, endDate, selectedAccountId));
                    } else {
                        result.put("incomeTimeData", incomeService.getIncomeByTime(currentUser.getId(), startDate, endDate, finalGroupBy, selectedAccountId));
                    }

                    // 获取收入汇总数据
                    result.put("totalIncome", incomeService.getTotalIncomeByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                    result.put("avgIncome", incomeService.getAvgIncomeByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                    result.put("maxIncome", incomeService.getMaxIncomeByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                }

                if ("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory)) {
                    // 获取支出统计数据
                    if ("category".equals(finalGroupBy)) {
                        result.put("expenseCategoryData", expenseService.getExpenseByCategory(currentUser.getId(), startDate, endDate, selectedAccountId));
                    } else {
                        result.put("expenseTimeData", expenseService.getExpenseByTime(currentUser.getId(), startDate, endDate, finalGroupBy, selectedAccountId));
                    }

                    // 获取支出汇总数据
                    result.put("totalExpense", expenseService.getTotalExpenseByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                    result.put("avgExpense", expenseService.getAvgExpenseByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                    result.put("maxExpense", expenseService.getMaxExpenseByDateRange(currentUser.getId(), startDate, endDate, selectedAccountId));
                }

                return result;
            }

            @Override
            protected void done() {
                try {
                    Map<String, Object> result = get();

                    // 获取收入支出汇总数据
                    double totalIncome = result.containsKey("totalIncome") ? (Double) result.get("totalIncome") : 0.0;
                    double totalExpense = result.containsKey("totalExpense") ? (Double) result.get("totalExpense") : 0.0;
                    double avgIncome = result.containsKey("avgIncome") ? (Double) result.get("avgIncome") : 0.0;
                    double avgExpense = result.containsKey("avgExpense") ? (Double) result.get("avgExpense") : 0.0;
                    double maxIncome = result.containsKey("maxIncome") ? (Double) result.get("maxIncome") : 0.0;
                    double maxExpense = result.containsKey("maxExpense") ? (Double) result.get("maxExpense") : 0.0;

                    // 更新汇总标签
                    updateSummaryLabels(totalIncome, totalExpense, avgIncome, avgExpense, maxIncome, maxExpense);

                    // 创建图表
                    JPanel chartContainer = null;

                    if ("按类别统计".equals(selectedGroupBy)) {
                        // 类别统计
                        Map<String, Double> incomeCategoryData = (Map<String, Double>) result.get("incomeCategoryData");
                        Map<String, Double> expenseCategoryData = (Map<String, Double>) result.get("expenseCategoryData");

                        if ("饼图".equals(selectedChartType)) {
                            chartContainer = createCategoryPieChart(incomeCategoryData, expenseCategoryData);
                        } else {
                            chartContainer = createCategoryBarChart(incomeCategoryData, expenseCategoryData);
                        }
                    } else {
                        // 时间统计
                        Map<String, Double> incomeTimeData = (Map<String, Double>) result.get("incomeTimeData");
                        Map<String, Double> expenseTimeData = (Map<String, Double>) result.get("expenseTimeData");

                        if ("柱状图".equals(selectedChartType)) {
                            chartContainer = createTimeBarChart(incomeTimeData, expenseTimeData);
                        } else if ("折线图".equals(selectedChartType)) {
                            chartContainer = createTimeLineChart(incomeTimeData, expenseTimeData);
                        } else if ("堆叠柱状图".equals(selectedChartType)) {
                            chartContainer = createTimeStackedBarChart(incomeTimeData, expenseTimeData);
                        }
                    }

                    // 更新图表面板
                    chartPanel.removeAll();

                    if (chartContainer != null) {
                        chartPanel.add(chartContainer, BorderLayout.CENTER);
                    } else {
                        JLabel errorLabel = new JLabel("无法创建图表", JLabel.CENTER);
                        errorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                        chartPanel.add(errorLabel, BorderLayout.CENTER);
                    }

                    chartPanel.revalidate();
                    chartPanel.repaint();

                } catch (Exception e) {
                    LogUtils.error("生成统计数据失败", e);

                    // 显示错误信息
                    chartPanel.removeAll();
                    JLabel errorLabel = new JLabel("加载数据失败: " + e.getMessage(), JLabel.CENTER);
                    errorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                    chartPanel.add(errorLabel, BorderLayout.CENTER);
                    chartPanel.revalidate();
                    chartPanel.repaint();
                }
            }
        };

        worker.execute();
    }

    /**
     * 更新汇总标签
     *
     * @param totalIncome 总收入
     * @param totalExpense 总支出
     * @param avgIncome 平均收入
     * @param avgExpense 平均支出
     * @param maxIncome 最高收入
     * @param maxExpense 最高支出
     */
    private void updateSummaryLabels(double totalIncome, double totalExpense, double avgIncome, double avgExpense, double maxIncome, double maxExpense) {
        // 计算净收入
        double netIncome = totalIncome - totalExpense;

        // 更新标签
        Component[] components = summaryPanel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] panelComponents = panel.getComponents();

                for (Component panelComponent : panelComponents) {
                    if (panelComponent instanceof JLabel) {
                        JLabel label = (JLabel) panelComponent;

                        if (label.getText().equals("总收入")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(totalIncome));
                        } else if (label.getText().equals("总支出")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(totalExpense));
                        } else if (label.getText().equals("净收入")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(netIncome));

                            // 根据净收入的正负设置颜色
                            if (netIncome < 0) {
                                valueLabel.setForeground(new Color(217, 83, 79)); // 红色
                            } else {
                                valueLabel.setForeground(new Color(66, 139, 202)); // 蓝色
                            }
                        } else if (label.getText().equals("平均收入")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(avgIncome));
                        } else if (label.getText().equals("平均支出")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(avgExpense));
                        } else if (label.getText().equals("最高收入")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(maxIncome));
                        } else if (label.getText().equals("最高支出")) {
                            JLabel valueLabel = (JLabel) panel.getComponent(1);
                            valueLabel.setText(StringUtils.formatCurrency(maxExpense));
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建类别饼图
     *
     * @param incomeCategoryData 收入类别数据
     * @param expenseCategoryData 支出类别数据
     * @return 饼图面板
     */
    private JPanel createCategoryPieChart(Map<String, Double> incomeCategoryData, Map<String, Double> expenseCategoryData) {
        JPanel container = new JPanel(new GridLayout(1, 2, 10, 0));
        container.setOpaque(false);

        // 检查数据是否为空
        boolean hasIncomeData = incomeCategoryData != null && !incomeCategoryData.isEmpty();
        boolean hasExpenseData = expenseCategoryData != null && !expenseCategoryData.isEmpty();

        if (!hasIncomeData && !hasExpenseData) {
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            container.add(emptyLabel);
            return container;
        }

        // 创建收入饼图
        if (hasIncomeData && ("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory))) {
            DefaultPieDataset incomeDataset = new DefaultPieDataset();

            for (Map.Entry<String, Double> entry : incomeCategoryData.entrySet()) {
                if (entry.getValue() > 0) {
                    incomeDataset.setValue(entry.getKey(), entry.getValue());
                }
            }

            JFreeChart incomeChart = ChartFactory.createPieChart(
                    "收入分类",        // 图表标题
                    incomeDataset,     // 数据集
                    true,              // 是否显示图例
                    true,              // 是否生成工具提示
                    false              // 是否生成URL链接
            );

            // 设置收入饼图样式
            incomeChart.setBackgroundPaint(Color.WHITE);
            PiePlot incomePlot = (PiePlot) incomeChart.getPlot();
            incomePlot.setBackgroundPaint(Color.WHITE);
            incomePlot.setOutlineVisible(false);
            incomePlot.setSectionPaint("工资", new Color(92, 184, 92));
            incomePlot.setSectionPaint("奖金", new Color(66, 139, 202));
            incomePlot.setSectionPaint("投资收益", new Color(91, 192, 222));

            // 创建图表面板
            ChartPanel incomeChartPanel = new ChartPanel(incomeChart);
            container.add(incomeChartPanel);
        }

        // 创建支出饼图
        if (hasExpenseData && ("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory))) {
            DefaultPieDataset expenseDataset = new DefaultPieDataset();

            for (Map.Entry<String, Double> entry : expenseCategoryData.entrySet()) {
                if (entry.getValue() > 0) {
                    expenseDataset.setValue(entry.getKey(), entry.getValue());
                }
            }

            JFreeChart expenseChart = ChartFactory.createPieChart(
                    "支出分类",        // 图表标题
                    expenseDataset,    // 数据集
                    true,              // 是否显示图例
                    true,              // 是否生成工具提示
                    false              // 是否生成URL链接
            );

            // 设置支出饼图样式
            expenseChart.setBackgroundPaint(Color.WHITE);
            PiePlot expensePlot = (PiePlot) expenseChart.getPlot();
            expensePlot.setBackgroundPaint(Color.WHITE);
            expensePlot.setOutlineVisible(false);
            expensePlot.setSectionPaint("餐饮", new Color(217, 83, 79));
            expensePlot.setSectionPaint("交通", new Color(240, 173, 78));
            expensePlot.setSectionPaint("购物", new Color(153, 102, 255));

            // 创建图表面板
            ChartPanel expenseChartPanel = new ChartPanel(expenseChart);
            container.add(expenseChartPanel);
        }

        return container;
    }

    /**
     * 创建类别柱状图
     *
     * @param incomeCategoryData 收入类别数据
     * @param expenseCategoryData 支出类别数据
     * @return 柱状图面板
     */
    private JPanel createCategoryBarChart(Map<String, Double> incomeCategoryData, Map<String, Double> expenseCategoryData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // 检查数据是否为空
        boolean hasIncomeData = incomeCategoryData != null && !incomeCategoryData.isEmpty();
        boolean hasExpenseData = expenseCategoryData != null && !expenseCategoryData.isEmpty();

        if (!hasIncomeData && !hasExpenseData) {
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            container.add(emptyLabel, BorderLayout.CENTER);
            return container;
        }

        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 添加收入数据
        if (hasIncomeData && ("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : incomeCategoryData.entrySet()) {
                if (entry.getValue() > 0) {
                    dataset.addValue(entry.getValue(), "收入", entry.getKey());
                }
            }
        }

        // 添加支出数据
        if (hasExpenseData && ("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : expenseCategoryData.entrySet()) {
                if (entry.getValue() > 0) {
                    dataset.addValue(entry.getValue(), "支出", entry.getKey());
                }
            }
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                "类别统计",                    // 图表标题
                "类别",                        // X轴标签
                "金额",                        // Y轴标签
                dataset,                      // 数据集
                PlotOrientation.VERTICAL,     // 图表方向
                true,                         // 是否显示图例
                true,                         // 是否生成工具提示
                false                         // 是否生成URL链接
        );

        // 设置图表样式
        chart.setBackgroundPaint(Color.WHITE);

        // 获取绘图区域
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置渲染器
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(92, 184, 92));    // 收入颜色：绿色
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(217, 83, 79));    // 支出颜色：红色
        }

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * 创建时间柱状图
     *
     * @param incomeTimeData 收入时间数据
     * @param expenseTimeData 支出时间数据
     * @return 柱状图面板
     */
    private JPanel createTimeBarChart(Map<String, Double> incomeTimeData, Map<String, Double> expenseTimeData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // 检查数据是否为空
        boolean hasIncomeData = incomeTimeData != null && !incomeTimeData.isEmpty();
        boolean hasExpenseData = expenseTimeData != null && !expenseTimeData.isEmpty();

        if (!hasIncomeData && !hasExpenseData) {
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            container.add(emptyLabel, BorderLayout.CENTER);
            return container;
        }

        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 添加收入数据
        if (hasIncomeData && ("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : incomeTimeData.entrySet()) {
                dataset.addValue(entry.getValue(), "收入", entry.getKey());
            }
        }

        // 添加支出数据
        if (hasExpenseData && ("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : expenseTimeData.entrySet()) {
                dataset.addValue(entry.getValue(), "支出", entry.getKey());
            }
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                getChartTitle(),                // 图表标题
                getXAxisLabel(),                // X轴标签
                "金额",                         // Y轴标签
                dataset,                       // 数据集
                PlotOrientation.VERTICAL,      // 图表方向
                true,                          // 是否显示图例
                true,                          // 是否生成工具提示
                false                          // 是否生成URL链接
        );

        // 设置图表样式
        chart.setBackgroundPaint(Color.WHITE);

        // 获取绘图区域
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置渲染器
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(92, 184, 92));    // 收入颜色：绿色
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(217, 83, 79));    // 支出颜色：红色
        }

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * 创建时间折线图
     *
     * @param incomeTimeData 收入时间数据
     * @param expenseTimeData 支出时间数据
     * @return 折线图面板
     */
    private JPanel createTimeLineChart(Map<String, Double> incomeTimeData, Map<String, Double> expenseTimeData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // 检查数据是否为空
        boolean hasIncomeData = incomeTimeData != null && !incomeTimeData.isEmpty();
        boolean hasExpenseData = expenseTimeData != null && !expenseTimeData.isEmpty();

        if (!hasIncomeData && !hasExpenseData) {
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            container.add(emptyLabel, BorderLayout.CENTER);
            return container;
        }

        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 添加收入数据
        if (hasIncomeData && ("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : incomeTimeData.entrySet()) {
                dataset.addValue(entry.getValue(), "收入", entry.getKey());
            }
        }

        // 添加支出数据
        if (hasExpenseData && ("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory))) {
            for (Map.Entry<String, Double> entry : expenseTimeData.entrySet()) {
                dataset.addValue(entry.getValue(), "支出", entry.getKey());
            }
        }

        // 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
                getChartTitle(),                // 图表标题
                getXAxisLabel(),                // X轴标签
                "金额",                         // Y轴标签
                dataset,                       // 数据集
                PlotOrientation.VERTICAL,      // 图表方向
                true,                          // 是否显示图例
                true,                          // 是否生成工具提示
                false                          // 是否生成URL链接
        );

        // 设置图表样式
        chart.setBackgroundPaint(Color.WHITE);

        // 获取绘图区域
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置渲染器
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(92, 184, 92));    // 收入颜色：绿色
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(217, 83, 79));    // 支出颜色：红色
        }

        // 设置显示数据点
        renderer.setDefaultShapesVisible(true);

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * 创建时间堆叠柱状图
     *
     * @param incomeTimeData 收入时间数据
     * @param expenseTimeData 支出时间数据
     * @return 堆叠柱状图面板
     */
    private JPanel createTimeStackedBarChart(Map<String, Double> incomeTimeData, Map<String, Double> expenseTimeData) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // 检查数据是否为空
        boolean hasIncomeData = incomeTimeData != null && !incomeTimeData.isEmpty();
        boolean hasExpenseData = expenseTimeData != null && !expenseTimeData.isEmpty();

        if (!hasIncomeData && !hasExpenseData) {
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            container.add(emptyLabel, BorderLayout.CENTER);
            return container;
        }

        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 获取所有时间点
        TreeMap<String, Double> sortedTimes = new TreeMap<>();

        if (hasIncomeData) {
            for (String time : incomeTimeData.keySet()) {
                sortedTimes.put(time, 0.0);
            }
        }

        if (hasExpenseData) {
            for (String time : expenseTimeData.keySet()) {
                sortedTimes.put(time, 0.0);
            }
        }

        // 添加净收入数据
        for (String time : sortedTimes.keySet()) {
            double income = hasIncomeData && incomeTimeData.containsKey(time) ? incomeTimeData.get(time) : 0;
            double expense = hasExpenseData && expenseTimeData.containsKey(time) ? expenseTimeData.get(time) : 0;

            if (("所有类别".equals(selectedCategory) || "收入".equals(selectedCategory)) && income > 0) {
                dataset.addValue(income, "收入", time);
            }

            if (("所有类别".equals(selectedCategory) || "支出".equals(selectedCategory)) && expense > 0) {
                dataset.addValue(expense, "支出", time);
            }
        }

        // 创建堆叠柱状图
        JFreeChart chart = ChartFactory.createStackedBarChart(
                getChartTitle(),                // 图表标题
                getXAxisLabel(),                // X轴标签
                "金额",                         // Y轴标签
                dataset,                       // 数据集
                PlotOrientation.VERTICAL,      // 图表方向
                true,                          // 是否显示图例
                true,                          // 是否生成工具提示
                false                          // 是否生成URL链接
        );

        // 设置图表样式
        chart.setBackgroundPaint(Color.WHITE);

        // 获取绘图区域
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置渲染器
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(92, 184, 92));    // 收入颜色：绿色
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(217, 83, 79));    // 支出颜色：红色
        }

        // 创建图表面板
        ChartPanel chartPanel = new ChartPanel(chart);
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * 获取图表标题
     *
     * @return 图表标题
     */
    private String getChartTitle() {
        String dateRangeStr = DateUtils.formatDate(startDate) + " ~ " + DateUtils.formatDate(endDate);

        if ("所有类别".equals(selectedCategory)) {
            return "收支统计 (" + dateRangeStr + ")";
        } else if ("收入".equals(selectedCategory)) {
            return "收入统计 (" + dateRangeStr + ")";
        } else {
            return "支出统计 (" + dateRangeStr + ")";
        }
    }

    /**
     * 获取X轴标签
     *
     * @return X轴标签
     */
    private String getXAxisLabel() {
        switch (selectedGroupBy) {
            case "按日统计":
                return "日期";
            case "按周统计":
                return "周";
            case "按月统计":
                return "月份";
            case "按季度统计":
                return "季度";
            case "按年统计":
                return "年份";
            default:
                return "";
        }
    }
}