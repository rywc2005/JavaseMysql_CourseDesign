package javasemysql.coursedesign.gui;

import javasemysql.coursedesign.dto.BillQueryParam;
import javasemysql.coursedesign.gui.dialog.BillDialog;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Bill;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.BillService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.service.impl.BillServiceImpl;
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
import java.util.Calendar;
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
 * 账单管理面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BillPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(BillPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;
    private BillService billService;
    private AccountService accountService;

    // UI组件
    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> timeRangeComboBox;
    private JComboBox<String> statusComboBox;
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JLabel totalBillLabel;
    private JLabel paidBillLabel;
    private JLabel unpaidBillLabel;
    private JLabel overdueBillLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton payButton;
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
    public BillPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.billService = new BillServiceImpl();
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
        JLabel titleLabel = new JLabel("账单管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建汇总面板
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);

        // 创建总账单标签
        totalBillLabel = new JLabel("总账单: ¥0.00");
        totalBillLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalBillLabel.setForeground(new Color(66, 139, 202)); // 蓝色

        // 创建已付款标签
        paidBillLabel = new JLabel("已付款: ¥0.00");
        paidBillLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        paidBillLabel.setForeground(new Color(92, 184, 92)); // 绿色

        // 创建未付款标签
        unpaidBillLabel = new JLabel("未付款: ¥0.00");
        unpaidBillLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        unpaidBillLabel.setForeground(new Color(240, 173, 78)); // 黄色

        // 创建逾期账单标签
        overdueBillLabel = new JLabel("已逾期: ¥0.00");
        overdueBillLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        overdueBillLabel.setForeground(new Color(217, 83, 79)); // 红色

        summaryPanel.add(totalBillLabel);
        summaryPanel.add(paidBillLabel);
        summaryPanel.add(unpaidBillLabel);
        summaryPanel.add(overdueBillLabel);

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

        // 创建类别下拉框
        categoryComboBox = new JComboBox<>(new String[]{"所有类别", "水电费", "房租", "贷款", "信用卡", "保险", "订阅服务", "税费", "会员费", "其他"});
        categoryComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 创建状态下拉框
        statusComboBox = new JComboBox<>(new String[]{"所有状态", "已付款", "未付款", "已逾期"});
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 创建时间范围下拉框
        timeRangeComboBox = new JComboBox<>(new String[]{"所有时间", "今天", "本周", "本月", "上个月", "未来7天", "未来30天", "自定义"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 组装搜索面板
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchFieldPanel);
        searchPanel.add(categoryComboBox);
        searchPanel.add(statusComboBox);
        searchPanel.add(timeRangeComboBox);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // 创建按钮
        addButton = new JButton("添加账单");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/add_icon.png")));
        addButton.setFocusPainted(false);

        editButton = new JButton("编辑");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/edit_icon.png")));
        editButton.setFocusPainted(false);
        editButton.setEnabled(false);

        payButton = new JButton("付款");
        payButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        payButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/pay_icon.png")));
        payButton.setFocusPainted(false);
        payButton.setEnabled(false);

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
        buttonPanel.add(payButton);
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
        createBillTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "账单列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 创建图表面板
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "账单统计",
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
     * 创建账单表格
     */
    private void createBillTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "账户ID", "账户名称", "类别", "金额", "到期日期", "状态", "支付日期", "说明"};

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
                } else if (columnIndex == 5 || columnIndex == 7) {
                    return Date.class; // 设置日期列为Date类型，以便正确排序
                } else if (columnIndex == 6) {
                    return String.class; // 状态列为String类型
                }
                return String.class;
            }
        };

        // 创建表格
        billTable = new JTable(tableModel);
        billTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        billTable.setRowHeight(30);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setAutoCreateRowSorter(true);

        // 设置表格样式
        billTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        billTable.getTableHeader().setReorderingAllowed(false);
        billTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        billTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        billTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        billTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        billTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        billTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        billTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        billTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        billTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        billTable.getColumnModel().getColumn(8).setPreferredWidth(200);

        // 隐藏ID列和账户ID列
        billTable.getColumnModel().getColumn(0).setMinWidth(0);
        billTable.getColumnModel().getColumn(0).setMaxWidth(0);
        billTable.getColumnModel().getColumn(0).setWidth(0);

        billTable.getColumnModel().getColumn(1).setMinWidth(0);
        billTable.getColumnModel().getColumn(1).setMaxWidth(0);
        billTable.getColumnModel().getColumn(1).setWidth(0);

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
                    if ("已付款".equals(status)) {
                        label.setForeground(new Color(92, 184, 92));  // 绿色
                    } else if ("已逾期".equals(status)) {
                        label.setForeground(new Color(217, 83, 79));  // 红色
                    } else {
                        label.setForeground(new Color(240, 173, 78));  // 黄色
                    }
                }

                return label;
            }
        };

        // 应用渲染器
        billTable.getColumnModel().getColumn(4).setCellRenderer(amountRenderer);
        billTable.getColumnModel().getColumn(5).setCellRenderer(dateRenderer);
        billTable.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
        billTable.getColumnModel().getColumn(7).setCellRenderer(dateRenderer);

        // 创建表格排序器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        billTable.setRowSorter(sorter);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 添加账单按钮点击事件
        addButton.addActionListener(e -> addBill());

        // 编辑账单按钮点击事件
        editButton.addActionListener(e -> editSelectedBill());

        // 付款按钮点击事件
        payButton.addActionListener(e -> paySelectedBill());

        // 删除账单按钮点击事件
        deleteButton.addActionListener(e -> deleteSelectedBill());

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());

        // 搜索框回车事件
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterBills();
                }
            }
        });

        // 类别下拉框选择事件
        categoryComboBox.addActionListener(e -> filterBills());

        // 状态下拉框选择事件
        statusComboBox.addActionListener(e -> filterBills());

        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> {
            if ("自定义".equals(timeRangeComboBox.getSelectedItem())) {
                showDateRangeDialog();
            } else {
                filterBills();
            }
        });

        // 表格选择事件
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = billTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);

                // 只有选中未付款的账单才能付款
                if (hasSelection) {
                    int modelRow = billTable.convertRowIndexToModel(billTable.getSelectedRow());
                    String status = tableModel.getValueAt(modelRow, 6).toString();
                    payButton.setEnabled(!status.equals("已付款"));
                } else {
                    payButton.setEnabled(false);
                }
            }
        });

        // 表格双击事件
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && billTable.getSelectedRow() != -1) {
                    editSelectedBill();
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
                filterBills();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
            }
        } else {
            timeRangeComboBox.setSelectedIndex(0); // 重置为"所有时间"
        }
    }

    /**
     * 添加账单
     */
    private void addBill() {
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

        // 创建账单对话框
        BillDialog dialog = new BillDialog(mainFrame, null, currentUser.getId(), accounts);
        dialog.setVisible(true);

        // 如果账单添加成功，刷新数据
        if (dialog.isBillSaved()) {
            refreshData();
        }
    }

    /**
     * 编辑选中的账单
     */
    private void editSelectedBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = billTable.convertRowIndexToModel(selectedRow);

        // 获取账单ID和账户ID
        int billId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        int accountId = Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString());

        // 获取账单信息
        Bill bill = billService.getBillById(billId);
        if (bill == null) {
            mainFrame.showErrorMessage("获取账单信息失败");
            return;
        }

        // 获取用户的账户列表
        List<Account> accounts = accountService.getAccountsByUserId(currentUser.getId());
        if (accounts.isEmpty()) {
            mainFrame.showErrorMessage("未找到有效账户");
            return;
        }

        // 创建账单对话框
        BillDialog dialog = new BillDialog(mainFrame, bill, currentUser.getId(), accounts);
        dialog.setVisible(true);

        // 如果账单编辑成功，刷新数据
        if (dialog.isBillSaved()) {
            refreshData();
        }
    }

    /**
     * 支付选中的账单
     */
    private void paySelectedBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = billTable.convertRowIndexToModel(selectedRow);

        // 获取账单ID和分类
        int billId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String category = tableModel.getValueAt(modelRow, 3).toString();
        double amount = (Double) tableModel.getValueAt(modelRow, 4);

        // 确认支付
        boolean confirmed = mainFrame.showConfirmDialog(
                "确定要支付分类为 \"" + category + "\" 金额为 \"" + StringUtils.formatCurrency(amount) + "\" 的账单吗？"
        );
        if (!confirmed) {
            return;
        }

        // 执行支付操作
        boolean success = billService.payBill(billId, new Date());
        if (success) {
            mainFrame.showInfoMessage("账单支付成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("账单支付失败");
        }
    }

    /**
     * 删除选中的账单
     */
    private void deleteSelectedBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = billTable.convertRowIndexToModel(selectedRow);

        // 获取账单ID和分类
        int billId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String category = tableModel.getValueAt(modelRow, 3).toString();
        double amount = (Double) tableModel.getValueAt(modelRow, 4);

        // 确认删除
        boolean confirmed = mainFrame.showConfirmDialog(
                "确定要删除分类为 \"" + category + "\" 金额为 \"" + StringUtils.formatCurrency(amount) + "\" 的账单吗？"
        );
        if (!confirmed) {
            return;
        }

        // 执行删除操作
        boolean success = billService.deleteBill(billId);
        if (success) {
            mainFrame.showInfoMessage("账单删除成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("账单删除失败");
        }
    }

    /**
     * 过滤账单
     */
    private void filterBills() {
        if (currentUser == null) {
            return;
        }

        // 获取搜索关键字
        String keyword = searchField.getText().trim();

        // 获取选中的类别、状态和时间范围
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        String selectedTimeRange = (String) timeRangeComboBox.getSelectedItem();

        // 创建查询参数
        BillQueryParam param = new BillQueryParam(currentUser.getId());

        // 设置说明关键字
        if (!keyword.isEmpty()) {
            param.setCategory(keyword);
        }

        // 设置类别过滤
        if (!"所有类别".equals(selectedCategory)) {
            param.setCategory(selectedCategory);
        }

        // 设置状态过滤
        if ("已付款".equals(selectedStatus)) {
            param.setPaid(true);
        } else if ("未付款".equals(selectedStatus)) {
            param.setPaid(false);
        } else if ("已逾期".equals(selectedStatus)) {
            param.setPaid(false);
            // 设置到期日为今天之前
            param.setEndDate(new Date());
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
        SwingWorker<List<Bill>, Void> worker = new SwingWorker<List<Bill>, Void>() {
            @Override
            protected List<Bill> doInBackground() throws Exception {
                return billService.queryBills(param);
            }

            @Override
            protected void done() {
                try {
                    List<Bill> bills = get();
                    updateBillTable(bills);
                    updateBillChart(bills);
                } catch (Exception e) {
                    LogUtils.error("过滤账单失败", e);
                    mainFrame.showErrorMessage("加载账单数据失败");
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
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();

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
            case "未来7天":
                calendar.setTime(today);
                dateRange = new Date[2];
                dateRange[0] = today;
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                dateRange[1] = calendar.getTime();
                break;
            case "未来30天":
                calendar.setTime(today);
                dateRange = new Date[2];
                dateRange[0] = today;
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                dateRange[1] = calendar.getTime();
                break;
            default: // "所有时间"
                dateRange = null;
        }

        return dateRange;
    }

    /**
     * 更新账单表格
     *
     * @param bills 账单列表
     */
    private void updateBillTable(List<Bill> bills) {
        // 清空表格
        tableModel.setRowCount(0);

        if (bills == null || bills.isEmpty()) {
            // 更新汇总标签
            updateSummaryLabels(0, 0, 0, 0);
            return;
        }

        double totalAmount = 0;
        double paidAmount = 0;
        double unpaidAmount = 0;
        double overdueAmount = 0;

        // 获取当前日期
        Date today = new Date();

        // 添加数据到表格并计算汇总数据
        for (Bill bill : bills) {
            Vector<Object> row = new Vector<>();
            row.add(bill.getId());
            row.add(bill.getAccountId());
            row.add(bill.getAccountName());
            row.add(bill.getCategory());
            row.add(bill.getAmount());
            row.add(bill.getDueDate());

            // 确定账单状态
            String status;
            if (bill.isPaid()) {
                status = "已付款";
                paidAmount += bill.getAmount();
            } else if (bill.getDueDate().before(today)) {
                status = "已逾期";
                overdueAmount += bill.getAmount();
                unpaidAmount += bill.getAmount();
            } else {
                status = "未付款";
                unpaidAmount += bill.getAmount();
            }

            row.add(status);
            row.add(bill.getPaymentDate());
            row.add(bill.getDescription());

            tableModel.addRow(row);

            totalAmount += bill.getAmount();
        }

        // 更新汇总标签
        updateSummaryLabels(totalAmount, paidAmount, unpaidAmount, overdueAmount);
    }

    /**
     * 更新账单图表
     *
     * @param bills 账单列表
     */
    private void updateBillChart(List<Bill> bills) {
        // 清空图表面板
        chartPanel.removeAll();

        if (bills == null || bills.isEmpty()) {
            // 显示无数据提示
            JLabel emptyLabel = new JLabel("没有数据可供显示", JLabel.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(120, 120, 120));
            chartPanel.add(emptyLabel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        // 创建一个包含两个图表的面板
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 10));
        container.setOpaque(false);

        // 创建类别饼图
        JPanel categoryChartPanel = createCategoryPieChart(bills);

        // 创建状态饼图
        JPanel statusChartPanel = createStatusPieChart(bills);

        // 添加到容器
        container.add(categoryChartPanel);
        container.add(statusChartPanel);

        // 添加到图表面板
        chartPanel.add(container, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    /**
     * 创建类别饼图
     *
     * @param bills 账单列表
     * @return 饼图面板
     */
    private JPanel createCategoryPieChart(List<Bill> bills) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 统计各类别账单
        Map<String, Double> categoryAmounts = new HashMap<>();
        for (Bill bill : bills) {
            String category = bill.getCategory();
            double amount = bill.getAmount();

            categoryAmounts.put(category, categoryAmounts.getOrDefault(category, 0.0) + amount);
        }

        // 创建饼图数据集
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : categoryAmounts.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "账单类别分布",   // 图表标题
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
        plot.setSectionPaint("水电费", new Color(91, 192, 222));
        plot.setSectionPaint("房租", new Color(66, 139, 202));
        plot.setSectionPaint("贷款", new Color(217, 83, 79));
        plot.setSectionPaint("信用卡", new Color(240, 173, 78));
        plot.setSectionPaint("保险", new Color(92, 184, 92));
        plot.setSectionPaint("订阅服务", new Color(153, 102, 255));
        plot.setSectionPaint("税费", new Color(220, 20, 60));
        plot.setSectionPaint("会员费", new Color(255, 165, 0));
        plot.setSectionPaint("其他", new Color(128, 128, 128));

        // 创建饼图面板
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建状态饼图
     *
     * @param bills 账单列表
     * @return 饼图面板
     */
    private JPanel createStatusPieChart(List<Bill> bills) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 统计各状态账单
        double paidAmount = 0;
        double unpaidAmount = 0;
        double overdueAmount = 0;

        // 获取当前日期
        Date today = new Date();

        for (Bill bill : bills) {
            if (bill.isPaid()) {
                paidAmount += bill.getAmount();
            } else if (bill.getDueDate().before(today)) {
                overdueAmount += bill.getAmount();
            } else {
                unpaidAmount += bill.getAmount();
            }
        }

        // 创建饼图数据集
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据（只添加非零项）
        if (paidAmount > 0) {
            dataset.setValue("已付款", paidAmount);
        }
        if (unpaidAmount > 0) {
            dataset.setValue("未付款", unpaidAmount);
        }
        if (overdueAmount > 0) {
            dataset.setValue("已逾期", overdueAmount);
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "账单状态分布",   // 图表标题
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

        // 设置不同状态的颜色
        plot.setSectionPaint("已付款", new Color(92, 184, 92));    // 绿色
        plot.setSectionPaint("未付款", new Color(240, 173, 78));   // 黄色
        plot.setSectionPaint("已逾期", new Color(217, 83, 79));    // 红色

        // 创建饼图面板
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 更新汇总标签
     *
     * @param totalAmount 总金额
     * @param paidAmount 已付款金额
     * @param unpaidAmount 未付款金额
     * @param overdueAmount 逾期金额
     */
    private void updateSummaryLabels(double totalAmount, double paidAmount, double unpaidAmount, double overdueAmount) {
        totalBillLabel.setText("总账单: " + StringUtils.formatCurrency(totalAmount));
        paidBillLabel.setText("已付款: " + StringUtils.formatCurrency(paidAmount));
        unpaidBillLabel.setText("未付款: " + StringUtils.formatCurrency(unpaidAmount));
        overdueBillLabel.setText("已逾期: " + StringUtils.formatCurrency(overdueAmount));
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
            updateSummaryLabels(0, 0, 0, 0);
            chartPanel.removeAll();
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        // 加载账单数据
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
        statusComboBox.setSelectedIndex(0);
        timeRangeComboBox.setSelectedIndex(0);
        startDate = null;
        endDate = null;

        // 异步加载数据
        SwingWorker<List<Bill>, Void> worker = new SwingWorker<List<Bill>, Void>() {
            @Override
            protected List<Bill> doInBackground() throws Exception {
                return billService.getBillsByUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Bill> bills = get();
                    updateBillTable(bills);
                    updateBillChart(bills);
                } catch (Exception e) {
                    LogUtils.error("加载账单数据失败", e);
                    mainFrame.showErrorMessage("加载账单数据失败");
                }
            }
        };

        worker.execute();
    }

    public void showBillDetails(int billId) {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        // 获取账单信息
        Bill bill = billService.getBillById(billId);
        if (bill == null) {
            mainFrame.showErrorMessage("获取账单信息失败");
            return;
        }

        // 创建账单详情对话框
        BillDetailsDialog dialog = new BillDetailsDialog(mainFrame, bill);
        dialog.setVisible(true);
    }
}