package com.PFM.CD.gui.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-27
 * @Description:
 * @Version: 17.0
 */


public class style {

    /**
     * FlatLaf 风格扁平按钮
     */
    public JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        btn.setBackground(color);
        btn.setForeground(Color.DARK_GRAY);
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

    /**
     * 表格美化
     */
    public void styleTable(JTable table) {
        table.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 20));
        table.getTableHeader().setBackground(new Color(238, 242, 255));
        table.getTableHeader().setForeground(new Color(55, 80, 150));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(220, 222, 230));
    }

}
