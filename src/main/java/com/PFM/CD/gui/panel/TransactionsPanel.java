package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.gui.style;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.AccountService;
import com.PFM.CD.service.interfaces.CategoryService;
import com.PFM.CD.service.interfaces.TransactionService;
import com.PFM.CD.utils.format.DateFormatter;
import com.PFM.CD.utils.format.NumberFormatter;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

/**
 * 交易管理面板（支持查询、展示、统计交易记录，新增分页、账户过滤、分类统计等功能，高端大气风格优化）
 */
public class TransactionsPanel extends JPanel {
    // 添加常量
    private static final String CURRENT_DATE_TIME = "2025-06-29 18:38:10";
    private static final Logger logger = Logger.getLogger(TransactionsPanel.class.getName());

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final int currentUserId;
    private JTable transactionTable;
    private TransactionTableModel tableModel;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JComboBox<TransactionType> typeComboBox;
    private JComboBox<AccountItem> accountComboBox;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private int currentPage = 1;
    private int pageSize = 15;
    private int totalPages = 1;

    private JTextField pageNumField;
    private JButton prevPageBtn;
    private JButton nextPageBtn;
    private JLabel totalPagesLabel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, queryBtn, recentBtn;
    private JLabel statusLabel; // 状态标签，用于显示刷新状态

    private JTable categoryStatsTable;
    private CategoryStatsTableModel categoryStatsModel;

    // 账户项包装类 - 用于下拉框
    private static class AccountItem {
        private final Account account;
        private final boolean isAllAccounts;

        public AccountItem(Account account) {
            this.account = account;
            this.isAllAccounts = false;
        }

        public AccountItem() {
            this.account = null;
            this.isAllAccounts = true;
        }

        public Account getAccount() {
            return account;
        }

        public int getAccountId() {
            return account != null ? account.getAccountId() : -1;
        }

        public boolean isAllAccounts() {
            return isAllAccounts;
        }

        @Override
        public String toString() {
            return isAllAccounts ? "全部账户" : account.getAccountName();
        }
    }

    // 分类项包装类 - 用于下拉框
    private static class CategoryItem {
        private final Category category;

        public CategoryItem(Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        public int getCategoryId() {
            return category.getCategoryId();
        }

        @Override
        public String toString() {
            return category.getCategoryName();
        }
    }

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

        queryBtn = new style().createFlatButton("查询", new Color(51, 102, 255), new Color(80, 130, 255));
        queryBtn.setPreferredSize(new Dimension(120, 40));
        queryBtn.setForeground(Color.WHITE);
        queryBtn.addActionListener(e -> {
            currentPage = 1;
            showLoadingIndicator(true);
            try {
                loadTransactions();
                showSuccessMessage("查询成功", "成功查询到数据");
            } catch (Exception ex) {
                showErrorMessage("查询失败", ex.getMessage());
            } finally {
                showLoadingIndicator(false);
            }
        });

        recentBtn = new style().createFlatButton("最近几条", new Color(80, 170, 230), new Color(105, 200, 255));
        recentBtn.setPreferredSize(new Dimension(150, 40));
        recentBtn.setForeground(Color.WHITE);
        recentBtn.addActionListener(e -> {
            currentPage = 1;
            pageSize = 10;
            showLoadingIndicator(true);
            try {
                loadRecentTransactions();
                showSuccessMessage("查询成功", "成功加载最近的交易记录");
            } catch (Exception ex) {
                showErrorMessage("加载最近交易失败", ex.getMessage());
            } finally {
                showLoadingIndicator(false);
            }
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

        // 添加状态标签
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 128, 0));
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);

        // 组合顶部面板
        filterOuterPanel.add(filterRow1);
        filterOuterPanel.add(Box.createVerticalStrut(8));
        filterOuterPanel.add(filterRow2);
        filterOuterPanel.add(Box.createVerticalStrut(4));
        filterOuterPanel.add(statusPanel);

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

