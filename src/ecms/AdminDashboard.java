package ecms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    JLabel lblUsers, lblCustomers, lblCollectors, lblItems, lblPickups;
    JPanel contentPanel;
    JTable table;
    DefaultTableModel model;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(0,102,204));

        // ===== TOP NAV =====
        JPanel nav = new JPanel(null);
        nav.setBackground(new Color(0,51,153));
        nav.setBounds(0,0,900,60);
        add(nav);

        JLabel welcome = new JLabel("Welcome Admin");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcome.setBounds(20,15,200,30);
        nav.add(welcome);

        JButton logout = new JButton("Logout");
        logout.setBounds(780,15,90,30);
        nav.add(logout);

        logout.addActionListener(e -> {
            new LandingPage().setVisible(true);
            dispose();
        });

        // ===== COUNT BOX PANEL =====
        JPanel boxPanel = new JPanel(new GridLayout(1,5,20,0));
boxPanel.setBackground(new Color(173,216,230));
boxPanel.setBounds(20,80,860,150);
add(boxPanel);

        lblUsers = createBox("Users");
lblCustomers = createBox("Customers");
lblCollectors = createBox("Collectors");
lblItems = createBox("Items");
lblPickups = createBox("Pickups");

        boxPanel.add(lblUsers);
        boxPanel.add(lblCustomers);
        boxPanel.add(lblCollectors);
        boxPanel.add(lblItems);
        boxPanel.add(lblPickups);

        loadCounts();

        // Click Actions
        lblUsers.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadTable("USERS");
            }
        });

        lblCustomers.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadTable("CUSTOMERS");
            }
        });

        lblCollectors.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadTable("COLLECTORS");
            }
        });
        
        lblItems.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        loadItemsSection();
    }
});

