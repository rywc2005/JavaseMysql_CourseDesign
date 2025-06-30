package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.PeriodType;
import com.PFM.CD.gui.style;
import com.PFM.CD.gui.dialog.BudgetCategoryDialog;
import com.PFM.CD.gui.dialog.BudgetDialog;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.BudgetService;
import com.PFM.CD.service.interfaces.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        initComponents();
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

    private void initComponents() {
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

        refreshButton = new style().createFlatButton("刷新", new Color(80, 170, 230), new Color(105, 200, 255));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadBudgets());

        topPanelRow1.add(refreshButton);

        // 第二行：功能按钮
        JPanel topPanelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        topPanelRow2.setOpaque(false);

        addButton = new style().createFlatButton("新建预算", new Color(0, 123, 255), new Color(30, 150, 255));
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addBudget());

        editButton = new style().createFlatButton("编辑预算", new Color(70, 180, 100), new Color(110, 220, 140));
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> editBudget());

        deleteButton = new style().createFlatButton("删除预算", new Color(230, 70, 70), new Color(240, 100, 100));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteBudget());

        categoryButton = new style().createFlatButton("分配/管理分类", new Color(130, 90, 220), new Color(180, 140, 255));
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
        String[] columns = {"序号", "预算名称", "周期", "起始", "结束", "总额"};//"已分配", "已用", "进度(%)"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        new style().styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
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
//                    b.getAllocatedTotal(),
//                    b.getSpentTotal(),
//                    String.format("%.2f", b.getUsagePercentage())
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
                // 加载所有预算，包括关联的分类

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
}