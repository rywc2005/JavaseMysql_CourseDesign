package javasemysql.coursedesign.gui;

import javasemysql.coursedesign.dto.ExpenseQueryParam;
import javasemysql.coursedesign.gui.dialog.ExpenseDialog;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Expense;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.service.impl.ExpenseServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * 支出管理面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ExpensePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ExpensePanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;
    private ExpenseService expenseService;
    private AccountService accountService;

    // UI组件
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> timeRangeComboBox;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JLabel totalExpenseLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JPanel chartPanel;

    // 日期范围
    private Date startDate;
    private Date endDate;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public ExpensePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.expenseService = new ExpenseServiceImpl();
        this.accountService = new AccountServiceImpl();

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
        JLabel titleLabel = new JLabel("支出管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建总支出标签
        totalExpenseLabel = new JLabel("总支出: ¥0.00");
        totalExpenseLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        totalExpenseLabel.setForeground(new Color(217, 83, 79));  // 红色

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(totalExpenseLabel, BorderLayout.EAST);

        // 创建工具栏面板
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setOpaque(false);
        toolbarPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // 创建搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        // 创建搜索框
        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // 创建搜索图标
        JLabel searchIcon = new JLabel(new ImageIcon(getClass().getResource("/resources/images/search_icon.png")));
        searchIcon.setBorder(new EmptyBorder(0, 5, 0, 5));

        // 创建类别下拉框
        categoryComboBox = new JComboBox<>(new String[]{"所有类别", "餐饮", "交通", "购物", "住房", "娱乐", "教育", "医疗", "旅行", "其他"});
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 创建时间范围下拉框
        timeRangeComboBox = new JComboBox<>(new String[]{"所有时间", "今天", "本周", "本月", "上个月", "本季度", "本年度", "自定义"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 组装搜索面板
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchFieldPanel);
        searchPanel.add(categoryComboBox);
        searchPanel.add(timeRangeComboBox);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // 创建按钮
        addButton = new JButton("添加支出");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/add_icon.png")));
        addButton.setFocusPainted(false);

        editButton = new JButton("编辑");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/edit_icon.png")));
        editButton.setFocusPainted(false);
        editButton.setEnabled(false);

        deleteButton = new JButton("删除");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/delete_icon.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);

        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/refresh_icon.png")));
        refreshButton.setFocusPainted(false);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 组装工具栏面板
        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(buttonPanel, BorderLayout.EAST);

        // 创建主内容面板（使用拆分面板）
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);  // 设置左侧占比
        splitPane.setDividerLocation(0.7);  // 设置分割线位置
        splitPane.setDividerSize(5);  // 设置分割线宽度
        splitPane.setBorder(null);  // 移除边框

        // 创建表格
        createExpenseTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "支出列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 创建图表面板
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "支出统计",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        chartPanel.setBackground(Color.WHITE);

        // 添加组件到拆分面板
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(chartPanel);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(toolbarPanel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);
    }

    /**
     * 创建支出表格
     */
    private void createExpenseTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "账户ID", "账户名称", "类别", "金额", "日期", "说明"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Double.class; // 设置金额列为Double类型，以便正确排序
                } else if (columnIndex == 5) {
                    return Date.class; // 设置日期列为Date类型，以便正确排序
                }
                return String.class;
            }
        };

        // 创建表格
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        expenseTable.setRowHeight(30);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setAutoCreateRowSorter(true);

        // 设置表格样式
        expenseTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        expenseTable.getTableHeader().setReorderingAllowed(false);
        expenseTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(6).setPreferredWidth(300);

        // 隐藏ID列和账户ID列
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);

        expenseTable.getColumnModel().getColumn(1).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(1).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(1).setWidth(0);

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

        // 应用渲染器
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(amountRenderer);
        expenseTable.getColumnModel().getColumn(5).setCellRenderer(dateRenderer);

        // 创建表格排序器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        expenseTable.setRowSorter(sorter);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 添加支出按钮点击事件
        addButton.addActionListener(e -> addExpense());

        // 编辑支出按钮点击事件
        editButton.addActionListener(e -> editSelectedExpense());

        // 删除支出按钮点击事件
        deleteButton.addActionListener(e -> deleteSelectedExpense());

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());

        // 搜索框回车事件
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterExpenses();
                }
            }
        });

        // 类别下拉框选择事件
        categoryComboBox.addActionListener(e -> filterExpenses());

        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> {
            if ("自定义".equals(timeRangeComboBox.getSelectedItem())) {
                showDateRangeDialog();
            } else {
                filterExpenses();
            }
        });

        // 表格选择事件
        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = expenseTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        // 表格双击事件
        expenseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && expenseTable.getSelectedRow() != -1) {
                    editSelectedExpense();
                }
            }
        });
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

        // 默认设置为本月
        Date[] monthRange = DateUtils.getCurrentMonthRange();
        startField.setText(new SimpleDateFormat("yyyy-MM-dd").format(monthRange[0]));
        endField.setText(new SimpleDateFormat("yyyy-MM-dd").format(monthRange[1]));

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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                startDate = sdf.parse(startField.getText());
                endDate = sdf.parse(endField.getText());

                if (startDate.after(endDate)) {
                    JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期", "错误", JOptionPane.ERROR_MESSAGE);
                    timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
                    return;
                }

                // 使用自定义日期范围过滤
                filterExpenses();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
            }
        } else {
            timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
        }
    }

    /**
     * 添加支出
     */
    private void addExpense() {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        // 获取用户的账户列表
        List<Account> accounts = accountService.getAccountsByUserId(currentUser.getId());
        if (accounts.isEmpty()) {
            mainFrame.showErrorMessage("请先添加账户");
            return;
        }

        // 创建支出对话框
        ExpenseDialog dialog = new ExpenseDialog(mainFrame, null, currentUser.getId(), accounts);
        dialog.setVisible(true);

        // 如果支出添加成功，刷新数据
        if (dialog.isExpenseSaved()) {
            refreshData();
        }
    }

    /**
     * 编辑选中的支出
     */
    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = expenseTable.convertRowIndexToModel(selectedRow);

        // 获取支出ID和账户ID
        int expenseId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        int accountId = Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString());

        // 获取支出信息
        Expense expense = expenseService.getExpenseById(expenseId);
        if (expense == null) {
            mainFrame.showErrorMessage("获取支出信息失败");
            return;
        }

        // 获取用户的账户列表
        List<Account> accounts = accountService.getAccountsByUserId(currentUser.getId());
        if (accounts.isEmpty()) {
            mainFrame.showErrorMessage("未找到有效账户");
            return;
        }

        // 创建支出对话框
        ExpenseDialog dialog = new ExpenseDialog(mainFrame, expense, currentUser.getId(), accounts);
        dialog.setVisible(true);

        // 如果支出编辑成功，刷新数据
        if (dialog.isExpenseSaved()) {
            refreshData();
        }
    }

    /**
     * 删除选中的支出
     */
    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = expenseTable.convertRowIndexToModel(selectedRow);

        // 获取支出ID和分类
        int expenseId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String category = tableModel.getValueAt(modelRow, 3).toString();
        double amount = (Double) tableModel.getValueAt(modelRow, 4);

        // 确认删除
        boolean confirmed = mainFrame.showConfirmDialog(
                "确定要删除分类为 \"" + category + "\" 金额为 \"" + StringUtils.formatCurrency(amount) + "\" 的支出记录吗？"
        );
        if (!confirmed) {
            return;
        }

        // 执行删除操作
        boolean success = expenseService.deleteExpense(expenseId);
        if (success) {
            mainFrame.showInfoMessage("支出记录删除成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("支出记录删除失败");
        }
    }

    /**
     * 过滤支出
     */
    private void filterExpenses() {
        if (currentUser == null) {
            return;
        }

        // 获取搜索关键字
        String keyword = searchField.getText().trim();

        // 获取选中的类别和时间范围
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String selectedTimeRange = (String) timeRangeComboBox.getSelectedItem();

        // 创建查询参数
        ExpenseQueryParam param = new ExpenseQueryParam(currentUser.getId());

        // 设置说明关键字
        if (!keyword.isEmpty()) {
            param.setDescription(keyword);
        }

        // 设置类别过滤
        if (!"所有类别".equals(selectedCategory)) {
            param.setCategory(selectedCategory);
        }

        // 设置时间范围
        if ("自定义".equals(selectedTimeRange)) {
            if (startDate != null && endDate != null) {
                param.setStartDate(startDate);
                param.setEndDate(endDate);
            }
        } else {
            Date[] dateRange = getDateRangeBySelection(selectedTimeRange);
            if (dateRange != null) {
                param.setStartDate(dateRange[0]);
                param.setEndDate(dateRange[1]);
            }
        }

        // 异步加载数据
        SwingWorker<List<Expense>, Void> worker = new SwingWorker<List<Expense>, Void>() {
            @Override
            protected List<Expense> doInBackground() throws Exception {
                return expenseService.queryExpenses(param);
            }

            @Override
            protected void done() {
                try {
                    List<Expense> expenses = get();
                    updateExpenseTable(expenses);
                    updateExpenseChart(expenses);
                } catch (Exception e) {
                    LogUtils.error("过滤支出失败", e);
                    mainFrame.showErrorMessage("加载支出数据失败");
                }
            }
        };

        worker.execute();
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
            default: // "所有时间"
                dateRange = null;
        }

        return dateRange;
    }

    /**
     * 更新支出表格
     *
     * @param expenses 支出列表
     */
    private void updateExpenseTable(List<Expense> expenses) {
        // 清空表格
        tableModel.setRowCount(0);

        if (expenses == null || expenses.isEmpty()) {
            // 更新总支出标签
            totalExpenseLabel.setText("总支出: " + StringUtils.formatCurrency(0));
            return;
        }

        // 计算总支出
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

        // 更新总支出标签
        totalExpenseLabel.setText("总支出: " + StringUtils.formatCurrency(totalExpense));

        // 添加数据到表格
        for (Expense expense : expenses) {
            Vector<Object> row = new Vector<>();
            row.add(expense.getId());
            row.add(expense.getAccountId());
            row.add(expense.getCategory());
            row.add(expense.getAmount());
            row.add(expense.getDate());
            row.add(expense.getDescription());
            tableModel.addRow(row);
        }
    }

    /**
     * 更新支出图表
     *
     * @param expenses 支出列表
     */
    private void updateExpenseChart(List<Expense> expenses) {
        // 清空图表面板
        chartPanel.removeAll();

        if (expenses == null || expenses.isEmpty()) {
            // 显示无数据提示
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(120, 120, 120));
            chartPanel.add(emptyLabel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        // 统计各类别支出
        Map<String, Double> categoryExpenses = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);
        }

        // 创建饼图数据集
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "支出类别分布",   // 图表标题
                dataset,         // 数据集
                true,            // 是否显示图例
                true,            // 是否生成工具提示
                false            // 是否生成URL链接
        );

        // 设置饼图样式
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        // 设置不同类别的颜色
        plot.setSectionPaint("餐饮", new Color(217, 83, 79));
        plot.setSectionPaint("交通", new Color(240, 173, 78));
        plot.setSectionPaint("购物", new Color(91, 192, 222));
        plot.setSectionPaint("住房", new Color(66, 139, 202));
        plot.setSectionPaint("娱乐", new Color(153, 102, 255));
        plot.setSectionPaint("教育", new Color(92, 184, 92));
        plot.setSectionPaint("医疗", new Color(220, 20, 60));
        plot.setSectionPaint("旅行", new Color(255, 165, 0));
        plot.setSectionPaint("其他", new Color(128, 128, 128));

        // 创建饼图面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        // 创建统计信息面板
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setOpaque(false);

        // 创建类别统计表格
        String[] columnNames = {"类别", "金额", "占比"};
        Object[][] data = new Object[categoryExpenses.size()][3];

        double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();

        int i = 0;
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = StringUtils.formatCurrency(entry.getValue());
            data[i][2] = String.format("%.2f%%", entry.getValue() / totalAmount * 100);
            i++;
        }

        JTable statsTable = new JTable(data, columnNames);
        statsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statsTable.setRowHeight(25);
        statsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        statsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        // 添加组件到统计面板
        statsPanel.add(scrollPane, BorderLayout.CENTER);

        // 创建容器面板
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(chartPanel, BorderLayout.CENTER);
        container.add(statsPanel, BorderLayout.SOUTH);

        // 添加到图表面板
        this.chartPanel.add(container, BorderLayout.CENTER);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    /**
     * 更新用户数据
     *
     * @param user 用户对象
     */
    public void updateUserData(User user) {
        this.currentUser = user;

        // 如果没有用户登录，清空表格和图表
        if (user == null) {
            tableModel.setRowCount(0);
            totalExpenseLabel.setText("总支出: " + StringUtils.formatCurrency(0));
            chartPanel.removeAll();
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        // 加载支出数据
        refreshData();
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (currentUser == null) {
            return;
        }

        // 重置过滤条件
        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        timeRangeComboBox.setSelectedIndex(0);
        startDate = null;
        endDate = null;

        // 异步加载数据
        SwingWorker<List<Expense>, Void> worker = new SwingWorker<List<Expense>, Void>() {
            @Override
            protected List<Expense> doInBackground() throws Exception {
                return expenseService.getExpensesByUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Expense> expenses = get();
                    updateExpenseTable(expenses);
                    updateExpenseChart(expenses);
                } catch (Exception e) {
                    LogUtils.error("加载支出数据失败", e);
                    mainFrame.showErrorMessage("加载支出数据失败");
                }
            }
        };

        worker.execute();
    }
}