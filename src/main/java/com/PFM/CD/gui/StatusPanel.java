package com.PFM.CD.gui;

import com.PFM.CD.entity.User;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */


public class StatusPanel extends JPanel {
    private JLabel statusLabel;

    public StatusPanel(User user) {
        setLayout(new BorderLayout());
        statusLabel = new JLabel("欢迎，" + user.getUsername());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        add(statusLabel, BorderLayout.WEST);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
