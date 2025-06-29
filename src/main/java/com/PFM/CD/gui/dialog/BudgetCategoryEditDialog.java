package com.PFM.CD.gui.dialog;

import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.Category;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-29
 * @Description:
 * @Version: 17.0
 */



// ==== 预算分类分配编辑弹窗 ====
public class BudgetCategoryEditDialog extends JDialog {
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
        allocLabel.setBounds(30, 62, 90, 26);
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
