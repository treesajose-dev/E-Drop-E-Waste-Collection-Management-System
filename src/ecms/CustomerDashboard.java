/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.time.LocalDate;

public class CustomerDashboard extends JFrame {

    String username;
    int custId;
    JLabel lblPending, lblAssigned, lblCompleted, lblCancelled;
    JPanel contentPanel;
    JTable table;
    DefaultTableModel model;

    public CustomerDashboard(String username) {
        this.username = username;
        fetchCustomerId();

        setTitle("Customer Dashboard");
        setSize(900,550);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(0,102,204));

        createTopBar();
        createStatusBoxes();
        createContentPanel();

        loadCounts();
        loadItems("pending");

        setVisible(true);
    }
    
    private void fetchCustomerId() {

    try {
        Connection con = DBConnection.getConnection();

        String sql = "SELECT c.cust_id " +
                     "FROM CUSTOMERS c " +
                     "JOIN USERS u ON c.user_id = u.user_id " +
                     "WHERE u.username=?";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, username);

        ResultSet rs = pst.executeQuery();

        if(rs.next()) {
            custId = rs.getInt(1);
        }

        rs.close();
        pst.close();
        con.close();

    } catch(Exception e) {
        e.printStackTrace();
    }
}
    
    private void createContentPanel() {

    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBounds(50,230,800,250);
    add(contentPanel);
}
    
    private void createTopBar() {

    JPanel nav = new JPanel(null);
    nav.setBounds(0,0,900,60);
    nav.setBackground(new Color(0,51,153));
    add(nav);

    JLabel welcome = new JLabel("Welcome Customer",SwingConstants.CENTER);
    welcome.setForeground(Color.WHITE);
    welcome.setBounds(20,15,200,30);
    nav.add(welcome);

    JButton btnAdd = new JButton("Add Item");
    btnAdd.setBounds(450,15,100,30);
    nav.add(btnAdd);

    JButton btnEdit = new JButton("Edit Profile");
    btnEdit.setBounds(560,15,110,30);
    nav.add(btnEdit);

    JButton btnLogout = new JButton("Logout");
    btnLogout.setBounds(700,15,100,30);
    nav.add(btnLogout);

    btnAdd.addActionListener(e ->
        new AddItemForm(custId,this).setVisible(true));

    btnLogout.addActionListener(e -> {
        new LoginForm().setVisible(true);
        dispose();
    });
    
    btnEdit.addActionListener(e ->
        new EditCustomerProfile(username, this).setVisible(true));
    
}
    
    private JLabel createBox(int x) {

    JLabel box = new JLabel("", SwingConstants.CENTER);
    box.setBounds(x,20,150,90);
    box.setOpaque(true);
    box.setBackground(new Color(0,51,153));
    box.setForeground(Color.WHITE);
    box.setFont(new Font("Segoe UI", Font.BOLD, 16));
    box.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return box;
}
    
    private void createStatusBoxes() {

    JPanel boxPanel = new JPanel(null);
    boxPanel.setBounds(50,80,800,130);
    boxPanel.setBackground(new Color(173,216,230));
    add(boxPanel);

    lblPending = createBox(20);
    lblAssigned = createBox(260);
    lblCompleted = createBox(440);
    lblCancelled = createBox(620);

    boxPanel.add(lblPending);
    boxPanel.add(lblAssigned);
    boxPanel.add(lblCompleted);
    boxPanel.add(lblCancelled);

    lblPending.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            loadItems("pending");
        }
    });

    lblAssigned.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            loadItems("assigned");
        }
    });

    lblCompleted.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            loadItems("completed");
        }
    });

    lblCancelled.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            loadItems("cancelled");
        }
    });
}
    
    public void loadCounts() {

    try {
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(
            "SELECT item_status, COUNT(*) " +
            "FROM Cust_Ewaste_Items " +
            "WHERE cust_id=? GROUP BY item_status");

        pst.setInt(1, custId);
        ResultSet rs = pst.executeQuery();

        int p=0,a=0,c=0,x=0;

        while(rs.next()) {
            String status = rs.getString(1);
            int count = rs.getInt(2);

            switch(status) {
                case "pending": p=count; break;
                case "assigned": a=count; break;
                case "completed": c=count; break;
                case "cancelled": x=count; break;
            }
        }

        lblPending.setText("<html>Pending<br>"+p+"</html>");
        lblAssigned.setText("<html>Assigned<br>"+a+"</html>");
        lblCompleted.setText("<html>Completed<br>"+c+"</html>");
        lblCancelled.setText("<html>Cancelled<br>"+x+"</html>");

    } catch(Exception e){
        e.printStackTrace();
    }
}
    
    public void loadItems(String status) {

    contentPanel.removeAll();

    model = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int column) {
            for (int row = 0; row < getRowCount(); row++) {
                Object value = getValueAt(row, column);
                if (value != null) {
                    return value.getClass();
                }
            }
            return Object.class;
        }
    };

    table = new JTable(model);
    table.setRowHeight(80);

    // ✅ Custom Column Names
    model.addColumn("ID");
    model.addColumn("Item Name");
    model.addColumn("Image");
    model.addColumn("Weight (kg)");
    model.addColumn("Quantity");
    model.addColumn("Pickup From");
    model.addColumn("Pickup To");

    try {
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(
            "SELECT item_id, item_name, item_img, weight, qty, " +
            "pref_pickup_from_date, pref_pickup_to_date " +
            "FROM Cust_Ewaste_Items " +
            "WHERE cust_id=? AND item_status=?"
        );

        pst.setInt(1, custId);
        pst.setString(2, status);

        ResultSet rs = pst.executeQuery();

        while(rs.next()) {

            byte[] imgBytes = rs.getBytes("item_img");
            ImageIcon icon = null;

            if(imgBytes != null) {
                ImageIcon temp = new ImageIcon(imgBytes);
                Image scaled = temp.getImage()
                        .getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
            }

            model.addRow(new Object[]{
                rs.getInt("item_id"),
                rs.getString("item_name"),
                icon,
                rs.getDouble("weight"),
                rs.getInt("qty"),
                rs.getDate("pref_pickup_from_date"),
                rs.getDate("pref_pickup_to_date")
            });
        }

        rs.close();
        pst.close();
        con.close();

    } catch(Exception e){
        e.printStackTrace();
    }

    JScrollPane scrollPane = new JScrollPane(table);
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    // ✅ Show buttons only for pending
    if(status.equals("pending")) {

        JButton btnEdit = new JButton("Edit");
        JButton btnCancel = new JButton("Cancel");

        JPanel actionPanel = new JPanel();
        actionPanel.add(btnEdit);
        actionPanel.add(btnCancel);

        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        btnEdit.addActionListener(e -> {

            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select an item.");
                return;
            }

            int itemId = (int) model.getValueAt(row,0);
            new EditItemForm(itemId, this).setVisible(true);
        });

        btnCancel.addActionListener(e -> {

            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select an item.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel this item?\n\n"
                            + "⚠ Once cancelled, it cannot be reverted back.",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if(confirm != JOptionPane.YES_OPTION) return;

            int itemId = (int) model.getValueAt(row,0);

            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(
                        "UPDATE Cust_Ewaste_Items SET item_status='cancelled' WHERE item_id=?");

                pst.setInt(1, itemId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Item cancelled successfully.");

                loadCounts();
                loadItems(status);

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });
    }

    contentPanel.revalidate();
    contentPanel.repaint();
}
}