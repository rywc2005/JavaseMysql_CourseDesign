package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.PeriodType;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.BudgetService;
import com.PFM.CD.service.interfaces.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 预算面板：创建预算、分配预算、监控执行
 * 风格对齐TransactionsPanel，表格第一列为"序号"
 */
public class BudgetsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, categoryButton, refreshButton;
    private BudgetController controller;
    private int userId;

    public BudgetsPanel(int userId, BudgetService budgetService, CategoryService categoryService) {
        this.userId = userId;
        this.controller = new BudgetControllerImpl(budgetService, categoryService);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(247, 249, 254));
        setBorder(new EmptyBorder(12, 18, 12, 18));

        // 顶部工具栏分两行
        JPanel topPanelOuter = new JPanel();
        topPanelOuter.setLayout(new BoxLayout(topPanelOuter, BoxLayout.Y_AXIS));
        topPanelOuter.setOpaque(false);

        // 第一行：刷新
        JPanel topPanelRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topPanelRow1.setOpaque(false);

        refreshButton = createFlatButton("刷新", new Color(80, 170, 230), new Color(105, 200, 255));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadBudgets());

        topPanelRow1.add(refreshButton);

        // 第二行：功能按钮
        JPanel topPanelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        topPanelRow2.setOpaque(false);

        addButton = createFlatButton("新建预算", new Color(0, 123, 255), new Color(30, 150, 255));
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addBudget());

        editButton = createFlatButton("编辑预算", new Color(70, 180, 100), new Color(110, 220, 140));
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> editBudget());

        deleteButton = createFlatButton("删除预算", new Color(230, 70, 70), new Color(240, 100, 100));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteBudget());

        categoryButton = createFlatButton("分配/管理分类", new Color(130, 90, 220), new Color(180, 140, 255));
        categoryButton.setPreferredSize(new Dimension(250, 40));
        categoryButton.setForeground(Color.WHITE);
        categoryButton.addActionListener(e -> manageCategories());

        topPanelRow2.add(addButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(editButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(deleteButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(categoryButton);

        topPanelOuter.add(topPanelRow1);
        topPanelOuter.add(Box.createVerticalStrut(8));
        topPanelOuter.add(topPanelRow2);

        add(topPanelOuter, BorderLayout.NORTH);

        // 表格
        String[] columns = {"序号", "预算名称", "周期", "起始", "结束", "总额", "已分配", "已用", "进度(%)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadBudgets();

        // 双击分配分类
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    manageCategories();
                }
            }
        });
    }

    public BudgetsPanel() {}

    private void loadBudgets() {
        tableModel.setRowCount(0);
        List<Budget> budgetList = controller.queryBudgets(userId);
        int rowNum = 1;
        for (Budget b : budgetList) {
            tableModel.addRow(new Object[]{
                    rowNum++,
                    b.getName(),
                    b.getPeriodType().getDisplayName(),
                    b.getStartDate(),
                    b.getEndDate(),
                    b.getTotalAmount(),
                    b.getAllocatedTotal(),
                    b.getSpentTotal(),
                    String.format("%.2f", b.getUsagePercentage())
            });
        }
    }

    private Budget getSelectedBudget() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        // 序号列是第0列，预算名称是第1列
        String name = (String) tableModel.getValueAt(row, 1);
        List<Budget> budgetList = controller.queryBudgets(userId);
        for (Budget b : budgetList) {
            if (b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

    private void addBudget() {
        BudgetDialog dialog = new BudgetDialog(null);
        dialog.setVisible(true);
        Budget budget = dialog.getBudget();
        if (budget != null) {
            try {
                controller.addBudget(userId, budget.getName(), budget.getPeriodType(),
                        budget.getStartDate(), budget.getTotalAmount());
                loadBudgets();
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void editBudget() {
        Budget budget = getSelectedBudget();
        if (budget == null) {
            showWarn("请选择要编辑的预算。");
            return;
        }
        BudgetDialog dialog = new BudgetDialog(budget);
        dialog.setVisible(true);
        Budget updated = dialog.getBudget();
        if (updated != null) {
            try {
                budget.setName(updated.getName());
                budget.setPeriodType(updated.getPeriodType());
                budget.setStartDate(updated.getStartDate());
                budget.setTotalAmount(updated.getTotalAmount());
                controller.updateBudget(budget);
                loadBudgets();
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void deleteBudget() {
        Budget budget = getSelectedBudget();
        if (budget == null) {
            showWarn("请选择要删除的预算。");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "确定要删除预算【" + budget.getName() + "】及其所有分配吗？", "删除确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                controller.deleteBudget(budget.getBudgetId());
                loadBudgets();
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void manageCategories() {
        Budget budget = getSelectedBudget();
        if (budget == null) {
            showWarn("请选择预算后操作。");
            return;
        }
        BudgetCategoryDialog dialog = new BudgetCategoryDialog(budget, controller);
        dialog.setVisible(true);
        if (dialog.isChanged()) {
            loadBudgets();
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // === 样式工具 ===
    private JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 34, 10, 34));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("微软雅黑", Font.PLAIN, 22));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 20));
        table.getTableHeader().setBackground(new Color(238, 242, 255));
        table.getTableHeader().setForeground(new Color(55, 80, 150));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(220, 222, 230));
    }

    // ==== Controller层 ====

    public interface BudgetController {
        List<Budget> queryBudgets(int userId);
        void addBudget(int userId, String name, PeriodType periodType, LocalDate start, BigDecimal total) throws ServiceException;
        void updateBudget(Budget budget) throws ServiceException;
        void deleteBudget(int budgetId) throws ServiceException;
        Budget getBudgetById(int budgetId);
        List<Category> getExpenseCategories();
        List<BudgetCategory> getBudgetCategories(int budgetId);
        void addBudgetCategory(int budgetId, int categoryId, BigDecimal amount) throws ServiceException;
        void updateBudgetCategory(int budgetCategoryId, BigDecimal amount) throws ServiceException;
        void deleteBudgetCategory(int budgetCategoryId) throws ServiceException;
    }

    public static class BudgetControllerImpl implements BudgetController {
        private final BudgetService budgetService;
        private final CategoryService categoryService;

        public BudgetControllerImpl(BudgetService budgetService, CategoryService categoryService) {
            this.budgetService = budgetService;
            this.categoryService = categoryService;
        }

        @Override
        public List<Budget> queryBudgets(int userId) {
            try {
                return budgetService.getUserBudgets(userId);
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(null, "加载预算失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return java.util.Collections.emptyList();
            }
        }

        @Override
        public void addBudget(int userId, String name, PeriodType periodType, LocalDate start, BigDecimal total) throws ServiceException {
            budgetService.createBudget(userId, name, periodType, start, total);
        }

        @Override
        public void updateBudget(Budget budget) throws ServiceException {
            budgetService.updateBudget(budget);
        }

        @Override
        public void deleteBudget(int budgetId) throws ServiceException {
            budgetService.deleteBudget(budgetId);
        }

        @Override
        public Budget getBudgetById(int budgetId) {
            try {
                return budgetService.getBudgetWithCategories(budgetId);
            } catch (ServiceException e) {
                return null;
            }
        }

        @Override
        public List<Category> getExpenseCategories() {
            try {
                return categoryService.getCategoriesByType(CategoryType.EXPENSE);
            } catch (ServiceException e) {
                return java.util.Collections.emptyList();
            }
        }

        @Override
        public List<BudgetCategory> getBudgetCategories(int budgetId) {
            try {
                return budgetService.getBudgetCategories(budgetId);
            } catch (ServiceException e) {
                return java.util.Collections.emptyList();
            }
        }

        @Override
        public void addBudgetCategory(int budgetId, int categoryId, BigDecimal amount) throws ServiceException {
            budgetService.addBudgetCategory(budgetId, categoryId, amount);
        }

        @Override
        public void updateBudgetCategory(int budgetCategoryId, BigDecimal amount) throws ServiceException {
            budgetService.updateBudgetCategory(budgetCategoryId, amount);
        }

        @Override
        public void deleteBudgetCategory(int budgetCategoryId) throws ServiceException {
            budgetService.deleteBudgetCategory(budgetCategoryId);
        }
    }

    // ==== 预算新建/编辑弹窗 ====
    private static class BudgetDialog extends JDialog {
        private JTextField nameField, amountField;
        private JComboBox<PeriodType> periodCombo;
        private JSpinner startDateSpinner;
        private Budget budget;
        private boolean confirmed = false;

        public BudgetDialog(Budget b) {
            setTitle(b == null ? "新建预算" : "编辑预算");
            setModal(true);
            setSize(400, 270);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel form = new JPanel(null);
            form.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel("预算名称：");
            nameLabel.setBounds(30, 24, 80, 26);
            nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            nameField = new JTextField();
            nameField.setBounds(110, 24, 240, 26);
            nameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            form.add(nameLabel); form.add(nameField);

            JLabel periodLabel = new JLabel("周期类型：");
            periodLabel.setBounds(30, 62, 80, 26);
            periodLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            periodCombo = new JComboBox<>(PeriodType.values());
            periodCombo.setBounds(110, 62, 240, 26);
            periodCombo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            form.add(periodLabel); form.add(periodCombo);

            JLabel startLabel = new JLabel("起始日期：");
            startLabel.setBounds(30, 100, 80, 26);
            startLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            startDateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
            startDateSpinner.setEditor(dateEditor);
            startDateSpinner.setBounds(110, 100, 240, 26);
            startDateSpinner.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            form.add(startLabel); form.add(startDateSpinner);

            JLabel amountLabel = new JLabel("预算总额：");
            amountLabel.setBounds(30, 138, 80, 26);
            amountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            amountField = new JTextField();
            amountField.setBounds(110, 138, 240, 26);
            amountField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            form.add(amountLabel); form.add(amountField);

            JButton okBtn = new JButton("确定");
            okBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            okBtn.setBounds(100, 190, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            cancelBtn.setBounds(210, 190, 90, 28);

            form.add(okBtn); form.add(cancelBtn);

            add(form, BorderLayout.CENTER);

            if (b != null) {
                nameField.setText(b.getName());
                periodCombo.setSelectedItem(b.getPeriodType());
                startDateSpinner.setValue(java.sql.Date.valueOf(b.getStartDate()));
                amountField.setText(b.getTotalAmount().toString());
            } else {
                startDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
            }

            okBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                PeriodType periodType = (PeriodType) periodCombo.getSelectedItem();
                LocalDate startDate = ((java.util.Date) startDateSpinner.getValue()).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String amountStr = amountField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请输入预算名称", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (periodType == null) {
                    JOptionPane.showMessageDialog(this, "请选择周期类型", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountStr);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(this, "预算总额必须为正数", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "请输入正确的预算金额", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                confirmed = true;
                if (b == null) {
                    budget = new Budget(0, 0, name, periodType, startDate, null, amount);
                } else {
                    budget = new Budget(b.getBudgetId(), b.getUserId(), name, periodType, startDate, null, amount);
                }
                dispose();
            });
            cancelBtn.addActionListener(e -> dispose());
        }

        public Budget getBudget() {
            return confirmed ? budget : null;
        }
    }

    // ==== 预算分类分配/编辑弹窗 ====
    private static class BudgetCategoryDialog extends JDialog {
        private final Budget budget;
        private final BudgetController controller;
        private boolean changed = false;
        private JTable catTable;
        private DefaultTableModel catTableModel;
        private JButton addBtn, editBtn, delBtn;

        public BudgetCategoryDialog(Budget budget, BudgetController controller) {
            this.budget = controller.getBudgetById(budget.getBudgetId());
            this.controller = controller;
            setTitle("预算分类分配 - " + budget.getName());
            setModal(true);
            setSize(650, 350);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            addBtn = new JButton("新增分配");
            addBtn.addActionListener(e -> addBudgetCategory());
            editBtn = new JButton("编辑分配");
            editBtn.addActionListener(e -> editBudgetCategory());
            delBtn = new JButton("删除分配");
            delBtn.addActionListener(e -> deleteBudgetCategory());
            topPanel.add(addBtn);
            topPanel.add(editBtn);
            topPanel.add(delBtn);
            add(topPanel, BorderLayout.NORTH);

            String[] cols = {"ID", "分类", "已分配", "已用", "剩余", "进度(%)"};
            catTableModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int row, int col) { return false; }
            };
            catTable = new JTable(catTableModel);
            catTable.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            catTable.setRowHeight(28);
            catTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 16));
            catTable.getTableHeader().setBackground(new Color(238, 242, 255));
            catTable.getTableHeader().setForeground(new Color(55, 80, 150));
            ((DefaultTableCellRenderer) catTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            catTable.setSelectionBackground(new Color(230, 240, 255));
            catTable.setGridColor(new Color(220, 222, 230));
            catTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scroll = new JScrollPane(catTable);
            add(scroll, BorderLayout.CENTER);

            loadBudgetCategories();
        }

        private void loadBudgetCategories() {
            catTableModel.setRowCount(0);
            List<BudgetCategory> list = controller.getBudgetCategories(budget.getBudgetId());
            for (BudgetCategory bc : list) {
                catTableModel.addRow(new Object[]{
                        bc.getBudgetCategoryId(),
                        bc.getCategoryName(),
                        bc.getAllocatedAmount(),
                        bc.getSpentAmount(),
                        bc.getRemainingAmount(),
                        String.format("%.2f", bc.getUsagePercentage())
                });
            }
        }

        private BudgetCategory getSelectedBudgetCategory() {
            int row = catTable.getSelectedRow();
            if (row == -1) return null;
            int id = (int) catTableModel.getValueAt(row, 0);
            for (BudgetCategory bc : controller.getBudgetCategories(budget.getBudgetId())) {
                if (bc.getBudgetCategoryId() == id) return bc;
            }
            return null;
        }

        private void addBudgetCategory() {
            List<Category> categories = controller.getExpenseCategories();
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "无可用支出分类，请先维护分类。", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BudgetCategoryEditDialog dialog = new BudgetCategoryEditDialog(categories, null, budget.getRemainingAllocatable());
            dialog.setVisible(true);
            BudgetCategory bc = dialog.getBudgetCategory();
            if (bc != null) {
                try {
                    controller.addBudgetCategory(budget.getBudgetId(), bc.getCategoryId(), bc.getAllocatedAmount());
                    changed = true;
                    loadBudgetCategories();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void editBudgetCategory() {
            BudgetCategory bc = getSelectedBudgetCategory();
            if (bc == null) {
                JOptionPane.showMessageDialog(this, "请选择要编辑的分配。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            BigDecimal maxAlloc = budget.getRemainingAllocatable().add(bc.getAllocatedAmount());
            BudgetCategoryEditDialog dialog = new BudgetCategoryEditDialog(null, bc, maxAlloc);
            dialog.setVisible(true);
            BudgetCategory updated = dialog.getBudgetCategory();
            if (updated != null) {
                try {
                    controller.updateBudgetCategory(bc.getBudgetCategoryId(), updated.getAllocatedAmount());
                    changed = true;
                    loadBudgetCategories();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void deleteBudgetCategory() {
            BudgetCategory bc = getSelectedBudgetCategory();
            if (bc == null) {
                JOptionPane.showMessageDialog(this, "请选择要删除的分配。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int ret = JOptionPane.showConfirmDialog(this, "确认删除该分类分配？", "删除确认", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteBudgetCategory(bc.getBudgetCategoryId());
                    changed = true;
                    loadBudgetCategories();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        public boolean isChanged() { return changed; }
    }

    // ==== 预算分类分配编辑弹窗 ====
    private static class BudgetCategoryEditDialog extends JDialog {
        private JComboBox<Category> catCombo;
        private JTextField allocField;
        private BudgetCategory budgetCategory;
        private boolean confirmed = false;

        public BudgetCategoryEditDialog(List<Category> categories, BudgetCategory bc, BigDecimal maxAlloc) {
            setTitle(bc == null ? "新增预算分类分配" : "编辑预算分类分配");
            setModal(true);
            setSize(350, 180);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel form = new JPanel(null);
            form.setBackground(Color.WHITE);

            JLabel catLabel = new JLabel("分类：");
            catLabel.setBounds(30, 24, 60, 26);
            catLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            if (categories != null) {
                catCombo = new JComboBox<>(categories.toArray(new Category[0]));
                catCombo.setBounds(100, 24, 200, 26);
                catCombo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                form.add(catLabel); form.add(catCombo);
            }

            JLabel allocLabel = new JLabel("分配金额：");
            allocLabel.setBounds(30, 62, 60, 26);
            allocLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            allocField = new JTextField();
            allocField.setBounds(100, 62, 200, 26);
            allocField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            form.add(allocLabel); form.add(allocField);

            JButton okBtn = new JButton("确定");
            okBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            okBtn.setBounds(60, 110, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            cancelBtn.setBounds(170, 110, 90, 28);

            form.add(okBtn); form.add(cancelBtn);

            add(form, BorderLayout.CENTER);

            if (bc != null) {
                allocField.setText(bc.getAllocatedAmount().toString());
            }

            okBtn.addActionListener(e -> {
                BigDecimal alloc;
                try {
                    alloc = new BigDecimal(allocField.getText().trim());
                    if (alloc.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(this, "分配金额必须为正数", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (alloc.compareTo(maxAlloc) > 0) {
                        JOptionPane.showMessageDialog(this, "分配金额不能超过可用金额: " + maxAlloc, "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "请输入正确的金额", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (bc == null) {
                    Category selected = (Category) catCombo.getSelectedItem();
                    if (selected == null) {
                        JOptionPane.showMessageDialog(this, "请选择分类", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    budgetCategory = new BudgetCategory();
                    budgetCategory.setCategoryId(selected.getCategoryId());
                    budgetCategory.setCategoryName(selected.getCategoryName());
                    budgetCategory.setAllocatedAmount(alloc);
                    budgetCategory.setSpentAmount(BigDecimal.ZERO);
                } else {
                    budgetCategory = new BudgetCategory();
                    budgetCategory.setBudgetCategoryId(bc.getBudgetCategoryId());
                    budgetCategory.setAllocatedAmount(alloc);
                    budgetCategory.setSpentAmount(bc.getSpentAmount());
                    budgetCategory.setCategoryId(bc.getCategoryId());
                    budgetCategory.setCategoryName(bc.getCategoryName());
                }
                confirmed = true;
                dispose();
            });
            cancelBtn.addActionListener(e -> dispose());
        }

        public BudgetCategory getBudgetCategory() {
            return confirmed ? budgetCategory : null;
        }
    }
}