    /**
     * 新增交易 - 添加成功提示
     */
    private void addTransaction() {
        TransactionDialog dialog = new TransactionDialog(null);
        dialog.setVisible(true);
        Transaction t = dialog.getTransaction();
        if (t != null) {
            showLoadingIndicator(true);
            try {
                if (t.getTransactionType() == TransactionType.INCOME) {
                    transactionService.recordIncome(
                            currentUserId,
                            t.getDestinationAccountId(),
                            t.getCategoryId(),
                            t.getAmount(),
                            t.getTransactionDate(),
                            t.getDescription()
                    );
                } else if (t.getTransactionType() == TransactionType.EXPENSE) {
                    transactionService.recordExpense(
                            currentUserId,
                            t.getSourceAccountId(),
                            t.getCategoryId(),
                            t.getAmount(),
                            t.getTransactionDate(),
                            t.getDescription()
                    );
                }

                loadTransactions();

                // 显示成功消息
                String transactionType = t.getTransactionType() == TransactionType.INCOME ? "收入" : "支出";
                String formattedAmount = NumberFormatter.format(t.getAmount());
                showSuccessMessage("添加成功",
                        "成功添加" + transactionType + "交易\n金额: " + formattedAmount);

            } catch (Exception ex) {
                showErrorMessage("新增交易失败", ex.getMessage());
            } finally {
                showLoadingIndicator(false);
            }
        }
    }

    /**
     * 编辑交易 - 添加成功提示
     */
    private void editTransaction() {
        Transaction sel = getSelectedTransaction();
        if (sel == null) {
            showInfoMessage("提示", "请选择要编辑的交易");
            return;
        }

        showLoadingIndicator(true);
        try {
            // 获取完整的交易信息，包括账户和分类名称
            Transaction fullTransaction = transactionService.getTransactionById(sel.getTransactionId());

            TransactionDialog dialog = new TransactionDialog(fullTransaction);
            dialog.setVisible(true);
            Transaction t = dialog.getTransaction();
            if (t != null) {
                // 保持原有ID和用户ID
                t.setTransactionId(sel.getTransactionId());
                t.setUserId(currentUserId);

                transactionService.updateTransaction(t);
                loadTransactions();

                // 显示成功消息
                String transactionType = t.getTransactionType() == TransactionType.INCOME ? "收入" : "支出";
                String formattedAmount = NumberFormatter.format(t.getAmount());
                String formattedDate = DateFormatter.format(t.getTransactionDate());

                showSuccessMessage("编辑成功",
                        "成功编辑" + transactionType + "交易\n" +
                                "日期: " + formattedDate + "\n" +
                                "金额: " + formattedAmount);
            }
        } catch (Exception ex) {
            showErrorMessage("编辑交易失败", ex.getMessage());
        } finally {
            showLoadingIndicator(false);
        }
    }

