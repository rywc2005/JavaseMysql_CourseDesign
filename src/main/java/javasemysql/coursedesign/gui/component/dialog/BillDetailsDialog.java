package javasemysql.coursedesign.gui.component.dialog;

import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.model.Bill;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-22
 * @Description:
 * @Version: 17.0
 */


public class BillDetailsDialog {
    private Bill bill;

    public BillDetailsDialog(MainFrame mainFrame, Bill bill) {
        this.bill = bill;
    }

    public void showDetails() {
        // 显示账单详情的逻辑
        System.out.println("账单详情：");
        System.out.println("ID: " + bill.getId());
        System.out.println("金额: " + bill.getAmount());
        System.out.println("日期: " + bill.getDueDate());
        System.out.println("状态: " + bill.getStatus());
    }

    public void setVisible(boolean b) {
        // 设置对话框的可见性
        if (b) {
            showDetails();
        } else {
            System.out.println("对话框已隐藏");
        }
    }
}