lblPickups.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        loadPickupSection();
    }
});

        // ===== CONTENT PANEL =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(50,250,800,230);
        add(contentPanel);
        
        setVisible(true);
    }

    // ===== CREATE BOX =====
    private JLabel createBox(String title) {

    JLabel box = new JLabel("", SwingConstants.CENTER);
    box.setOpaque(true);
    box.setBackground(new Color(0,51,153));
    box.setForeground(Color.WHITE);
    box.setFont(new Font("Segoe UI", Font.BOLD, 16));
    box.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return box;
}

    // ===== LOAD COUNTS =====
    private void loadCounts() {

    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        //Active Users
        ResultSet r1 = st.executeQuery(
            "SELECT COUNT(*) FROM USERS WHERE status='active'");
        if(r1.next())
            lblUsers.setText("<html>Users<br>" + r1.getInt(1) + "</html>");

        //Active Customers (based on USERS status)
        ResultSet r2 = st.executeQuery(
            "SELECT COUNT(*) " +
            "FROM USERS u " +
            "JOIN CUSTOMERS c ON u.user_id = c.user_id " +
            "WHERE u.status='active'");
        if(r2.next())
            lblCustomers.setText("<html>Customers<br>" + r2.getInt(1) + "</html>");

        //Active Collectors (based on USERS status)
        ResultSet r3 = st.executeQuery(
            "SELECT COUNT(*) " +
            "FROM USERS u " +
            "JOIN COLLECTORS col ON u.user_id = col.user_id " +
            "WHERE u.status='active'");
        if(r3.next())
            lblCollectors.setText("<html>Collectors<br>" + r3.getInt(1) + "</html>");
        
        // Total Items
ResultSet r4 = st.executeQuery(
    "SELECT COUNT(*) FROM CUST_EWASTE_ITEMS");
if(r4.next())
    lblItems.setText("<html>Items<br>" + r4.getInt(1) + "</html>");

// Total Pickups
ResultSet r5 = st.executeQuery(
    "SELECT COUNT(*) FROM PICKUP");
if(r5.next())
    lblPickups.setText("<html>Pickups<br>" + r5.getInt(1) + "</html>");

        st.close();
        con.close();

    } catch(Exception e) {
        e.printStackTrace();
    }
}

    // ===== LOAD TABLE INSIDE SAME PAGE =====
    private void loadTable(String tableName) {

    contentPanel.removeAll();

    model = new DefaultTableModel();
    table = new JTable(model);

    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        ResultSet rs;

        // IMPORTANT CHANGE HERE
        if(tableName.equals("CUSTOMERS")) {

            rs = st.executeQuery(
                "SELECT c.cust_id, c.user_id, c.name, c.cust_dob, " +
                "c.locality, c.pincode, c.address_link, c.phno, c.email, " +
                "u.status " +
                "FROM CUSTOMERS c " +
                "JOIN USERS u ON c.user_id = u.user_id");

        }
        else if(tableName.equals("COLLECTORS")) {

            rs = st.executeQuery(
                "SELECT col.collector_id, col.user_id, col.name, " +
                "col.collector_dob, col.phno, col.email, " +
                "col.gov_registration_no, u.status " +
                "FROM COLLECTORS col " +
                "JOIN USERS u ON col.user_id = u.user_id");

        }
        else {  // USERS

            rs = st.executeQuery("SELECT * FROM USERS");
        }

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        for(int i=1;i<=colCount;i++)
            model.addColumn(meta.getColumnName(i));

        while(rs.next()){
            Object[] row = new Object[colCount];
            for(int i=1;i<=colCount;i++)
                row[i-1] = rs.getObject(i);
            model.addRow(row);
        }

        st.close();
        con.close();

    } catch(Exception e){
        e.printStackTrace();
    }

    JScrollPane scroll = new JScrollPane(table);

    JButton toggleBtn = new JButton("Activate / Deactivate");

    toggleBtn.addActionListener(e -> {

        if(!tableName.equals("USERS")) {
            JOptionPane.showMessageDialog(this,
                "Activate/Deactivate allowed only in USERS table");
            return;
        }

        toggleStatus();
    });

    JPanel bottom = new JPanel();
    bottom.add(toggleBtn);

    contentPanel.add(scroll, BorderLayout.CENTER);
    contentPanel.add(bottom, BorderLayout.SOUTH);

    contentPanel.revalidate();
    contentPanel.repaint();
}

    // ===== ACTIVATE / DEACTIVATE =====
    private void toggleStatus() {

    int row = table.getSelectedRow();
    if(row == -1) {
        JOptionPane.showMessageDialog(this,
            "Please select a user");
        return;
    }

    try {
        Connection con = DBConnection.getConnection();

        Object val = model.getValueAt(row,0); // user_id column
        int userId = ((Number) val).intValue();

        PreparedStatement pst1 =
            con.prepareStatement(
                "SELECT status FROM USERS WHERE user_id=?");
        pst1.setInt(1,userId);

        ResultSet rs = pst1.executeQuery();

        if(rs.next()) {

            String current = rs.getString(1);
            String newStatus =
                current.equals("active") ? "inactive" : "active";

            PreparedStatement pst2 =
                con.prepareStatement(
                    "UPDATE USERS SET status=? WHERE user_id=?");

            pst2.setString(1,newStatus);
            pst2.setInt(2,userId);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "User status updated to " + newStatus);

            loadCounts();
            loadTable("USERS");
        }

    } catch(Exception e){
        e.printStackTrace();
    }
}
    
    private void loadItemsSection() {

    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    String[] statusOptions = {
        "pending","assigned","completed","cancelled"
    };
    JComboBox<String> statusBox =
        new JComboBox<>(statusOptions);

    JButton filterBtn = new JButton("View");

    topPanel.add(new JLabel("Select Status: "));
    topPanel.add(statusBox);
    topPanel.add(filterBtn);

    model = new DefaultTableModel();
    table = new JTable(model);

    JScrollPane scroll = new JScrollPane(table);

    filterBtn.addActionListener(e -> {

        String status =
            statusBox.getSelectedItem().toString();

        loadItemsByStatus(status);
    });

    contentPanel.add(topPanel, BorderLayout.NORTH);
    contentPanel.add(scroll, BorderLayout.CENTER);

    contentPanel.revalidate();
    contentPanel.repaint();
}
    
    private void loadItemsByStatus(String status) {

    model.setRowCount(0);
    model.setColumnCount(0);

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
            "SELECT cu.name, cu.locality, " +
            "i.item_name, i.qty " +
            "FROM CUST_EWASTE_ITEMS i " +
            "JOIN CUSTOMERS cu ON i.cust_id = cu.cust_id " +
            "WHERE i.item_status = ?");

        pst.setString(1,status);

        ResultSet rs = pst.executeQuery();

        model.addColumn("Customer Name");
        model.addColumn("Locality");
        model.addColumn("Item Name");
        model.addColumn("Qty");

        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4)
            });
        }

        con.close();

    } catch(Exception ex) {
        ex.printStackTrace();
    }
}
    
    private void loadPickupSection() {

    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    String[] options = {
        "out for pickup",
        "pickup completed",
        "rejected"
    };

    JComboBox<String> box =
        new JComboBox<>(options);

    JButton viewBtn = new JButton("View");

    topPanel.add(new JLabel("Select Status: "));
    topPanel.add(box);
    topPanel.add(viewBtn);

    model = new DefaultTableModel();
    table = new JTable(model);

    JScrollPane scroll = new JScrollPane(table);

    viewBtn.addActionListener(e -> {

        String status =
            box.getSelectedItem().toString();

        if(status.equals("rejected"))
            loadRejected();
        else
            loadPickupByStatus(status);
    });

    contentPanel.add(topPanel, BorderLayout.NORTH);
    contentPanel.add(scroll, BorderLayout.CENTER);

    contentPanel.revalidate();
    contentPanel.repaint();
}
    
    private void loadPickupByStatus(String status) {

    model.setRowCount(0);
    model.setColumnCount(0);

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst =
            con.prepareStatement(
            "SELECT cu.name, cu.locality, " +
            "i.item_name, i.qty, p.status " +
            "FROM PICKUP p " +
            "JOIN CUST_EWASTE_ITEMS i ON p.item_id=i.item_id " +
            "JOIN CUSTOMERS cu ON i.cust_id=cu.cust_id " +
            "WHERE p.status=?");

        pst.setString(1,status);

        ResultSet rs = pst.executeQuery();

        model.addColumn("Customer Name");
        model.addColumn("Locality");
        model.addColumn("Item Name");
        model.addColumn("Qty");
        model.addColumn("Pickup Status");

        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4),
                rs.getString(5)
            });
        }

        con.close();

    } catch(Exception e){
        e.printStackTrace();
    }
}
    
    private void loadRejected() {

    model.setRowCount(0);
    model.setColumnCount(0);

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst =
            con.prepareStatement(
            "SELECT cu.name, cu.locality, " +
            "i.item_name, i.qty, r.rejected_on " +
            "FROM COLLECTOR_REJECTIONS r " +
            "JOIN CUST_EWASTE_ITEMS i ON r.item_id=i.item_id " +
            "JOIN CUSTOMERS cu ON i.cust_id=cu.cust_id");

        ResultSet rs = pst.executeQuery();

        model.addColumn("Customer Name");
        model.addColumn("Locality");
        model.addColumn("Item Name");
        model.addColumn("Qty");
        model.addColumn("Rejected On");

        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4),
                rs.getDate(5)
            });
        }

        con.close();

    } catch(Exception e){
        e.printStackTrace();
    }
}
}