package com.PFM.CD.gui.dialog;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.Category;
import com.PFM.CD.gui.panel.BudgetsPanel;
import com.PFM.CD.service.exception.ServiceException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-29
 * @Description:
 * @Version: 17.0
 */


// ==== 预算分类分配/编辑弹窗 ====
public class BudgetCategoryDialog extends JDialog {
    private final Budget budget;
    private final BudgetsPanel.BudgetController controller;
    private boolean changed = false;
    private JTable catTable;
    private DefaultTableModel catTableModel;
    private JButton addBtn, editBtn, delBtn;

    public BudgetCategoryDialog(Budget budget, BudgetsPanel.BudgetController controller) {
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

        String[] cols = {"序号", "分类", "已分配", "已用", "剩余", "进度(%)"};
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

        // 双击分配分类
        catTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && catTable.getSelectedRow() != -1) {
                    editBudgetCategory();
                }
            }
        });
    }

    private void loadBudgetCategories() {
        catTableModel.setRowCount(0);
        java.util.List<BudgetCategory> list = controller.getBudgetCategories(budget.getBudgetId());
        int rowNum = 1;
        for (BudgetCategory bc : list) {
            catTableModel.addRow(new Object[]{
                    rowNum++,
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
            if (bc.getBudgetCategoryId()!=0) return bc;
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