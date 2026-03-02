package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Desktop;
import java.net.URI;
import javax.swing.table.DefaultTableCellRenderer;

public class CollectorDashboard extends JFrame {

    String username;
    int collectorId;

    JLabel lblAvailable, lblAssigned, lblCompleted, lblRejected;
    JPanel contentPanel;
    JTable table;
    DefaultTableModel model;

    public CollectorDashboard(String username) {

        this.username = username;
        fetchCollectorId();

        setTitle("Collector Dashboard");
        setSize(1100,900);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(0,102,204));

        createTopBar();
        createBoxes();
        createContentPanel();

        loadCounts();
        loadAvailableItems();

        setVisible(true);
    }

    // ================= FETCH COLLECTOR ID =================
    private void fetchCollectorId() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT col.collector_id FROM Collectors col " +
                    "JOIN Users u ON col.user_id=u.user_id " +
                    "WHERE u.username=?");

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if(rs.next())
                collectorId = rs.getInt(1);

        } catch(Exception e){ e.printStackTrace(); }
    }

    // ================= TOP BAR =================
    private void createTopBar() {

        JPanel nav = new JPanel(null);
        nav.setBounds(0,0,1100,60);
        nav.setBackground(new Color(0,51,153));
        add(nav);
        
        JLabel welcome = new JLabel("Welcome Collector",SwingConstants.CENTER);
    welcome.setForeground(Color.WHITE);
    welcome.setBounds(20,15,200,30);
    nav.add(welcome);

        JButton btnEdit = new JButton("Edit Profile");
        btnEdit.setBounds(750,15,120,30);
        nav.add(btnEdit);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(900,15,120,30);
        nav.add(btnLogout);

        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        
        btnEdit.addActionListener(e ->
        new EditCollectorProfile(username, this).setVisible(true));
    }

    // ================= COUNT BOX =================
    private JLabel createBox(int x) {
        JLabel box = new JLabel("",SwingConstants.CENTER);
        box.setBounds(x,20,200,100);
        box.setOpaque(true);
        box.setBackground(new Color(0,51,153));
        box.setForeground(Color.WHITE);
        box.setFont(new Font("Segoe UI",Font.BOLD,16));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return box;
    }

    private void createBoxes() {

        JPanel boxPanel = new JPanel(null);
        boxPanel.setBounds(50,80,1000,150);
        boxPanel.setBackground(new Color(173,216,230));
        add(boxPanel);

        lblAvailable = createBox(50);
        lblAssigned = createBox(300);
        lblCompleted = createBox(550);
        lblRejected = createBox(800);

        boxPanel.add(lblAvailable);
        boxPanel.add(lblAssigned);
        boxPanel.add(lblCompleted);
        boxPanel.add(lblRejected);

        lblAvailable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loadAvailableItems();
            }
        });

        lblAssigned.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loadAssignedItems();
            }
        });

        lblCompleted.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loadCompletedItems();
            }
        });

        lblRejected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loadRejectedItems();
            }
        });
    }

    // ================= CONTENT PANEL =================
    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(50,290,1000,420);
        add(contentPanel);
    }

    // ================= LOAD COUNTS =================
    public void loadCounts() {

        try {
            Connection con = DBConnection.getConnection();

            // Available
            PreparedStatement p1 = con.prepareStatement(
                    "SELECT COUNT(*) FROM Cust_Ewaste_Items i " +
                    "WHERE item_status='pending' AND NOT EXISTS " +
                    "(SELECT 1 FROM Collector_Rejections r " +
                    "WHERE r.item_id=i.item_id AND r.collector_id=?)");

            p1.setInt(1, collectorId);
            ResultSet r1 = p1.executeQuery();
            if(r1.next())
                lblAvailable.setText("<html>Available<br>"+r1.getInt(1)+"</html>");

            // Assigned
            PreparedStatement p2 = con.prepareStatement(
                    "SELECT COUNT(*) FROM Pickup " +
                    "WHERE collector_id=? AND status='out for pickup'");
            p2.setInt(1, collectorId);
            ResultSet r2 = p2.executeQuery();
            if(r2.next())
                lblAssigned.setText("<html>Assigned<br>"+r2.getInt(1)+"</html>");

            // Completed
            PreparedStatement p3 = con.prepareStatement(
                    "SELECT COUNT(*) FROM Pickup " +
                    "WHERE collector_id=? AND status='pickup completed'");
            p3.setInt(1, collectorId);
            ResultSet r3 = p3.executeQuery();
            if(r3.next())
                lblCompleted.setText("<html>Completed<br>"+r3.getInt(1)+"</html>");

            // Rejected
            PreparedStatement p4 = con.prepareStatement(
                    "SELECT COUNT(*) FROM Collector_Rejections " +
                    "WHERE collector_id=?");
            p4.setInt(1, collectorId);
            ResultSet r4 = p4.executeQuery();
            if(r4.next())
                lblRejected.setText("<html>Rejected<br>"+r4.getInt(1)+"</html>");

        } catch(Exception e){ e.printStackTrace(); }
    }

    // ================= AVAILABLE ITEMS =================
    private void loadAvailableItems() {

        contentPanel.removeAll();
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(70);

        model.addColumn("ID");
        model.addColumn("Item Name");
        model.addColumn("Qty");
        model.addColumn("Pickup From");
        model.addColumn("Pickup To");
        model.addColumn("Customer Name");
        model.addColumn("Locality");
        model.addColumn("Phone");
        model.addColumn("Address");

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT i.item_id,i.item_name,i.qty," +
                    "i.pref_pickup_from_date,i.pref_pickup_to_date," +
                    "c.name,c.locality,c.phno,c.address_link " +
                    "FROM Cust_Ewaste_Items i " +
                    "JOIN Customers c ON i.cust_id=c.cust_id " +
                    "WHERE i.item_status='pending' AND NOT EXISTS " +
                    "(SELECT 1 FROM Collector_Rejections r " +
                    "WHERE r.item_id=i.item_id AND r.collector_id=?)");

            pst.setInt(1, collectorId);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getDate(4),
                        rs.getDate(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getLong(8),
                        rs.getString(9)
                });
            }

        } catch(Exception e){ e.printStackTrace(); }

        JScrollPane scroll = new JScrollPane(table);
        contentPanel.add(scroll,BorderLayout.CENTER);
        
        table.getColumn("Address").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = new JLabel(value == null ? "" : value.toString());

        label.setForeground(Color.BLUE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setText("<html><u>" + value + "</u></html>");
        return label;
    }
});
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

        if (e.getClickCount() == 1) {   // ✅ only single click

            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());

            int addressCol = table.getColumnModel()
                                  .getColumnIndex("Address");

            if (col == addressCol) {

                Object value = table.getValueAt(row, col);

                if (value != null) {
                    String url = value.toString();

                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid link");
                    }
                }
            }
        }
    }
});

        JButton btnAccept = new JButton("Accept");
        JButton btnReject = new JButton("Reject");

        JPanel panel = new JPanel();
        panel.add(btnAccept);
        panel.add(btnReject);

        contentPanel.add(panel,BorderLayout.SOUTH);

        // ACCEPT
        btnAccept.addActionListener(e -> acceptItem());

        // REJECT
        btnReject.addActionListener(e -> rejectItem());

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= ACCEPT =================
    private void acceptItem() {

    int row = table.getSelectedRow();
    if (row == -1) return;

    int itemId = (int) model.getValueAt(row, 0);

    Connection con = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);   // 🔥 Start transaction

        // Insert into Pickup
        PreparedStatement pst1 = con.prepareStatement(
                "INSERT INTO Pickup VALUES " +
                "(pickup_seq.nextval, ?, ?, 'out for pickup', SYSDATE, SYSDATE)");
        pst1.setInt(1, itemId);
        pst1.setInt(2, collectorId);
        pst1.executeUpdate();

        // Update Item Status
        PreparedStatement pst2 = con.prepareStatement(
                "UPDATE Cust_Ewaste_Items SET item_status='assigned' WHERE item_id=?");
        pst2.setInt(1, itemId);
        pst2.executeUpdate();

        con.commit();  // ✅ commit both

        JOptionPane.showMessageDialog(this, "Item Accepted");

        loadCounts();
        loadAssignedItems();

    } catch (Exception e) {

        try {
            if (con != null) con.rollback();   // 🔥 rollback on failure
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.printStackTrace();

    } finally {
        try {
            if (con != null) con.setAutoCommit(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

    // ================= REJECT =================
    private void rejectItem() {

        int row = table.getSelectedRow();
        if(row==-1) return;

        int itemId = (int) model.getValueAt(row,0);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO Collector_Rejections VALUES " +
                    "(reject_seq.nextval, ?, ?, SYSDATE)");
            pst.setInt(1,itemId);
            pst.setInt(2,collectorId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,"Item Rejected");

            loadCounts();
            loadAvailableItems();

        } catch(Exception e){ e.printStackTrace(); }
    }

    // ================= ASSIGNED =================
    private void loadAssignedItems() {

    contentPanel.removeAll();
    model = new DefaultTableModel();
    table = new JTable(model);
    table.setRowHeight(60);

    model.addColumn("Item ID");
    model.addColumn("Item Name");
    model.addColumn("Qty");
    model.addColumn("Pickup From");
    model.addColumn("Pickup To");
    model.addColumn("Customer Name");
    model.addColumn("Locality");
    model.addColumn("Phone");
    model.addColumn("Address");

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
                "SELECT i.item_id, i.item_name, i.qty, " +
                "i.pref_pickup_from_date, i.pref_pickup_to_date, " +
                "c.name, c.locality, c.phno, c.address_link " +
                "FROM Cust_Ewaste_Items i " +
                "JOIN Pickup p ON i.item_id = p.item_id " +
                "JOIN Customers c ON i.cust_id = c.cust_id " +
                "WHERE p.collector_id=? AND p.status='out for pickup'");

        pst.setInt(1, collectorId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getDate(4),
                    rs.getDate(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getLong(8),
                    rs.getString(9)
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    JScrollPane scroll = new JScrollPane(table);
    contentPanel.add(scroll, BorderLayout.CENTER);
    table.getColumn("Address").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = new JLabel(value == null ? "" : value.toString());

        label.setForeground(Color.BLUE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setText("<html><u>" + value + "</u></html>");
        return label;
    }
});
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

        if (e.getClickCount() == 1) {   // ✅ only single click

            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());

            int addressCol = table.getColumnModel()
                                  .getColumnIndex("Address");

            if (col == addressCol) {

                Object value = table.getValueAt(row, col);

                if (value != null) {
                    String url = value.toString();

                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid link");
                    }
                }
            }
        }
    }
});

    JButton btnComplete = new JButton("Mark as Collected");
    JPanel panel = new JPanel();
    panel.add(btnComplete);
    contentPanel.add(panel, BorderLayout.SOUTH);

    btnComplete.addActionListener(e -> markCollected());

    contentPanel.revalidate();
    contentPanel.repaint();
}
    //Mark collected
    private void markCollected() {

    int row = table.getSelectedRow();
    if (row == -1) return;

    int itemId = (int) model.getValueAt(row, 0);

    Connection con = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        // Update Pickup
        PreparedStatement pst1 = con.prepareStatement(
                "UPDATE Pickup SET status='pickup completed' WHERE item_id=?");
        pst1.setInt(1, itemId);
        pst1.executeUpdate();

        // Update Item
        PreparedStatement pst2 = con.prepareStatement(
                "UPDATE Cust_Ewaste_Items SET item_status='completed' WHERE item_id=?");
        pst2.setInt(1, itemId);
        pst2.executeUpdate();

        con.commit();

        JOptionPane.showMessageDialog(this,
                "Pickup Completed");

        loadCounts();
        loadCompletedItems();

    } catch (Exception e) {

        try {
            if (con != null) con.rollback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.printStackTrace();

    } finally {
        try {
            if (con != null) con.setAutoCommit(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

    // ================= COMPLETED =================
    private void loadCompletedItems() {

    contentPanel.removeAll();
    model = new DefaultTableModel();
    table = new JTable(model);

    model.addColumn("Item ID");
    model.addColumn("Item Name");
    model.addColumn("Qty");
    model.addColumn("Locality");

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
                "SELECT i.item_id, i.item_name, i.qty, c.locality " +
        "FROM Cust_Ewaste_Items i " +
        "JOIN Pickup p ON i.item_id = p.item_id " +
        "JOIN Customers c ON i.cust_id = c.cust_id " +
        "WHERE p.collector_id=? AND p.status='pickup completed'");

        pst.setInt(1, collectorId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
    model.addRow(new Object[]{
            rs.getInt(1),
            rs.getString(2),
            rs.getInt(3),
            rs.getString(4)
    });
}

    } catch (Exception e) {
        e.printStackTrace();
    }

    contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}
    
    // ================= REJECTED =================
   private void loadRejectedItems() {

    contentPanel.removeAll();
    model = new DefaultTableModel();
    table = new JTable(model);

    model.addColumn("Item ID");
    model.addColumn("Item Name");
    model.addColumn("Locality");

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
                "SELECT i.item_id, i.item_name, c.locality " +
                "FROM Cust_Ewaste_Items i " +
                "JOIN Collector_Rejections r ON i.item_id=r.item_id " +
                "JOIN Customers c ON i.cust_id = c.cust_id " +
                "WHERE r.collector_id=?");

        pst.setInt(1, collectorId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3)
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}
}
