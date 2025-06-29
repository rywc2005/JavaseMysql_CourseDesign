package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.gui.component.style;
import com.PFM.CD.service.interfaces.AccountService;
import com.PFM.CD.service.interfaces.CategoryService;
import com.PFM.CD.service.interfaces.TransactionService;
import com.PFM.CD.utils.format.DateFormatter;
import com.PFM.CD.utils.format.NumberFormatter;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;

/**
 * 交易管理面板（支持查询、展示、统计交易记录，新增分页、账户过滤、分类统计等功能，高端大气风格优化）
 */
public class TransactionsPanel extends JPanel {
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final int currentUserId;
    private JTable transactionTable;
    private TransactionTableModel tableModel;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JComboBox<TransactionType> typeComboBox;
    private JComboBox<String> accountComboBox;
    private JComboBox<String> accountNameComboBox;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private int currentPage = 1;
    private int pageSize = 15;
    private int totalPages = 1;

    private JTextField pageNumField;
    private JButton prevPageBtn;
    private JButton nextPageBtn;
    private JLabel totalPagesLabel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn;

    private JTable categoryStatsTable;
    private CategoryStatsTableModel categoryStatsModel;

    public TransactionsPanel(TransactionService transactionService, AccountService accountService, CategoryService categoryService, int currentUserId) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.currentUserId = currentUserId;
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(247, 249, 254));
        setBorder(new EmptyBorder(12, 18, 12, 18));
        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        // 顶部过滤面板（分两行：第一行条件，第二行按钮）
        JPanel filterOuterPanel = new JPanel();
        filterOuterPanel.setLayout(new BoxLayout(filterOuterPanel, BoxLayout.Y_AXIS));
        filterOuterPanel.setOpaque(false);

        JPanel filterRow1 = new JPanel(new GridBagLayout());
        filterRow1.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 12, 4, 0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;

        int gridx = 0;

        // 开始日期
        c.gridx = gridx++;
        filterRow1.add(new JLabel("开始日期："), c);

        startDateChooser = new JDateChooser();
        startDateChooser.setDate(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        startDateChooser.setPreferredSize(new Dimension(200, 36));
        startDateChooser.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        c.gridx = gridx++;
        filterRow1.add(startDateChooser, c);

        // 结束日期
        c.gridx = gridx++;
        filterRow1.add(new JLabel("结束日期："), c);

        endDateChooser = new JDateChooser();
        endDateChooser.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateChooser.setPreferredSize(new Dimension(200, 36));
        endDateChooser.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        c.gridx = gridx++;
        filterRow1.add(endDateChooser, c);

        // 交易类型
        c.gridx = gridx++;
        filterRow1.add(new JLabel("交易类型："), c);

        typeComboBox = new JComboBox<>(TransactionType.values());
        typeComboBox.addItem(null);
        typeComboBox.setSelectedItem(null);
        typeComboBox.setPreferredSize(new Dimension(130, 36));
        typeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        c.gridx = gridx++;
        filterRow1.add(typeComboBox, c);

        // 账户
        c.gridx = gridx++;
        filterRow1.add(new JLabel("账户："), c);

        accountComboBox = new JComboBox<>();
        accountComboBox.setPreferredSize(new Dimension(160, 36));
        accountComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        loadUserAccounts();
        c.gridx = gridx++;
        filterRow1.add(accountComboBox, c);

        // 第二行：功能按钮（新增、编辑、删除、刷新）
        JPanel filterRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        filterRow2.setOpaque(false);

        JButton queryBtn = new style().createFlatButton("查询", new Color(51, 102, 255), new Color(80, 130, 255));
        queryBtn.setPreferredSize(new Dimension(120, 40));
        queryBtn.setForeground(Color.WHITE);
        queryBtn.addActionListener(e -> {
            currentPage = 1;
            loadTransactions();
        });

        JButton recentBtn = new style().createFlatButton("最近几条", new Color(80, 170, 230), new Color(105, 200, 255));
        recentBtn.setPreferredSize(new Dimension(200, 40));
        recentBtn.setForeground(Color.WHITE);
        recentBtn.addActionListener(e -> {
            currentPage = 1;
            pageSize = 10;
            loadRecentTransactions();
        });

        addBtn = new style().createFlatButton("新增", new Color(0, 123, 255), new Color(50, 150, 255));
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addTransaction());

        editBtn = new style().createFlatButton("编辑", new Color(70, 180, 100), new Color(110, 220, 140));
        editBtn.setPreferredSize(new Dimension(120, 40));
        editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(e -> editTransaction());

        deleteBtn = new style().createFlatButton("删除", new Color(230, 70, 70), new Color(240, 100, 100));
        deleteBtn.setPreferredSize(new Dimension(120, 40));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteTransaction());

        refreshBtn = new style().createFlatButton("刷新", new Color(80, 170, 230), new Color(105, 200, 255));
        refreshBtn.setPreferredSize(new Dimension(120, 40));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshTransactions());

        filterRow2.add(queryBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(recentBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(addBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(editBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(deleteBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(refreshBtn);

        filterOuterPanel.add(filterRow1);
        filterOuterPanel.add(Box.createVerticalStrut(8));
        filterOuterPanel.add(filterRow2);

        filterRow2.add(queryBtn);
        filterRow2.add(Box.createHorizontalStrut(12));
        filterRow2.add(recentBtn);

        filterOuterPanel.add(filterRow1);
        filterOuterPanel.add(Box.createVerticalStrut(8));
        filterOuterPanel.add(filterRow2);

        // 主内容面板（选项卡）
        JTabbedPane mainTabPane = new JTabbedPane();
        mainTabPane.setFont(new Font("微软雅黑", Font.BOLD, 15));

        // 交易列表
        JPanel transactionListPanel = new JPanel(new BorderLayout());
        transactionListPanel.setOpaque(false);
        tableModel = new TransactionTableModel();
        transactionTable = new JTable(tableModel);
        new style().styleTable(transactionTable);
        transactionListPanel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        // 分类统计
        JPanel categoryStatsPanel = new JPanel(new BorderLayout());
        categoryStatsPanel.setOpaque(false);
        categoryStatsModel = new CategoryStatsTableModel();
        categoryStatsTable = new JTable(categoryStatsModel);
        new style().styleTable(categoryStatsTable);
        categoryStatsPanel.add(new JScrollPane(categoryStatsTable), BorderLayout.CENTER);

        mainTabPane.addTab("交易列表", transactionListPanel);
        mainTabPane.addTab("分类统计", categoryStatsPanel);

        // 底部统计 + 分页面板
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 6));
        bottomPanel.setOpaque(false);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 3));
        statsPanel.setOpaque(false);
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();
        JLabel incomeTitle = new JLabel("总收入：");
        incomeTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        incomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        incomeLabel.setForeground(new Color(28, 150, 97));
        JLabel expenseTitle = new JLabel("总支出：");
        expenseTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        expenseLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        expenseLabel.setForeground(new Color(225, 80, 80));
        statsPanel.add(incomeTitle);
        statsPanel.add(incomeLabel);
        statsPanel.add(expenseTitle);
        statsPanel.add(expenseLabel);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 3));
        paginationPanel.setOpaque(false);
        prevPageBtn = new style().createFlatButton("上一页", new Color(220, 220, 220), new Color(200, 200, 200));
        nextPageBtn = new style().createFlatButton("下一页", new Color(220, 220, 220), new Color(200, 200, 200));
        prevPageBtn.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadTransactions();
            }
        });
        nextPageBtn.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadTransactions();
            }
        });
        pageNumField = new JTextField(3);
        pageNumField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        pageNumField.setHorizontalAlignment(JTextField.CENTER);
        pageNumField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageNumField.getText());
                if (page >= 1 && page <= totalPages) {
                    currentPage = page;
                    loadTransactions();
                }
            } catch (Exception ex) {
                // ignore
            }
        });
        totalPagesLabel = new JLabel();
        totalPagesLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        paginationPanel.add(prevPageBtn);
        paginationPanel.add(new JLabel("第"));
        paginationPanel.add(pageNumField);
        paginationPanel.add(new JLabel("页 / 共"));
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(new JLabel("页"));
        paginationPanel.add(nextPageBtn);

        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(paginationPanel, BorderLayout.EAST);

        add(filterOuterPanel, BorderLayout.NORTH);
        add(mainTabPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    private Transaction getSelectedTransaction() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) return null;
        return tableModel.transactions.get(row);
    }
    // 新增：新增交易
    private void addTransaction() {
        TransactionDialog dialog = new TransactionDialog(null);
        dialog.setVisible(true);
        Transaction t = dialog.getTransaction();
        if (t != null) {
            try {
                if (t.getTransactionType() == TransactionType.INCOME) {
                    transactionService.recordIncome(currentUserId, t.getSourceAccountId(), t.getCategoryId(),
                            t.getAmount(), t.getTransactionDate(), t.getDescription());
                } else if (t.getTransactionType() == TransactionType.EXPENSE) {
                    transactionService.recordExpense(currentUserId, t.getSourceAccountId(), t.getCategoryId(),
                            t.getAmount(), t.getTransactionDate(), t.getDescription());
                }
                loadTransactions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "新增交易失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 新增：编辑交易
    private void editTransaction() {
        Transaction sel = getSelectedTransaction();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的交易。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TransactionDialog dialog = new TransactionDialog(sel);
        dialog.setVisible(true);
        Transaction t = dialog.getTransaction();
        if (t != null) {
            try {
                t.setTransactionId(sel.getTransactionId());
                transactionService.updateTransaction(t);
                loadTransactions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "编辑交易失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 新增：删除交易
    private void deleteTransaction() {
        Transaction sel = getSelectedTransaction();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "请选择要删除的交易。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int ret = JOptionPane.showConfirmDialog(this, "确认要删除该交易吗？", "删除确认", JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            try {
                transactionService.deleteTransaction(sel.getTransactionId(), true);
                loadTransactions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "删除交易失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 新增：刷新功能
    private void refreshTransactions() {
        loadTransactions();
    }
    private class TransactionDialog extends JDialog {
        // 实际字段
        private JComboBox<TransactionType> typeCombo;
        private JTextField amountField, descField;
        private JComboBox<String> srcAccountCombo, dstAccountCombo;
        private JComboBox<String> categoryCombo;
        private JDateChooser dateChooser;
        private Transaction transaction;
        private boolean confirmed = false;

        public TransactionDialog(Transaction t) {
            setTitle(t == null ? "新增交易" : "编辑交易");
            setModal(true);
            setSize(400, 600);
            setLocationRelativeTo(null);
            setLayout(null);

            JLabel typeLbl = new JLabel("类型：");
            typeLbl.setBounds(30, 28, 70, 26);
            typeCombo = new JComboBox<>(TransactionType.values());
            typeCombo.setBounds(100, 28, 200, 26);

            JLabel amountLbl = new JLabel("金额：");
            amountLbl.setBounds(30, 68, 70, 26);
            amountField = new JTextField();
            amountField.setBounds(100, 68, 200, 26);

            JLabel srcLbl = new JLabel("源账户：");
            srcLbl.setBounds(30, 108, 90, 26);

            srcAccountCombo = new JComboBox<>();
            srcAccountCombo.setBounds(100, 108, 200, 26);
            // 加载账户
            try {
                List<Account> acts = accountService.getUserActiveAccounts(currentUserId);
                srcAccountCombo.addItem("");
                for (Account a : acts) srcAccountCombo.addItem(a.getAccountName());//
            } catch (Exception ignored) {}

            JLabel dstLbl = new JLabel("目标账户：");
            dstLbl.setBounds(30, 148, 90, 26);
            dstAccountCombo = new JComboBox<>();
            dstAccountCombo.setBounds(100, 148, 190, 26);
            try {
                List<Account> acts = accountService.getUserActiveAccounts(currentUserId);
                for (Account a : acts) dstAccountCombo.addItem(a.getAccountName());//
            } catch (Exception ignored) {}

            JLabel catLbl = new JLabel("分类：");
            catLbl.setBounds(30, 188, 70, 26);
            categoryCombo = new JComboBox<>();
            categoryCombo.setBounds(100, 188, 200, 26);
            try{
                List<Category> cats = categoryService.getAllCategories();
                for (Category c : cats) categoryCombo.addItem(c.getCategoryName());
            }catch (Exception ignored){}

            JLabel dateLbl = new JLabel("日期：");
            dateLbl.setBounds(30, 228, 70, 26);
            dateChooser = new JDateChooser();
            dateChooser.setBounds(100, 228, 200, 26);

            JLabel descLbl = new JLabel("描述：");
            descLbl.setBounds(30, 268, 70, 26);
            descField = new JTextField();
            descField.setBounds(100, 268, 200, 26);

            JButton okBtn = new JButton("确定");
            okBtn.setBounds(80, 300, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.setBounds(200, 300, 90, 28);

            add(typeLbl); add(typeCombo);
            add(amountLbl); add(amountField);
            add(srcLbl); add(srcAccountCombo);
            add(dstLbl); add(dstAccountCombo);
            add(catLbl); add(categoryCombo);
            add(dateLbl); add(dateChooser);
            add(descLbl); add(descField);
            add(okBtn); add(cancelBtn);

            if (t != null) {
                typeCombo.setSelectedItem(t.getTransactionType());
                amountField.setText(t.getAmount().toString());
                srcAccountCombo.setSelectedItem(t.getSourceAccountId());
                dstAccountCombo.setSelectedItem(t.getDestinationAccountId());
                categoryCombo.setSelectedItem(t.getCategoryId());
                dateChooser.setDate(Date.from(t.getTransactionDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                descField.setText(t.getDescription());
            }
            okBtn.addActionListener(e -> {
                try {
                    TransactionType type = (TransactionType) typeCombo.getSelectedItem();
                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    Account src = (Account) srcAccountCombo.getSelectedItem();
                    Account dst = (Account) dstAccountCombo.getSelectedItem();
                    int cat = categoryCombo.getSelectedItem() != null ? (int) categoryCombo.getSelectedItem() : -1;
                    LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String desc = descField.getText().trim();

                    transaction = new Transaction();
                    transaction.setTransactionType(type);
                    transaction.setAmount(amount);
                    transaction.setSourceAccountId(src != null ? src.getAccountId() : -1);
                    transaction.setDestinationAccountId(dst != null ? dst.getAccountId() : -1);
                    transaction.setCategoryId(cat);
                    transaction.setTransactionDate(date);
                    transaction.setDescription(desc);
                    confirmed = true;
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "请填写完整且正确的信息", "警告", JOptionPane.WARNING_MESSAGE);
                }
            });
            cancelBtn.addActionListener(e -> dispose());
        }
        public Transaction getTransaction() {
            return confirmed ? transaction : null;
        }
    }
    /**
     * 加载账户下拉框
     */
    private void loadUserAccounts() {
        try {
            accountComboBox.removeAllItems();
            accountComboBox.addItem(null); // "全部账户"

            List<Account> activeAccounts = accountService.getUserActiveAccounts(currentUserId);

            if (activeAccounts != null) {
                for (Account a : activeAccounts) accountComboBox.addItem(a.getAccountName());//
              //  for (Account a : activeAccounts) accountNameComboBox.addItem(a.getAccountName());//
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载账户失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 加载交易数据
     */
    private void loadTransactions() {
        try {
            LocalDate startDate = startDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = endDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            TransactionType type = (TransactionType) typeComboBox.getSelectedItem();
            Account selectedAccount = (Account) accountComboBox.getSelectedItem();
            int accountId = selectedAccount != null ? selectedAccount.getAccountId() : -1;

            int offset = (currentPage - 1) * pageSize;
            List<Transaction> transactions = transactionService.getTransactionsWithPagination(
                    currentUserId, startDate, endDate, type, accountId, offset, pageSize
            );
            int totalRecords = transactionService.countByUserId(currentUserId, startDate, endDate, type);
            totalPages = (totalRecords + pageSize - 1) / pageSize;
            if (totalPages == 0) totalPages = 1;

            tableModel.setTransactions(transactions);
            tableModel.fireTableDataChanged();

            Map<Integer, BigDecimal> incomeByCategory = transactionService.calculateIncomeByCategory(currentUserId, startDate, endDate);
            Map<Integer, BigDecimal> expenseByCategory = transactionService.calculateExpenseByCategory(currentUserId, startDate, endDate);
            categoryStatsModel.setStats(incomeByCategory, expenseByCategory);
            categoryStatsModel.fireTableDataChanged();

            BigDecimal totalIncome = transactionService.calculateTotalIncome(currentUserId, startDate, endDate);
            BigDecimal totalExpense = transactionService.calculateTotalExpense(currentUserId, startDate, endDate);
            incomeLabel.setText(NumberFormatter.format(totalIncome));
            expenseLabel.setText(NumberFormatter.format(totalExpense));

            prevPageBtn.setEnabled(currentPage > 1);
            nextPageBtn.setEnabled(currentPage < totalPages);
            pageNumField.setText(String.valueOf(currentPage));
            totalPagesLabel.setText(String.valueOf(totalPages));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载交易失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 加载最近交易
     */
    private void loadRecentTransactions() {
        try {
            List<Transaction> recentTransactions = transactionService.getRecentTransactions(currentUserId, 15);
            tableModel.setTransactions(recentTransactions);
            tableModel.fireTableDataChanged();
            prevPageBtn.setEnabled(false);
            nextPageBtn.setEnabled(false);
            pageNumField.setText("1");
            totalPagesLabel.setText("1");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载最近交易失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 交易表格模型
     */
    private static class TransactionTableModel extends AbstractTableModel {
        private final String[] columnNames = {"序号", "日期", "类型", "金额", "源账户", "目标账户", "分类", "描述"};
        private List<Transaction> transactions = new ArrayList<>();

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions != null ? transactions : new ArrayList<>();
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
                case 0: return row+1;
                case 1: return DateFormatter.format(t.getTransactionDate());
                case 2: return t.getTransactionType().name();
                case 3: return NumberFormatter.format(t.getAmount());
                case 4: return t.getSourceAccountName() != null ? t.getSourceAccountName() : "-";
                case 5: return t.getDestinationAccountName() != null ? t.getDestinationAccountName() : "-";
                case 6: return t.getCategoryName();
                case 7: return t.getDescription();
                default: return null;
            }
        }
    }

    /**
     * 分类统计表格模型
     */
    private static class CategoryStatsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"分类", "收入金额", "支出金额"};
        private List<Map.Entry<String, BigDecimal[]>> stats = new ArrayList<>();

        public void setStats(Map<Integer, BigDecimal> incomeByCategory, Map<Integer, BigDecimal> expenseByCategory) {
            stats.clear();
            Set<Integer> allCategories = new HashSet<>();
            allCategories.addAll(incomeByCategory.keySet());
            allCategories.addAll(expenseByCategory.keySet());
            for (Integer catId : allCategories) {
                String catName = "分类" + catId; // TODO: 替换为实际分类名称
                BigDecimal income = incomeByCategory.getOrDefault(catId, BigDecimal.ZERO);
                BigDecimal expense = expenseByCategory.getOrDefault(catId, BigDecimal.ZERO);
                stats.add(Map.entry(catName, new BigDecimal[]{income, expense}));
            }
        }

        @Override
        public int getRowCount() {
            return stats.size();
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
            Map.Entry<String, BigDecimal[]> entry = stats.get(row);
            switch (column) {
                case 0: return entry.getKey();
                case 1: return NumberFormatter.format(entry.getValue()[0]);
                case 2: return NumberFormatter.format(entry.getValue()[1]);
                default: return null;
            }
        }
    }
}