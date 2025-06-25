package com.PFM.CD.gui;

import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.interfaces.TransactionService;
import com.PFM.CD.utils.format.DateFormatter;
import com.PFM.CD.utils.format.NumberFormatter;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 交易管理面板（支持查询、展示、统计交易记录）
 */

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易管理面板（支持查询、展示、统计交易记录）
 */
public class TransactionsPanel extends JPanel {
    private final TransactionService transactionService;
    private JTable transactionTable;
    private TransactionTableModel tableModel;
    private JDateChooser startDateChooser;  // 依赖 com.toedter:jdatechooser
    private JDateChooser endDateChooser;
    private JComboBox<TransactionType> typeComboBox;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private int currentPage = 1;  // 当前页码
    private int pageSize = 10;    // 每页显示数量
    private int totalPages = 1;   // 总页数

    // 新增分页控件
    private JTextField pageNumField;
    private JButton prevPageBtn;
    private JButton nextPageBtn;

    public TransactionsPanel(TransactionService transactionService) {
        this.transactionService = transactionService;
        setLayout(new BorderLayout(10, 10));// 设置布局管理器
        initComponents();
        loadTransactions();  // 初始加载所有交易
    }

    /**
     * 初始化界面组件
     */
    private void initComponents() {
        // 顶部过滤面板（日期、类型、查询按钮）
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        startDateChooser = new JDateChooser();
        startDateChooser.setDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateChooser = new JDateChooser();
        endDateChooser.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        typeComboBox = new JComboBox<>(TransactionType.values());
        typeComboBox.addItem(null);  // "全部类型"选项
        typeComboBox.setSelectedItem(null);

        JButton queryBtn = new JButton("查询");
        queryBtn.addActionListener(e -> loadTransactions());

        filterPanel.add(new JLabel("开始日期："));
        filterPanel.add(startDateChooser);
        filterPanel.add(new JLabel("结束日期："));
        filterPanel.add(endDateChooser);
        filterPanel.add(new JLabel("交易类型："));
        filterPanel.add(typeComboBox);
        filterPanel.add(queryBtn);

        // 交易表格（带滚动条）
        tableModel = new TransactionTableModel();
        transactionTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(transactionTable);

        // 底部统计面板（总收入、总支出）
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        statsPanel.add(new JLabel("总收入："));
        statsPanel.add(incomeLabel);
        statsPanel.add(new JLabel("总支出："));
        statsPanel.add(expenseLabel);

        // 组合面板
        add(filterPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }

    /**
     * 加载交易数据并刷新界面
     */
    private void loadTransactions() {
        try {
            // 获取过滤条件
            LocalDate startDate = startDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = endDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            TransactionType type = (TransactionType) typeComboBox.getSelectedItem();

            // 查询交易列表
            List<Transaction> transactions;
            if (type == null) {
                transactions = transactionService.getTransactionsByDateRange(1, startDate, endDate);  // 假设用户ID为1（需动态获取）
            } else {
                transactions = transactionService.getTransactionsByType(1, type);
            }

            // 更新表格数据
            tableModel.setTransactions(transactions);
            tableModel.fireTableDataChanged();

            // 更新统计信息
            BigDecimal totalIncome = transactionService.calculateTotalIncome(1, startDate, endDate);
            BigDecimal totalExpense = transactionService.calculateTotalExpense(1, startDate, endDate);
            incomeLabel.setText(NumberFormatter.format(totalIncome));
            expenseLabel.setText(NumberFormatter.format(totalExpense));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载交易失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 自定义交易表格模型（管理交易数据展示）
     */
    private static class TransactionTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID", "日期", "类型", "金额", "源账户", "目标账户", "分类", "描述"};
        private List<Transaction> transactions = new ArrayList<>();

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public int getRowCount() {
            return transactions.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            Transaction t = transactions.get(row);
            switch (column) {
                case 0: return t.getTransactionId();
                case 1: return DateFormatter.format(t.getTransactionDate());  // 使用工程日期格式化工具
                case 2: return t.getTransactionType().name();
                case 3: return NumberFormatter.format(t.getAmount());  // 使用工程数字格式化工具
                case 4: return t.getSourceAccountId() == null ? "-" : t.getSourceAccountId();
                case 5: return t.getDestinationAccountId() == null ? "-" : t.getDestinationAccountId();
                case 6: return t.getCategoryId();
                case 7: return t.getDescription();
                default: return null;
            }
        }
    }
}
