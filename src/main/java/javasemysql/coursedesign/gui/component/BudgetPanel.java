package javasemysql.coursedesign.gui.component;

import javasemysql.coursedesign.dto.BudgetQueryParam;
import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.gui.component.dialog.BudgetDialog;
import javasemysql.coursedesign.model.Budget;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.BudgetService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.service.impl.BudgetServiceImpl;
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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * 预算管理面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BudgetPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(BudgetPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;
    private BudgetService budgetService;
    private ExpenseService expenseService;

    // UI组件
    private JTextField searchField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> timeRangeComboBox;
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private JLabel totalBudgetLabel;
    private JLabel totalUsedLabel;
    private JLabel totalRemainingLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public BudgetPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.budgetService = new BudgetServiceImpl();
        this.expenseService = new ExpenseServiceImpl();

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
        JLabel titleLabel = new JLabel("预算管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建汇总面板
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        summaryPanel.setOpaque(false);

        // 创建总预算标签
        totalBudgetLabel = new JLabel("总预算: ¥0.00");
        totalBudgetLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalBudgetLabel.setForeground(new Color(66, 139, 202));  // 蓝色

        // 创建已用总额标签
        totalUsedLabel = new JLabel("已用总额: ¥0.00");
        totalUsedLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalUsedLabel.setForeground(new Color(217, 83, 79));  // 红色

        // 创建剩余总额标签
        totalRemainingLabel = new JLabel("剩余总额: ¥0.00");
        totalRemainingLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalRemainingLabel.setForeground(new Color(92, 184, 92));  // 绿色

        summaryPanel.add(totalBudgetLabel);
        summaryPanel.add(totalUsedLabel);
        summaryPanel.add(totalRemainingLabel);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(summaryPanel, BorderLayout.EAST);

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

        // 创建状态下拉框
        statusComboBox = new JComboBox<>(new String[]{"所有状态", "未超支", "已超支"});
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 创建时间范围下拉框
        timeRangeComboBox = new JComboBox<>(new String[]{"所有时间", "本月", "上个月", "本季度", "本年度", "自定义"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 组装搜索面板
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchFieldPanel);
        searchPanel.add(statusComboBox);
        searchPanel.add(timeRangeComboBox);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // 创建按钮
        addButton = new JButton("添加预算");
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

        // 创建表格
        createBudgetTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "预算列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(toolbarPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
    }

    /**
     * 创建预算表格
     */
    private void createBudgetTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "分类", "预算金额", "已用金额", "剩余金额", "使用比例", "开始日期", "结束日期", "状态"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3 || columnIndex == 4) {
                    return Double.class; // 设置金额列为Double类型，以便正确排序
                } else if (columnIndex == 5) {
                    return Double.class; // 设置比例列为Double类型
                } else if (columnIndex == 6 || columnIndex == 7) {
                    return Date.class; // 设置日期列为Date类型，以便正确排序
                }
                return String.class;
            }
        };

        // 创建表格
        budgetTable = new JTable(tableModel);
        budgetTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        budgetTable.setRowHeight(30);
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        budgetTable.setAutoCreateRowSorter(true);

        // 设置表格样式
        budgetTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        budgetTable.getTableHeader().setReorderingAllowed(false);
        budgetTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        budgetTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        budgetTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        budgetTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        budgetTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        budgetTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        budgetTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        budgetTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        budgetTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        budgetTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        // 隐藏ID列
        budgetTable.getColumnModel().getColumn(0).setMinWidth(0);
        budgetTable.getColumnModel().getColumn(0).setMaxWidth(0);
        budgetTable.getColumnModel().getColumn(0).setWidth(0);

        // 设置金额列的单元格渲染器，以便格式化显示
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) comp;

                if (value != null) {
                    double amount = (Double) value;
                    label.setText(StringUtils.formatCurrency(amount));

                    // 如果是剩余金额列，根据金额设置颜色
                    if (column == 4) {
                        if (amount < 0) {
                            label.setForeground(new Color(217, 83, 79)); // 红色
                        } else {
                            label.setForeground(new Color(92, 184, 92)); // 绿色
                        }
                    }
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

        // 设置使用比例的单元格渲染器，以显示进度条
        DefaultTableCellRenderer progressRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value == null) {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }

                JProgressBar progressBar = new JProgressBar(0, 100);
                double percentage = (Double) value;
                int intPercentage = (int) percentage;

                progressBar.setValue(intPercentage > 100 ? 100 : intPercentage);
                progressBar.setStringPainted(true);
                progressBar.setString(String.format("%.1f%%", percentage));

                // 根据使用比例设置进度条颜色
                if (percentage >= 100) {
                    progressBar.setForeground(new Color(217, 83, 79));  // 红色
                } else if (percentage >= 80) {
                    progressBar.setForeground(new Color(240, 173, 78));  // 黄色
                } else {
                    progressBar.setForeground(new Color(92, 184, 92));  // 绿色
                }

                return progressBar;
            }
        };

        // 设置状态列的单元格渲染器
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) comp;

                String status = (String) value;
                if ("超支".equals(status)) {
                    label.setForeground(new Color(217, 83, 79)); // 红色
                } else {
                    label.setForeground(new Color(92, 184, 92)); // 绿色
                }

                return label;
            }
        };

        // 应用渲染器
        budgetTable.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        budgetTable.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);
        budgetTable.getColumnModel().getColumn(4).setCellRenderer(amountRenderer);
        budgetTable.getColumnModel().getColumn(5).setCellRenderer(progressRenderer);
        budgetTable.getColumnModel().getColumn(6).setCellRenderer(dateRenderer);
        budgetTable.getColumnModel().getColumn(7).setCellRenderer(dateRenderer);
        budgetTable.getColumnModel().getColumn(8).setCellRenderer(statusRenderer);

        // 创建表格排序器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        budgetTable.setRowSorter(sorter);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 添加预算按钮点击事件
        addButton.addActionListener(e -> addBudget());

        // 编辑预算按钮点击事件
        editButton.addActionListener(e -> editSelectedBudget());

        // 删除预算按钮点击事件
        deleteButton.addActionListener(e -> deleteSelectedBudget());

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());

        // 搜索框回车事件
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterBudgets();
                }
            }
        });

        // 状态下拉框选择事件
        statusComboBox.addActionListener(e -> filterBudgets());

        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> {
            if ("自定义".equals(timeRangeComboBox.getSelectedItem())) {
                showDateRangeDialog();
            } else {
                filterBudgets();
            }
        });

        // 表格选择事件
        budgetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = budgetTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        // 表格双击事件
        budgetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && budgetTable.getSelectedRow() != -1) {
                    editSelectedBudget();
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
                Date startDate = sdf.parse(startField.getText());
                Date endDate = sdf.parse(endField.getText());

                if (startDate.after(endDate)) {
                    JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期", "错误", JOptionPane.ERROR_MESSAGE);
                    timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
                    return;
                }

                // 使用自定义日期范围过滤
                customDateRange = new Date[] { startDate, endDate };
                filterBudgets();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
            }
        } else {
            timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
        }
    }

    private Date[] customDateRange = null;

    /**
     * 添加预算
     */
    private void addBudget() {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        // 创建预算对话框
        BudgetDialog dialog = new BudgetDialog(mainFrame, null, currentUser.getId());
        dialog.setVisible(true);

        // 如果预算添加成功，刷新数据
        if (dialog.isBudgetSaved()) {
            refreshData();
        }
    }

    /**
     * 编辑选中的预算
     */
    private void editSelectedBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = budgetTable.convertRowIndexToModel(selectedRow);

        // 获取预算ID
        int budgetId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());

        // 获取预算信息
        Budget budget = budgetService.getBudgetById(budgetId);
        if (budget == null) {
            mainFrame.showErrorMessage("获取预算信息失败");
            return;
        }

        // 创建预算对话框
        BudgetDialog dialog = new BudgetDialog(mainFrame, budget, currentUser.getId());
        dialog.setVisible(true);

        // 如果预算编辑成功，刷新数据
        if (dialog.isBudgetSaved()) {
            refreshData();
        }
    }

    /**
     * 删除选中的预算
     */
    private void deleteSelectedBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = budgetTable.convertRowIndexToModel(selectedRow);

        // 获取预算ID和分类
        int budgetId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String category = tableModel.getValueAt(modelRow, 1).toString();

        // 确认删除
        boolean confirmed = mainFrame.showConfirmDialog("确定要删除分类为 \"" + category + "\" 的预算吗？");
        if (!confirmed) {
            return;
        }

        // 执行删除操作
        boolean success = budgetService.deleteBudget(budgetId);
        if (success) {
            mainFrame.showInfoMessage("预算删除成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("预算删除失败");
        }
    }

    /**
     * 过滤预算
     */
    private void filterBudgets() {
        if (currentUser == null) {
            return;
        }

        // 获取搜索关键字
        String keyword = searchField.getText().trim();

        // 获取选中的状态和时间范围
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        String selectedTimeRange = (String) timeRangeComboBox.getSelectedItem();

        // 创建查询参数
        BudgetQueryParam param = new BudgetQueryParam(currentUser.getId());
        param.setCategory(keyword.isEmpty() ? null : keyword);

        // 设置状态过滤
        if ("未超支".equals(selectedStatus)) {
            param.setIsExceeded(false);
        } else if ("已超支".equals(selectedStatus)) {
            param.setIsExceeded(true);
        }

        // 设置时间范围
        Date[] dateRange = null;
        if ("自定义".equals(selectedTimeRange)) {
            dateRange = customDateRange;
        } else {
            dateRange = getDateRangeBySelection(selectedTimeRange);
        }

        if (dateRange != null) {
            param.setStartDate(dateRange[0]);
            param.setEndDate(dateRange[1]);
        }

        // 异步加载数据
        SwingWorker<List<Budget>, Void> worker = new SwingWorker<List<Budget>, Void>() {
            @Override
            protected List<Budget> doInBackground() throws Exception {
                return budgetService.queryBudgets(param);
            }

            @Override
            protected void done() {
                try {
                    List<Budget> budgets = get();
                    updateBudgetTable(budgets);
                } catch (Exception e) {
                    LogUtils.error("过滤预算失败", e);
                    mainFrame.showErrorMessage("加载预算数据失败");
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
     * 更新预算表格
     *
     * @param budgets 预算列表
     */
    private void updateBudgetTable(List<Budget> budgets) {
        // 清空表格
        tableModel.setRowCount(0);

        if (budgets == null || budgets.isEmpty()) {
            // 更新总额标签
            updateTotalLabels(0, 0);
            return;
        }

        double totalBudgetAmount = 0;
        double totalUsedAmount = 0;

        // 添加数据到表格
        for (Budget budget : budgets) {
            Vector<Object> row = new Vector<>();
            row.add(budget.getId());
            row.add(budget.getCategory());
            row.add(budget.getAmount());
            row.add(budget.getUsedAmount());

            // 计算剩余金额
            double remaining = budget.getAmount() - budget.getUsedAmount();
            row.add(remaining);

            // 计算使用比例
            double usagePercentage = budget.getAmount() > 0 ? (budget.getUsedAmount() / budget.getAmount() * 100) : 0;
            row.add(usagePercentage);

            row.add(budget.getStartDate());
            row.add(budget.getEndDate());

            // 设置状态
            row.add(usagePercentage >= 100 ? "超支" : "正常");

            tableModel.addRow(row);

            // 累加总额
            totalBudgetAmount += budget.getAmount();
            totalUsedAmount += budget.getUsedAmount();
        }

        // 更新总额标签
        updateTotalLabels(totalBudgetAmount, totalUsedAmount);
    }

    /**
     * 更新总额标签
     *
     * @param totalBudget 总预算金额
     * @param totalUsed 总已用金额
     */
    private void updateTotalLabels(double totalBudget, double totalUsed) {
        double totalRemaining = totalBudget - totalUsed;

        totalBudgetLabel.setText("总预算: " + StringUtils.formatCurrency(totalBudget));
        totalUsedLabel.setText("已用总额: " + StringUtils.formatCurrency(totalUsed));
        totalRemainingLabel.setText("剩余总额: " + StringUtils.formatCurrency(totalRemaining));

        // 根据剩余金额设置颜色
        if (totalRemaining < 0) {
            totalRemainingLabel.setForeground(new Color(217, 83, 79)); // 红色
        } else {
            totalRemainingLabel.setForeground(new Color(92, 184, 92)); // 绿色
        }
    }

    /**
     * 更新用户数据
     *
     * @param user 用户对象
     */
    public void updateUserData(User user) {
        this.currentUser = user;

        // 如果没有用户登录，清空表格
        if (user == null) {
            tableModel.setRowCount(0);
            updateTotalLabels(0, 0);
            return;
        }

        // 加载预算数据
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
        statusComboBox.setSelectedIndex(0);
        timeRangeComboBox.setSelectedIndex(0);
        customDateRange = null;

        // 异步加载数据
        SwingWorker<List<Budget>, Void> worker = new SwingWorker<List<Budget>, Void>() {
            @Override
            protected List<Budget> doInBackground() throws Exception {
                return budgetService.getBudgetsByUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Budget> budgets = get();
                    updateBudgetTable(budgets);
                } catch (Exception e) {
                    LogUtils.error("加载预算数据失败", e);
                    mainFrame.showErrorMessage("加载预算数据失败");
                }
            }
        };

        worker.execute();
    }

    public void showBudgetDetails(int id) {
        // 获取预算详情
        Budget budget = budgetService.getBudgetById(id);
        if (budget == null) {
            mainFrame.showErrorMessage("获取预算详情失败");
            return;
        }

        // 创建预算详情对话框
        BudgetDialog dialog = new BudgetDialog(mainFrame, budget, currentUser.getId());
        dialog.setVisible(true);

        // 如果预算编辑成功，刷新数据
        if (dialog.isBudgetSaved()) {
            refreshData();
        }
    }
}