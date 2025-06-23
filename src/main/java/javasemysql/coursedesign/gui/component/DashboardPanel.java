package javasemysql.coursedesign.gui.component;

import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Bill;
import javasemysql.coursedesign.model.Budget;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.BillService;
import javasemysql.coursedesign.service.BudgetService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.service.IncomeService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.service.impl.BillServiceImpl;
import javasemysql.coursedesign.service.impl.BudgetServiceImpl;
import javasemysql.coursedesign.service.impl.ExpenseServiceImpl;
import javasemysql.coursedesign.service.impl.IncomeServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 仪表盘面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class DashboardPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(DashboardPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;

    // 服务
    private AccountService accountService;
    private IncomeService incomeService;
    private ExpenseService expenseService;
    private BudgetService budgetService;
    private BillService billService;

    // 日期范围
    private Date startDate;
    private Date endDate;

    // UI组件
    private JComboBox<String> timeRangeComboBox;
    private JLabel totalBalanceLabel;
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel netCashflowLabel;
    private JPanel incomeExpenseChartPanel;
    private JPanel categoryChartPanel;
    private JPanel accountsPanel;
    private JPanel upcomingBillsPanel;
    private JPanel budgetAlertPanel;

    // 账单表格
    private JTable upcomingBillsTable;
    private DefaultTableModel billsTableModel;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // 初始化服务
        this.accountService = new AccountServiceImpl();
        this.incomeService = new IncomeServiceImpl();
        this.expenseService = new ExpenseServiceImpl();
        this.budgetService = new BudgetServiceImpl();
        this.billService = new BillServiceImpl();

        // 设置默认日期范围为本月
        Date[] monthRange = DateUtils.getCurrentMonthRange();
        this.startDate = monthRange[0];
        this.endDate = monthRange[1];

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
        JLabel titleLabel = new JLabel("财务总览");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建时间范围选择器
        JPanel timeRangePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timeRangePanel.setOpaque(false);

        JLabel timeRangeLabel = new JLabel("时间范围:");
        timeRangeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        timeRangeComboBox = new JComboBox<>(new String[]{"本月", "上月", "最近3个月", "今年", "自定义"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        timeRangeComboBox.setPreferredSize(new Dimension(120, 30));

        timeRangePanel.add(timeRangeLabel);
        timeRangePanel.add(timeRangeComboBox);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(timeRangePanel, BorderLayout.EAST);

        // 创建摘要卡片面板
        JPanel summaryPanel = createSummaryPanel();

        // 创建主内容面板（使用网格布局）
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // 收入支出趋势图
        incomeExpenseChartPanel = new JPanel(new BorderLayout());
        incomeExpenseChartPanel.setBorder(createPanelBorder("收入支出趋势"));
        incomeExpenseChartPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        contentPanel.add(incomeExpenseChartPanel, gbc);

        // 收入支出类别分布
        categoryChartPanel = new JPanel(new BorderLayout());
        categoryChartPanel.setBorder(createPanelBorder("收支类别分布"));
        categoryChartPanel.setBackground(Color.WHITE);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        contentPanel.add(categoryChartPanel, gbc);

        // 账户概览
        accountsPanel = new JPanel(new BorderLayout());
        accountsPanel.setBorder(createPanelBorder("账户概览"));
        accountsPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        contentPanel.add(accountsPanel, gbc);

        // 创建即将到期账单面板
        upcomingBillsPanel = new JPanel(new BorderLayout());
        upcomingBillsPanel.setBorder(createPanelBorder("即将到期账单"));
        upcomingBillsPanel.setBackground(Color.WHITE);

        // 创建账单表格
        createBillsTable();
        JScrollPane billsScrollPane = new JScrollPane(upcomingBillsTable);
        billsScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        upcomingBillsPanel.add(billsScrollPane, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        contentPanel.add(upcomingBillsPanel, gbc);

        // 创建预算警告面板
        budgetAlertPanel = new JPanel();
        budgetAlertPanel.setBorder(createPanelBorder("预算警告"));
        budgetAlertPanel.setLayout(new BoxLayout(budgetAlertPanel, BoxLayout.Y_AXIS));
        budgetAlertPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        contentPanel.add(budgetAlertPanel, gbc);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(summaryPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建摘要面板
     *
     * @return 摘要面板
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // 总余额卡片
        JPanel balanceCard = createSummaryCard("总资产", "¥0.00", new Color(66, 139, 202));
        totalBalanceLabel = (JLabel) ((JPanel) balanceCard.getComponent(0)).getComponent(1);

        // 总收入卡片
        JPanel incomeCard = createSummaryCard("总收入", "¥0.00", new Color(92, 184, 92));
        totalIncomeLabel = (JLabel) ((JPanel) incomeCard.getComponent(0)).getComponent(1);

        // 总支出卡片
        JPanel expenseCard = createSummaryCard("总支出", "¥0.00", new Color(217, 83, 79));
        totalExpenseLabel = (JLabel) ((JPanel) expenseCard.getComponent(0)).getComponent(1);

        // 净现金流卡片
        JPanel cashflowCard = createSummaryCard("净现金流", "¥0.00", new Color(240, 173, 78));
        netCashflowLabel = (JLabel) ((JPanel) cashflowCard.getComponent(0)).getComponent(1);

        panel.add(balanceCard);
        panel.add(incomeCard);
        panel.add(expenseCard);
        panel.add(cashflowCard);

        return panel;
    }

    /**
     * 创建摘要卡片
     *
     * @param title 标题
     * @param value 值
     * @param color 颜色
     * @return 卡片面板
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        valueLabel.setForeground(color);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * 创建面板边框
     *
     * @param title 标题
     * @return 边框
     */
    private TitledBorder createPanelBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        );
    }

    /**
     * 创建账单表格
     */
    private void createBillsTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "账户", "类别", "金额", "到期日期", "状态"};

        // 创建表格模型
        billsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Double.class; // 设置金额列为Double类型，以便正确排序
                } else if (columnIndex == 4) {
                    return Date.class; // 设置日期列为Date类型，以便正确排序
                }
                return String.class;
            }
        };

        // 创建表格
        upcomingBillsTable = new JTable(billsTableModel);
        upcomingBillsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        upcomingBillsTable.setRowHeight(30);
        upcomingBillsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置表格样式
        upcomingBillsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        upcomingBillsTable.getTableHeader().setReorderingAllowed(false);
        upcomingBillsTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        upcomingBillsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        upcomingBillsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        upcomingBillsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        upcomingBillsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        upcomingBillsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        upcomingBillsTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // 隐藏ID列
        upcomingBillsTable.getColumnModel().getColumn(0).setMinWidth(0);
        upcomingBillsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        upcomingBillsTable.getColumnModel().getColumn(0).setWidth(0);

        // 设置金额列的单元格渲染器，以便格式化显示
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) comp;

                if (value != null) {
                    double amount = (Double) value;
                    label.setText(StringUtils.formatCurrency(amount));
                    label.setForeground(new Color(217, 83, 79));  // 红色
                }

                label.setHorizontalAlignment(JLabel.RIGHT);
                return label;
            }
        };

        // 设置日期列的单元格渲染器，以便格式化显示
        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        // 设置状态列的单元格渲染器，以便显示不同颜色
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) comp;

                if (value != null) {
                    String status = value.toString();
                    if ("已逾期".equals(status)) {
                        label.setForeground(new Color(217, 83, 79));  // 红色
                    } else if ("即将到期".equals(status)) {
                        label.setForeground(new Color(240, 173, 78));  // 黄色
                    } else {
                        label.setForeground(new Color(92, 184, 92));  // 绿色
                    }
                }

                return label;
            }
        };

        // 应用渲染器
        upcomingBillsTable.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);
        upcomingBillsTable.getColumnModel().getColumn(4).setCellRenderer(dateRenderer);
        upcomingBillsTable.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> {
            if ("自定义".equals(timeRangeComboBox.getSelectedItem())) {
                showDateRangeDialog();
            } else {
                updateDateRange();
                refreshData();
            }
        });

        // 账单表格双击事件
        upcomingBillsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && upcomingBillsTable.getSelectedRow() != -1) {
                    int billId = (int) upcomingBillsTable.getValueAt(upcomingBillsTable.getSelectedRow(), 0);
                    mainFrame.showBillTab(billId);
                }
            }
        });
    }

    /**
     * 更新日期范围
     */
    private void updateDateRange() {
        String selectedRange = (String) timeRangeComboBox.getSelectedItem();

        switch (selectedRange) {
            case "本月":
                Date[] currentMonth = DateUtils.getCurrentMonthRange();
                startDate = currentMonth[0];
                endDate = currentMonth[1];
                break;
            case "上月":
                Date[] lastMonth = DateUtils.getLastMonthRange();
                startDate = lastMonth[0];
                endDate = lastMonth[1];
                break;
            case "最近3个月":
                Calendar cal = Calendar.getInstance();
                endDate = cal.getTime();
                cal.add(Calendar.MONTH, -3);
                startDate = cal.getTime();
                break;
            case "今年":
                Date[] currentYear = DateUtils.getCurrentYearRange();
                startDate = currentYear[0];
                endDate = currentYear[1];
                break;
        }
    }

    /**
     * 显示日期范围对话框
     */
    private void showDateRangeDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel startLabel = new JLabel("开始日期 (yyyy-MM-dd):");
        JTextField startField = new JTextField(10);

        JLabel endLabel = new JLabel("结束日期 (yyyy-MM-dd):");
        JTextField endField = new JTextField(10);

        // 默认显示当前选择的日期范围
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        startField.setText(sdf.format(startDate));
        endField.setText(sdf.format(endDate));

        panel.add(startLabel);
        panel.add(startField);
        panel.add(endLabel);
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "选择日期范围",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                Date newStartDate = sdf.parse(startField.getText());
                Date newEndDate = sdf.parse(endField.getText());

                if (newStartDate.after(newEndDate)) {
                    JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期", "错误", JOptionPane.ERROR_MESSAGE);
                    timeRangeComboBox.setSelectedIndex(0); // 重置为"本月"
                    return;
                }

                startDate = newStartDate;
                endDate = newEndDate;
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                timeRangeComboBox.setSelectedIndex(0); // 重置为"本月"
            }
        } else {
            timeRangeComboBox.setSelectedIndex(0); // 重置为"本月"
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (currentUser == null) {
            return;
        }

        // 异步加载数据
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 更新摘要数据
                updateSummaryData();

                // 更新收入支出趋势图
                updateIncomeExpenseChart();

                // 更新类别分布图
                updateCategoryChart();

                // 更新账户概览
                updateAccountsPanel();

                // 更新即将到期账单
                updateUpcomingBills();

                // 更新预算警告
                updateBudgetAlerts();

                return null;
            }
        };

        worker.execute();
    }

    /**
     * 更新摘要数据
     */
    private void updateSummaryData() {
        try {
            // 获取总资产
            double totalBalance = accountService.getTotalBalance(currentUser.getId());

            // 获取总收入
            double totalIncome = incomeService.getTotalIncomeByDateRange(currentUser.getId(), startDate, endDate);

            // 获取总支出
            double totalExpense = expenseService.getTotalExpenseByDateRange(currentUser.getId(), startDate, endDate);

            // 计算净现金流
            double netCashflow = totalIncome - totalExpense;

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                totalBalanceLabel.setText(StringUtils.formatCurrency(totalBalance));
                totalIncomeLabel.setText(StringUtils.formatCurrency(totalIncome));
                totalExpenseLabel.setText(StringUtils.formatCurrency(totalExpense));
                netCashflowLabel.setText(StringUtils.formatCurrency(netCashflow));

                // 设置净现金流颜色
                if (netCashflow >= 0) {
                    netCashflowLabel.setForeground(new Color(92, 184, 92)); // 绿色
                } else {
                    netCashflowLabel.setForeground(new Color(217, 83, 79)); // 红色
                }
            });
        } catch (Exception e) {
            LogUtils.error("更新摘要数据失败", e);
        }
    }

    /**
     * 更新收入支出趋势图
     */
    private void updateIncomeExpenseChart() {
        try {
            // 获取收入数据
            Map<String, Double> incomeData = incomeService.getIncomeByTime(
                    currentUser.getId(), startDate, endDate, "month");

            // 获取支出数据
            Map<String, Double> expenseData = expenseService.getExpenseByTime(
                    currentUser.getId(), startDate, endDate, "month");

            // 创建图表数据集
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // 添加收入数据
            for (Map.Entry<String, Double> entry : incomeData.entrySet()) {
                dataset.addValue(entry.getValue(), "收入", entry.getKey());
            }

            // 添加支出数据
            for (Map.Entry<String, Double> entry : expenseData.entrySet()) {
                dataset.addValue(entry.getValue(), "支出", entry.getKey());
            }

            // 创建柱状图
            JFreeChart chart = ChartFactory.createBarChart(
                    null,                // 图表标题
                    "月份",               // X轴标签
                    "金额 (¥)",           // Y轴标签
                    dataset,             // 数据集
                    PlotOrientation.VERTICAL, // 图表方向
                    true,                // 是否显示图例
                    true,                // 是否生成工具提示
                    false                // 是否生成URL链接
            );

            // 设置图表样式
            chart.setBackgroundPaint(Color.WHITE);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(new Color(220, 220, 220));
            plot.setRangeGridlinePaint(new Color(220, 220, 220));

            // 设置柱状图颜色
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(92, 184, 92)); // 收入为绿色
            renderer.setSeriesPaint(1, new Color(217, 83, 79)); // 支出为红色

            // 创建图表面板
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(400, 300));

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                incomeExpenseChartPanel.removeAll();
                incomeExpenseChartPanel.add(chartPanel, BorderLayout.CENTER);
                incomeExpenseChartPanel.revalidate();
                incomeExpenseChartPanel.repaint();
            });
        } catch (Exception e) {
            LogUtils.error("更新收入支出趋势图失败", e);
        }
    }

    /**
     * 更新类别分布图
     */
    private void updateCategoryChart() {
        try {
            // 获取收入类别分布
            Map<String, Double> incomeCategories = incomeService.getIncomeByCategory(
                    currentUser.getId(), startDate, endDate);

            // 获取支出类别分布
            Map<String, Double> expenseCategories = expenseService.getExpenseByCategory(
                    currentUser.getId(), startDate, endDate);

            // 创建面板
            JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 0));
            chartContainer.setOpaque(false);

            // 创建收入饼图
            JPanel incomeChartPanel = createPieChart("收入分布", incomeCategories, new Color(92, 184, 92));

            // 创建支出饼图
            JPanel expenseChartPanel = createPieChart("支出分布", expenseCategories, new Color(217, 83, 79));

            chartContainer.add(incomeChartPanel);
            chartContainer.add(expenseChartPanel);

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                categoryChartPanel.removeAll();
                categoryChartPanel.add(chartContainer, BorderLayout.CENTER);
                categoryChartPanel.revalidate();
                categoryChartPanel.repaint();
            });
        } catch (Exception e) {
            LogUtils.error("更新类别分布图失败", e);
        }
    }

    /**
     * 创建饼图
     *
     * @param title 标题
     * @param data 数据
     * @param mainColor 主色调
     * @return 饼图面板
     */
    private JPanel createPieChart(String title, Map<String, Double> data, Color mainColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 创建数据集
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                title,               // 图表标题
                dataset,             // 数据集
                true,                // 是否显示图例
                true,                // 是否生成工具提示
                false                // 是否生成URL链接
        );

        // 设置饼图样式
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionPaint(0, mainColor);

        // 创建饼图面板
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 更新账户概览
     */
    private void updateAccountsPanel() {
        try {
            // 获取用户所有账户
            List<Account> accounts = accountService.getAccountsByUserId(currentUser.getId());

            // 创建账户面板
            JPanel accountsContainer = new JPanel();
            accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
            accountsContainer.setOpaque(false);
            accountsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

            if (accounts.isEmpty()) {
                JLabel emptyLabel = new JLabel("没有账户数据", JLabel.CENTER);
                emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                emptyLabel.setForeground(new Color(120, 120, 120));
                accountsContainer.add(emptyLabel);
            } else {
                for (Account account : accounts) {
                    JPanel accountCard = createAccountCard(account);
                    accountsContainer.add(accountCard);
                    accountsContainer.add(Box.createVerticalStrut(10)); // 添加间隔
                }
            }

            // 创建滚动面板
            JScrollPane scrollPane = new JScrollPane(accountsContainer);
            scrollPane.setBorder(null);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                accountsPanel.removeAll();
                accountsPanel.add(scrollPane, BorderLayout.CENTER);
                accountsPanel.revalidate();
                accountsPanel.repaint();
            });
        } catch (Exception e) {
            LogUtils.error("更新账户概览失败", e);
        }
    }

    /**
     * 创建账户卡片
     *
     * @param account 账户
     * @return 账户卡片面板
     */
    private JPanel createAccountCard(Account account) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // 账户图标和名称
        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/images/account_icon.png")));
        JLabel nameLabel = new JLabel(account.getName());
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        leftPanel.add(iconLabel, BorderLayout.WEST);
        leftPanel.add(nameLabel, BorderLayout.CENTER);

        // 账户余额
        JLabel balanceLabel = new JLabel(StringUtils.formatCurrency(account.getBalance()));
        balanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        balanceLabel.setHorizontalAlignment(JLabel.RIGHT);

        // 根据余额设置颜色
        if (account.getBalance() >= 0) {
            balanceLabel.setForeground(new Color(92, 184, 92)); // 绿色
        } else {
            balanceLabel.setForeground(new Color(217, 83, 79)); // 红色
        }

        card.add(leftPanel, BorderLayout.WEST);
        card.add(balanceLabel, BorderLayout.EAST);

        // 添加点击事件，跳转到账户页面
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showAccountTab(account.getId());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    /**
     * 更新即将到期账单
     */
    private void updateUpcomingBills() {
        try {
            // 清空表格
            billsTableModel.setRowCount(0);

            // 获取即将到期的账单（7天内）
            List<Bill> upcomingBills = billService.getUpcomingBills(currentUser.getId(), 7);

            // 获取逾期账单
            List<Bill> overdueBills = billService.getOverdueBills(currentUser.getId());

            // 添加逾期账单
            for (Bill bill : overdueBills) {
                Vector<Object> row = new Vector<>();
                row.add(bill.getId());
                row.add(bill.getAmount());
                row.add(bill.getDueDate());
                row.add("已逾期");

                billsTableModel.addRow(row);
            }

            // 添加即将到期账单
            for (Bill bill : upcomingBills) {
                Vector<Object> row = new Vector<>();
                row.add(bill.getId());
                row.add(bill.getAmount());
                row.add(bill.getDueDate());

                // 计算剩余天数
                int daysLeft = 0;
                String status = daysLeft <= 3 ? "即将到期" : "未付款";

                row.add(status);

                billsTableModel.addRow(row);
            }

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                if (overdueBills.isEmpty() && upcomingBills.isEmpty()) {
                    JLabel emptyLabel = new JLabel("没有即将到期的账单", JLabel.CENTER);
                    emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                    emptyLabel.setForeground(new Color(120, 120, 120));

                    upcomingBillsPanel.removeAll();
                    upcomingBillsPanel.add(emptyLabel, BorderLayout.CENTER);
                } else {
                    upcomingBillsPanel.removeAll();
                    upcomingBillsPanel.add(new JScrollPane(upcomingBillsTable), BorderLayout.CENTER);
                }

                upcomingBillsPanel.revalidate();
                upcomingBillsPanel.repaint();
            });
        } catch (Exception e) {
            LogUtils.error("更新即将到期账单失败", e);
        }
    }

    /**
     * 更新预算警告
     */
    private void updateBudgetAlerts() {
        try {
            // 获取当前活跃的预算
            List<Budget> activeBudgets = budgetService.getActiveBudgets(currentUser.getId());

            // 创建预算警告面板
            JPanel alertsContainer = new JPanel();
            alertsContainer.setLayout(new BoxLayout(alertsContainer, BoxLayout.Y_AXIS));
            alertsContainer.setOpaque(false);
            alertsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

            boolean hasWarnings = false;

            for (Budget budget : activeBudgets) {
                // 计算使用百分比
                double usagePercentage = budget.getUsagePercentage();

                // 检查是否超出预算或接近预算限制
                if (usagePercentage >= 80) {
                    JPanel alertCard = createBudgetAlertCard(budget);
                    alertsContainer.add(alertCard);
                    alertsContainer.add(Box.createVerticalStrut(10)); // 添加间隔
                    hasWarnings = true;
                }
            }

            if (!hasWarnings) {
                JLabel emptyLabel = new JLabel("没有预算警告", JLabel.CENTER);
                emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                emptyLabel.setForeground(new Color(120, 120, 120));
                alertsContainer.add(emptyLabel);
            }

            // 创建滚动面板
            JScrollPane scrollPane = new JScrollPane(alertsContainer);
            scrollPane.setBorder(null);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            // 更新UI
            SwingUtilities.invokeLater(() -> {
                budgetAlertPanel.removeAll();
                budgetAlertPanel.add(scrollPane);
                budgetAlertPanel.revalidate();
                budgetAlertPanel.repaint();
            });
        } catch (Exception e) {
            LogUtils.error("更新预算警告失败", e);
        }
    }

    /**
     * 创建预算警告卡片
     *
     * @param budget 预算
     * @return 预算警告卡片面板
     */
    private JPanel createBudgetAlertCard(Budget budget) {
        JPanel card = new JPanel(new BorderLayout(10, 0));

        // 判断警告级别
        double usagePercentage = budget.getUsagePercentage();
        boolean isOverBudget = usagePercentage >= 100;

        // 设置背景颜色
        if (isOverBudget) {
            card.setBackground(new Color(255, 235, 235)); // 浅红色
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(217, 83, 79), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        } else {
            card.setBackground(new Color(255, 248, 230)); // 浅黄色
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(240, 173, 78), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        }

        // 创建警告图标和类别
        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
        leftPanel.setOpaque(false);

        ImageIcon icon = new ImageIcon(getClass().getResource(
                isOverBudget ? "/images/alert_icon.png" : "/images/warning_icon.png"));
        JLabel iconLabel = new JLabel(icon);

        JLabel categoryLabel = new JLabel(budget.getCategory() + " 类别预算");
        categoryLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        leftPanel.add(iconLabel, BorderLayout.WEST);
        leftPanel.add(categoryLabel, BorderLayout.CENTER);

        // 创建警告信息
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setOpaque(false);

        // 警告文本
        String alertText = isOverBudget ?
                "已超出预算 " + String.format("%.1f", usagePercentage - 100) + "%" :
                "已使用 " + String.format("%.1f", usagePercentage) + "%";

        JLabel alertLabel = new JLabel(alertText);
        alertLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        alertLabel.setForeground(isOverBudget ? new Color(217, 83, 79) : new Color(240, 173, 78));

        // 金额信息
        String amountText = StringUtils.formatCurrency(budget.getUsedAmount()) + " / " +
                StringUtils.formatCurrency(budget.getAmount());

        JLabel amountLabel = new JLabel(amountText);
        amountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        rightPanel.add(alertLabel);
        rightPanel.add(amountLabel);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);

        // 添加点击事件，跳转到预算页面
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showBudgetTab(budget.getId());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return card;
    }

    /**
     * 更新用户数据
     *
     * @param user 用户对象
     */
    public void updateUserData(User user) {
        this.currentUser = user;

        // 如果没有用户登录，清空面板
        if (user == null) {
            clearAllPanels();
            return;
        }

        // 加载数据
        refreshData();
    }

    /**
     * 清空所有面板
     */
    private void clearAllPanels() {
        // 清空摘要标签
        totalBalanceLabel.setText("¥0.00");
        totalIncomeLabel.setText("¥0.00");
        totalExpenseLabel.setText("¥0.00");
        netCashflowLabel.setText("¥0.00");

        // 清空图表面板
        incomeExpenseChartPanel.removeAll();
        incomeExpenseChartPanel.revalidate();
        incomeExpenseChartPanel.repaint();

        categoryChartPanel.removeAll();
        categoryChartPanel.revalidate();
        categoryChartPanel.repaint();

        // 清空账户面板
        accountsPanel.removeAll();
        accountsPanel.revalidate();
        accountsPanel.repaint();

        // 清空账单表格
        billsTableModel.setRowCount(0);
        upcomingBillsPanel.removeAll();
        upcomingBillsPanel.add(new JScrollPane(upcomingBillsTable), BorderLayout.CENTER);
        upcomingBillsPanel.revalidate();
        upcomingBillsPanel.repaint();

        // 清空预算警告面板
        budgetAlertPanel.removeAll();
        budgetAlertPanel.revalidate();
        budgetAlertPanel.repaint();
    }
}