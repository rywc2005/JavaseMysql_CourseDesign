package com.PFM.CD.gui.dialog;

import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.service.dto.AccountDto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-27
 * @Description:
 * @Version: 17.0
 */


public class AccountDialog extends JDialog {
    private JTextField nameField;
    private JTextField balanceField;
    private AccountDto account;
    private boolean confirmed = false;

    public AccountDialog(AccountDto acc) {
        setTitle(acc == null ? "新建账户" : "编辑账户");
        setModal(true);
        setSize(360, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("账户名称：");
        nameLabel.setBounds(30, 24, 80, 26);
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        nameField = new JTextField();
        nameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        nameField.setBounds(110, 24, 200, 26);
        form.add(nameLabel); form.add(nameField);

        JLabel balanceLabel = new JLabel("初始余额：");
        balanceLabel.setBounds(30, 62, 80, 26);
        balanceLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        balanceField = new JTextField();
        balanceField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        balanceField.setBounds(110, 62, 200, 26);
        form.add(balanceLabel); form.add(balanceField);

        JButton okBtn = new JButton("确定");
        okBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        okBtn.setBounds(70, 110, 90, 28);
        okBtn.setBackground(new Color(0, 123, 255));
        okBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cancelBtn.setBounds(190, 110, 90, 28);

        form.add(okBtn); form.add(cancelBtn);

        add(form, BorderLayout.CENTER);

        if (acc != null) {
            nameField.setText(acc.getAccountName());
            balanceField.setText(acc.getBalance().toString());
            balanceField.setEditable(false);
        } else {
            balanceField.setText("0.00");
        }

        okBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String balanceStr = balanceField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入账户名称", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BigDecimal balance = BigDecimal.ZERO;
            try {
                balance = new BigDecimal(balanceStr);
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "余额不能为负数", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "请输入正确的余额数字", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            confirmed = true;
            if (acc == null)
                account = new AccountDto(0, 0, name, balance, AccountStatus.ACTIVE);
            else
                account = new AccountDto(acc.getAccountId(), acc.getUserId(), name, acc.getBalance(), acc.getStatus());
            dispose();
        });
        cancelBtn.addActionListener(e -> dispose());
    }

    public AccountDto getAccount() {
        return confirmed ? account : null;
    }
    }
