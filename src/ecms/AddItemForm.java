/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Date;

public class AddItemForm extends JFrame {

    int custId;
    byte[] imageBytes = null;
    JTextField txtItemName, txtWeight, txtQty;
    JSpinner fromDateSpinner, toDateSpinner;
    JButton btnAdd;
    CustomerDashboard dashboard;

    public AddItemForm(int custId, CustomerDashboard dashboard) {

        this.custId = custId;
        this.dashboard = dashboard;

        setTitle("Add E-Waste Item");
        setSize(600,650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        getContentPane().setBackground(new Color(0,102,204));

        JPanel panel = new JPanel(null);
        panel.setBounds(80,40,440,500);
        panel.setBackground(new Color(173,216,230));
        add(panel);

        JLabel title = new JLabel("Add New Item");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(90,20,200,30);
        panel.add(title);

        int y = 80;

        panel.add(createLabel("Item Name:", y));
        txtItemName = createField(y);
        panel.add(txtItemName);
        
        y += 70;
        JButton btnUpload = new JButton("Upload Image");
        btnUpload.setBounds(40, y, 150, 30);
        panel.add(btnUpload);
        
        btnUpload.addActionListener(e -> {

    JFileChooser chooser = new JFileChooser();

    // Optional: allow only images
    FileNameExtensionFilter filter =
        new FileNameExtensionFilter("Images", "jpg", "png", "jpeg");
    chooser.setFileFilter(filter);

    int result = chooser.showOpenDialog(this);

    if(result == JFileChooser.APPROVE_OPTION) {
        try {
            File file = chooser.getSelectedFile();
            FileInputStream fis = new FileInputStream(file);

            imageBytes = fis.readAllBytes();
            fis.close();

            JOptionPane.showMessageDialog(this,
                "Image selected successfully!");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
});
        
        y += 40;
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
Date today = new Date();
fromModel.setStart(today);
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


        y += 70;
        btnAdd = new JButton("Submit");
        btnAdd.setBounds(160, y, 150, 40);
        btnAdd.setBackground(new Color(0,51,153));
        btnAdd.setForeground(Color.WHITE);
        panel.add(btnAdd);

        btnAdd.addActionListener(e -> {
            if(validateForm()) {
                insertItem();
            }
        });

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

    // ================= VALIDATION =================

    private boolean validateForm() {

        String itemName = txtItemName.getText().trim();
        String weightStr = txtWeight.getText().trim();
        String qtyStr = txtQty.getText().trim();
        Date fromDate = (Date) fromDateSpinner.getValue();
Date toDate = (Date) toDateSpinner.getValue();



        // Item Name
        if(!itemName.matches("^[A-Za-z0-9 /#()]+$")) {
    JOptionPane.showMessageDialog(this,
        "Item name can contain alphabets, numbers, spaces, /, # and ().");
    return false;
}
        
        if(imageBytes != null && imageBytes.length > 2 * 1024 * 1024) {
    JOptionPane.showMessageDialog(this,
        "Image size must be less than 2MB.");
    return false;
}

        // Weight
        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if(weight <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Weight must be positive.");
                return false;
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                "Invalid weight.");
            return false;
        }

        // Quantity
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if(qty <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Quantity must be positive integer.");
                return false;
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                "Invalid quantity.");
            return false;
        }

        //Date validation
LocalDate from = fromDate.toInstant()
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate();

LocalDate to = toDate.toInstant()
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate();

LocalDate today = LocalDate.now();

if(from.isBefore(today)) {
    JOptionPane.showMessageDialog(this,
        "Pickup From date cannot be before today.");
    return false;
}

if(to.isBefore(from)) {
    JOptionPane.showMessageDialog(this,
        "Pickup To date cannot be before From date.");
    return false;
}

        return true;
    }

    // ================= INSERT =================

    private void insertItem() {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                "INSERT INTO Cust_Ewaste_Items " +
                "(item_id, cust_id, item_name, item_img, weight, qty, " +
                "item_status, date_added, pref_pickup_from_date, pref_pickup_to_date) " +
                "VALUES (item_seq.nextval, ? ,?, ?, ?, ?, 'pending', SYSDATE, ?, ?)";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, custId);
            pst.setString(2, txtItemName.getText().trim());
            
            if(imageBytes != null)
    pst.setBytes(3, imageBytes);
else
    pst.setNull(3, Types.BLOB);
            
            pst.setDouble(4, Double.parseDouble(txtWeight.getText().trim()));
            pst.setInt(5, Integer.parseInt(txtQty.getText().trim()));
            pst.setDate(6, new java.sql.Date(
    ((Date) fromDateSpinner.getValue()).getTime()
));

pst.setDate(7, new java.sql.Date(
    ((Date) toDateSpinner.getValue()).getTime()
));

            pst.executeUpdate();
            
            dashboard.loadCounts();
            dashboard.loadItems("pending");
            dispose();

            JOptionPane.showMessageDialog(this,
                "Item Added Successfully!");

            dispose();

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
    }

    private String getUsername() {
        return ""; // optional if needed
    }
}