    /**
     * 删除交易 - 添加成功提示
     */
    private void deleteTransaction() {
        Transaction sel = getSelectedTransaction();
        if (sel == null) {
            showInfoMessage("提示", "请选择要删除的交易");
            return;
        }

        String transactionType = sel.getTransactionType() == TransactionType.INCOME ? "收入" : "支出";
        String formattedAmount = NumberFormatter.format(sel.getAmount());
        String formattedDate = DateFormatter.format(sel.getTransactionDate());

        int ret = JOptionPane.showConfirmDialog(this,
                "确认要删除该" + transactionType + "交易吗？\n" +
                        "日期: " + formattedDate + "\n" +
                        "金额: " + formattedAmount + "\n" +
                        "此操作不可恢复！",
                "删除确认", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (ret == JOptionPane.YES_OPTION) {
            showLoadingIndicator(true);
            try {
                transactionService.deleteTransaction(sel.getTransactionId(), true);
                loadTransactions();

                // 显示成功消息
                showSuccessMessage("删除成功",
                        "成功删除" + transactionType + "交易\n" +
                                "日期: " + formattedDate + "\n" +
                                "金额: " + formattedAmount);

            } catch (Exception ex) {
                showErrorMessage("删除交易失败", ex.getMessage());
            } finally {
                showLoadingIndicator(false);
            }
        }
    }

    /**
     * 修复后的刷新功能 - 默认重置所有过滤条件
     * 包含视觉反馈和错误处理
     */
    private void refreshTransactions() {
        // 获取当前日期时间 (用于日志和显示)
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        showLoadingIndicator(true);

        try {
            // 重置日期为当前月份
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            startDateChooser.setDate(Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateChooser.setDate(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // 重置类型过滤
            typeComboBox.setSelectedItem(null);

            // 重置账户过滤
            accountComboBox.setSelectedIndex(0); // 选择"全部账户"

            // 重置分页状态
            currentPage = 1;
            pageSize = 15; // 恢复默认页大小

            // 刷新数据
            loadTransactions();

            // 显示成功消息 - 使用之前获取的格式化日期时间，而不是LocalDate对象
            showStatusMessage("数据已刷新 (" + CURRENT_DATE_TIME + ")");

            // 显示成功对话框
            showSuccessMessage("刷新成功",
                    "已重置所有过滤条件并刷新数据\n" +
                            "时间: " + CURRENT_DATE_TIME);
        } catch (Exception ex) {
            showErrorMessage("刷新失败", ex.getMessage());
            showStatusMessage("刷新失败：" + ex.getMessage());
        } finally {
            showLoadingIndicator(false);
        }
    }

    /**
     * 显示或隐藏加载指示器
     * @param show 是否显示加载指示器
     */
    private void showLoadingIndicator(boolean show) {
        // 设置鼠标等待光标
        setCursor(show ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());

        // 禁用或启用各控件
        typeComboBox.setEnabled(!show);
        accountComboBox.setEnabled(!show);
        startDateChooser.setEnabled(!show);
        endDateChooser.setEnabled(!show);
        addBtn.setEnabled(!show);
        editBtn.setEnabled(!show);
        deleteBtn.setEnabled(!show);
        refreshBtn.setEnabled(!show);
        queryBtn.setEnabled(!show);
        recentBtn.setEnabled(!show);

        // 禁用或启用表格
        if (show) {
            SwingUtilities.invokeLater(() -> {
                transactionTable.setEnabled(false);
                categoryStatsTable.setEnabled(false);
                statusLabel.setText("正在处理...");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                transactionTable.setEnabled(true);
                categoryStatsTable.setEnabled(true);
            });
        }
    }

    /**
     * 显示状态消息
     * @param message 状态消息
     */
    private void showStatusMessage(String message) {
        statusLabel.setText(message);

        // 5秒后自动清除消息
        Timer timer = new Timer(5000, e -> {
            statusLabel.setText("");
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * 显示成功消息对话框
     */
    private void showSuccessMessage(String title, String message) {
        showStatusMessage("操作成功: " + title);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示错误消息对话框
     */
    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示信息消息对话框
     */
    private void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 自动刷新方法，可用于定时刷新或外部调用
     */
    public void autoRefresh() {
        try {
            // 只刷新数据，不重置过滤条件
            loadTransactions();
            showStatusMessage("数据已自动刷新 (" + CURRENT_DATE_TIME + ")");
        } catch (Exception ex) {
            logger.warning("自动刷新失败: " + ex.getMessage());
        }
    }

    /**
     * 交易对话框
     */
    private class TransactionDialog extends JDialog {
        private JComboBox<TransactionType> typeCombo;
        private JTextField amountField, descField;
        private JComboBox<Account> accountCombo; // 根据交易类型动态显示源账户或目标账户
        private JComboBox<Category> categoryCombo;
        private JDateChooser dateChooser;
        private Transaction transaction;
        private boolean confirmed = false;
        private List<Account> accounts;
        private List<Category> incomeCategories;
        private List<Category> expenseCategories;

        public TransactionDialog(Transaction t) {
            setTitle(t == null ? "新增交易" : "编辑交易");
            setModal(true);
            setSize(450, 500);
            setLocationRelativeTo(null);

            // 加载账户和分类数据
            try {
                accounts = accountService.getUserActiveAccounts(currentUserId);
                incomeCategories = categoryService.getCategoriesByType(CategoryType.INCOME); // 收入分类
                expenseCategories = categoryService.getCategoriesByType(CategoryType.EXPENSE); // 支出分类
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            // 创建UI
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(15, 20, 15, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 5, 8, 5);

            // 交易类型
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("交易类型:"), gbc);

            typeCombo = new JComboBox<>(new TransactionType[]{TransactionType.INCOME, TransactionType.EXPENSE});
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            panel.add(typeCombo, gbc);

            // 金额
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0.0;
            panel.add(new JLabel("金额:"), gbc);

            amountField = new JTextField(15);
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            panel.add(amountField, gbc);

            // 账户（动态标签）
            JLabel accountLabel = new JLabel("收款账户:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0.0;
            panel.add(accountLabel, gbc);

            accountCombo = new JComboBox<>();
            for (Account account : accounts) {
                accountCombo.addItem(account);
            }
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 1.0;
            panel.add(accountCombo, gbc);

            // 分类
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0.0;
            panel.add(new JLabel("分类:"), gbc);

            categoryCombo = new JComboBox<>();
            updateCategoryCombo(TransactionType.INCOME); // 默认为收入分类
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.weightx = 1.0;
            panel.add(categoryCombo, gbc);

            // 日期
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0.0;
            panel.add(new JLabel("日期:"), gbc);

            dateChooser = new JDateChooser();
            dateChooser.setDate(new Date()); // 默认今天
            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.weightx = 1.0;
            panel.add(dateChooser, gbc);

            // 描述
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0.0;
            panel.add(new JLabel("描述:"), gbc);

            descField = new JTextField(15);
            gbc.gridx = 1;
            gbc.gridy = 5;
            gbc.weightx = 1.0;
            panel.add(descField, gbc);

            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("确定");
            JButton cancelButton = new JButton("取消");

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            // 添加到对话框
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            // 事件监听
            typeCombo.addActionListener(e -> {
                TransactionType selectedType = (TransactionType) typeCombo.getSelectedItem();
                if (selectedType == TransactionType.INCOME) {
                    accountLabel.setText("收款账户:");
                    updateCategoryCombo(TransactionType.INCOME);
                } else {
                    accountLabel.setText("付款账户:");
                    updateCategoryCombo(TransactionType.EXPENSE);
                }
            });

            okButton.addActionListener(e -> {
                if (validateAndSave()) {
                    confirmed = true;
                    dispose();
                }
            });

            cancelButton.addActionListener(e -> dispose());

            // 如果是编辑模式，设置初始值
            if (t != null) {
                typeCombo.setSelectedItem(t.getTransactionType());
                amountField.setText(t.getAmount().toString());

                // 设置账户
                if (t.getTransactionType() == TransactionType.INCOME) {
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).getAccountId() == t.getDestinationAccountId()) {
                            accountCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).getAccountId() == t.getSourceAccountId()) {
                            accountCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                // 设置分类
                updateCategoryCombo(t.getTransactionType());
                List<Category> categories = t.getTransactionType() == TransactionType.INCOME ?
                        incomeCategories : expenseCategories;

                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getCategoryId() == t.getCategoryId()) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }

                // 设置日期和描述
                dateChooser.setDate(Date.from(t.getTransactionDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                descField.setText(t.getDescription());
            }
        }

        /**
         * 根据交易类型更新分类下拉框
         */
        private void updateCategoryCombo(TransactionType type) {
            categoryCombo.removeAllItems();
            List<Category> categories = type == TransactionType.INCOME ? incomeCategories : expenseCategories;
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        }

        /**
         * 验证输入并保存交易
         */
        private boolean validateAndSave() {
            try {
                TransactionType type = (TransactionType) typeCombo.getSelectedItem();
                if (type == null) {
                    JOptionPane.showMessageDialog(this, "请选择交易类型", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请输入金额", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountText);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(this, "金额必须大于零", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "金额格式无效", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                Account selectedAccount = (Account) accountCombo.getSelectedItem();
                if (selectedAccount == null) {
                    JOptionPane.showMessageDialog(this, "请选择账户", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                Category selectedCategory = (Category) categoryCombo.getSelectedItem();
                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(this, "请选择分类", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                Date date = dateChooser.getDate();
                if (date == null) {
                    JOptionPane.showMessageDialog(this, "请选择日期", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                LocalDate transactionDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (transactionDate.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "交易日期不能是未来日期", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                String description = descField.getText().trim();

                // 创建交易对象
                transaction = new Transaction();
                transaction.setUserId(currentUserId);
                transaction.setTransactionType(type);
                transaction.setAmount(amount);
                transaction.setTransactionDate(transactionDate);
                transaction.setDescription(description);
                transaction.setCategoryId(selectedCategory.getCategoryId());

                // 根据交易类型设置源账户或目标账户
                if (type == TransactionType.INCOME) {
                    transaction.setDestinationAccountId(selectedAccount.getAccountId());
                    transaction.setSourceAccountId(null);
                } else {
                    transaction.setSourceAccountId(selectedAccount.getAccountId());
                    transaction.setDestinationAccountId(null);
                }

                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "输入验证失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        /**
         * 获取创建或编辑的交易
         */
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
            accountComboBox.addItem(new AccountItem()); // 添加"全部账户"选项

            List<Account> activeAccounts = accountService.getUserActiveAccounts(currentUserId);
            if (activeAccounts != null) {
                for (Account account : activeAccounts) {
                    accountComboBox.addItem(new AccountItem(account));
                }
            }
        } catch (Exception e) {
            showErrorMessage("加载账户失败", e.getMessage());
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

            // 获取选中的账户ID
            int accountId = -1; // 默认全部账户
            if (accountComboBox.getSelectedItem() != null) {
                AccountItem selectedItem = (AccountItem) accountComboBox.getSelectedItem();
                if (!selectedItem.isAllAccounts()) {
                    accountId = selectedItem.getAccountId();
                }
            }

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

            // 更新状态信息
            showStatusMessage("加载了 " + transactions.size() + " 条交易记录 (第 " + currentPage + "/" + totalPages + " 页)");

        } catch (Exception e) {
            showErrorMessage("加载交易失败", e.getMessage());
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

            // 更新状态信息
            showStatusMessage("加载了 " + recentTransactions.size() + " 条最近交易记录");

        } catch (Exception e) {
            showErrorMessage("加载最近交易失败", e.getMessage());
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
    private class CategoryStatsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"分类", "收入金额", "支出金额"};
        private List<Map.Entry<String, BigDecimal[]>> stats = new ArrayList<>();

        public void setStats(Map<Integer, BigDecimal> incomeByCategory, Map<Integer, BigDecimal> expenseByCategory) {
            stats.clear();
            Set<Integer> allCategories = new HashSet<>();

            allCategories.addAll(incomeByCategory.keySet());
            allCategories.addAll(expenseByCategory.keySet());

            Map<String, BigDecimal[]> categoryStats = new HashMap<>();

            for (Integer catId : allCategories) {
                String catName = null;
                try {
                    catName = categoryService.getCategoryById(catId).getCategoryName();
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
                BigDecimal income = incomeByCategory.getOrDefault(catId, BigDecimal.ZERO);
                BigDecimal expense = expenseByCategory.getOrDefault(catId, BigDecimal.ZERO);
                categoryStats.put(catName, new BigDecimal[]{income, expense});
            }

            stats.addAll(categoryStats.entrySet());
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