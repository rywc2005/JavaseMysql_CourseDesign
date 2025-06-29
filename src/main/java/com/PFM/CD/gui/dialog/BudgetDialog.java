package com.PFM.CD.gui.dialog;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.enums.PeriodType;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-29
 * @Description:
 * @Version: 17.0
 */


// ==== 预算新建/编辑弹窗 ====
public  class BudgetDialog extends JDialog {
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
