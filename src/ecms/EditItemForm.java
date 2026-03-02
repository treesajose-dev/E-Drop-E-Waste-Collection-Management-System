package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;

public class EditItemForm extends JFrame {

    int itemId;
    CustomerDashboard dashboard;

    JTextField txtItemName, txtWeight, txtQty;
    JSpinner fromDateSpinner, toDateSpinner;
    JButton btnUpdate;

    public EditItemForm(int itemId, CustomerDashboard dashboard) {

        this.itemId = itemId;
        this.dashboard = dashboard;

        setTitle("Edit Item");
        setSize(600,600);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        getContentPane().setBackground(new Color(0,102,204));

        JPanel panel = new JPanel(null);
        panel.setBounds(80,40,440,480);
        panel.setBackground(new Color(173,216,230));
        add(panel);

        JLabel title = new JLabel("Edit Item");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(150,20,200,30);
        panel.add(title);

        int y = 80;

        panel.add(createLabel("Item Name:", y));
        txtItemName = createField(y);
        panel.add(txtItemName);

        y += 60;

        panel.add(createLabel("Weight (kg):", y));
        txtWeight = createField(y);
        panel.add(txtWeight);

        y += 60;

        panel.add(createLabel("Quantity:", y));
        txtQty = createField(y);
        panel.add(txtQty);

        y += 60;

        // Pickup From
        panel.add(createLabel("Pickup From:", y));

        SpinnerDateModel fromModel = new SpinnerDateModel();
        fromDateSpinner = new JSpinner(fromModel);
        fromDateSpinner.setBounds(40, y + 20, 350, 30);
        fromDateSpinner.setEditor(
            new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd")
        );
        panel.add(fromDateSpinner);

        y += 70;

        // Pickup To
        panel.add(createLabel("Pickup To:", y));

        SpinnerDateModel toModel = new SpinnerDateModel();
        toDateSpinner = new JSpinner(toModel);
        toDateSpinner.setBounds(40, y + 20, 350, 30);
        toDateSpinner.setEditor(
            new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd")
        );
        panel.add(toDateSpinner);

        y += 80;

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, y, 150, 40);
        btnUpdate.setBackground(new Color(0,51,153));
        btnUpdate.setForeground(Color.WHITE);
        panel.add(btnUpdate);

        btnUpdate.addActionListener(e -> {
            if(validateForm()) {
                updateItem();
            }
        });

        fetchItemData();  // 🔥 PREFILL DATA

        setVisible(true);
    }

    private JLabel createLabel(String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(30,y,250,20);
        return lbl;
    }

    private JTextField createField(int y) {
        JTextField txt = new JTextField();
        txt.setBounds(40, y + 20, 350, 30);
        return txt;
    }

    // ================= PREFILL =================

    private void fetchItemData() {

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                "SELECT item_name, weight, qty, pref_pickup_from_date, pref_pickup_to_date " +
                "FROM Cust_Ewaste_Items WHERE item_id=?");

            pst.setInt(1, itemId);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                txtItemName.setText(rs.getString(1));
                txtWeight.setText(rs.getString(2));
                txtQty.setText(rs.getString(3));

                fromDateSpinner.setValue(rs.getDate(4));
                toDateSpinner.setValue(rs.getDate(5));
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // ================= VALIDATION =================

    private boolean validateForm() {

        String itemName = txtItemName.getText().trim();
        String weightStr = txtWeight.getText().trim();
        String qtyStr = txtQty.getText().trim();

        if(!itemName.matches("^[A-Za-z0-9 /#()]+$")) {
            JOptionPane.showMessageDialog(this,
                "Invalid Item Name.");
            return false;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            if(weight <= 0) throw new Exception();
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,
                "Invalid Weight.");
            return false;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if(qty <= 0) throw new Exception();
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,
                "Invalid Quantity.");
            return false;
        }

        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();

        LocalDate from = fromDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

        LocalDate to = toDate.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

        if(to.isBefore(from)) {
            JOptionPane.showMessageDialog(this,
                "Pickup To date cannot be before From date.");
            return false;
        }

        return true;
    }

    // ================= UPDATE =================

    private void updateItem() {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                "UPDATE Cust_Ewaste_Items SET " +
                "item_name=?, weight=?, qty=?, " +
                "pref_pickup_from_date=?, pref_pickup_to_date=? " +
                "WHERE item_id=?");

            pst.setString(1, txtItemName.getText().trim());
            pst.setDouble(2, Double.parseDouble(txtWeight.getText().trim()));
            pst.setInt(3, Integer.parseInt(txtQty.getText().trim()));
            pst.setDate(4, new java.sql.Date(
                ((Date) fromDateSpinner.getValue()).getTime()));
            pst.setDate(5, new java.sql.Date(
                ((Date) toDateSpinner.getValue()).getTime()));
            pst.setInt(6, itemId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Item Updated Successfully!");

            dashboard.loadCounts();
            dashboard.loadItems("pending");

            dispose();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}