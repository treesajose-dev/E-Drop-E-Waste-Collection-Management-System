/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author 2tree
 */
public class CustomerSignup extends JFrame {
    JTextField txtUsername, txtName, txtDob, txtLocality, txtPincode,
            txtAddress, txtPhone, txtEmail;
    JPasswordField txtPassword;
    JButton btnSignup;

    public CustomerSignup() {

        setTitle("Customer Registration");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Blue Background
        getContentPane().setBackground(new Color(0, 102, 204));

        // Main Panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230));
        panel.setBounds(100, 50, 380, 520);
        panel.setLayout(null);

        setLayout(null);
        add(panel);

        JLabel title = new JLabel("Customer Registration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(70, 20, 300, 30);
        panel.add(title);

        int y = 70;

        panel.add(createLabel("Username:", y));
        txtUsername = createTextField(y);
        panel.add(txtUsername);

        y += 40;
        panel.add(createLabel("Password:", y));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, y, 180, 25);
        panel.add(txtPassword);

        y += 40;
        panel.add(createLabel("Name:", y));
        txtName = createTextField(y);
        panel.add(txtName);

        y += 40;
        panel.add(createLabel("DOB (yyyy-mm-dd):", y));
        txtDob = createTextField(y);
        panel.add(txtDob);

        y += 40;
        panel.add(createLabel("Locality:", y));
        txtLocality = createTextField(y);
        panel.add(txtLocality);

        y += 40;
        panel.add(createLabel("Pincode:", y));
        txtPincode = createTextField(y);
        panel.add(txtPincode);

        y += 40;
        panel.add(createLabel("Address Link:", y));
        txtAddress = createTextField(y);
        panel.add(txtAddress);

        y += 40;
        panel.add(createLabel("Phone:                  91+", y));
        txtPhone = createTextField(y);
        panel.add(txtPhone);

        y += 40;
        panel.add(createLabel("Email:", y));
        txtEmail = createTextField(y);
        panel.add(txtEmail);

        y += 50;
        btnSignup = new JButton("Register");
        btnSignup.setBackground(new Color(0, 51, 153));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setBounds(130, y, 120, 35);
        panel.add(btnSignup);

        btnSignup.addActionListener(e -> registerCustomer());

        setVisible(true);
    }

    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(20, y, 130, 25);
        return label;
    }

    private JTextField createTextField(int y) {
        JTextField field = new JTextField();
        field.setBounds(150, y, 180, 25);
        return field;
    }

    //Validations check
    private boolean validateForm() {

    String username = txtUsername.getText().trim();
    String password = String.valueOf(txtPassword.getPassword());
    String name = txtName.getText().trim();
    String dob = txtDob.getText().trim();
    String locality = txtLocality.getText().trim();
    String pincode = txtPincode.getText().trim();
    String address = txtAddress.getText().trim();
    String phone = txtPhone.getText().trim();
    String email = txtEmail.getText().trim();

    // Username: alphabets and underscore only
    if (!username.matches("^[A-Za-z0-9_]+$")) {
        JOptionPane.showMessageDialog(this,
                "Username can contain only alphabets,numbers and underscore.");
        return false;
    }

    // Password validation
    if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#!]).{8,}$")) {
        JOptionPane.showMessageDialog(this,
                "Password must be minimum 8 characters\n"
                + "Include 1 uppercase, 1 lowercase,\n"
                + "1 number and 1 special character (@,#,!)");
        return false;
    }

    // Name: alphabets and spaces only
    if (!name.matches("^[A-Za-z ]+$")) {
        JOptionPane.showMessageDialog(this,
                "Name can contain only alphabets and spaces.");
        return false;
    }

    // DOB validation
    if (!dob.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
        JOptionPane.showMessageDialog(this,
            "DOB must be in yyyy-mm-dd format.");
        return false;
    }

    try {
        LocalDate birthDate = LocalDate.parse(dob);
        LocalDate today = LocalDate.now();

        if (!birthDate.isBefore(today)) {
            JOptionPane.showMessageDialog(this,
                "DOB cannot be today or future date.");
            return false;
        }

        int age = Period.between(birthDate, today).getYears();

        if (age < 18) {
            JOptionPane.showMessageDialog(this,
                "Customer must be at least 18 years old.");
            return false;
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Invalid Date Entered.");
        return false;
    }

    // Locality: alphabets, numbers and spaces
    if (!locality.matches("^[A-Za-z0-9 ]+$")) {
        JOptionPane.showMessageDialog(this,
                "Locality can contain alphabets, numbers and spaces only.");
        return false;
    }

    // Pincode: exactly 6 digits
    if (!pincode.matches("^\\d{6}$")) {
        JOptionPane.showMessageDialog(this,
                "Pincode must be exactly 6 digits.");
        return false;
    }

    // Address Link validation (Google Maps or app.goo.gl short link)
    if (!(address.contains("google.com/maps")
      || address.contains("goo.gl/maps")
      || address.contains("maps.app.goo.gl"))) {

    JOptionPane.showMessageDialog(this,
            "Please enter a valid Google Maps link.");
    return false;
    }


    // Phone validation: exactly 10 digits
    if (!phone.matches("^\\d{10}$")) {
        JOptionPane.showMessageDialog(this,
            "Phone number must be exactly 10 digits.");
        return false;
    }

    // Email validation
    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        JOptionPane.showMessageDialog(this,
                "Enter valid email address.");
        return false;
    }

    return true;
}
    
    // Username uniquness check
    private boolean isUsernameUnique(String username) {

    try {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM Users WHERE username=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. Try another.");
            return false;
        }

    } catch (Exception e) {
    e.printStackTrace();   // shows error in console
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    return false;
}

    return true;
}
    
    private void registerCustomer() {

    if (!validateForm()) return;

    String username = txtUsername.getText().trim();

    if (!isUsernameUnique(username)) return;

    String password = String.valueOf(txtPassword.getPassword());
    String name = txtName.getText().trim();
    String dob = txtDob.getText().trim();
    String locality = txtLocality.getText().trim();
    int pincode = Integer.parseInt(txtPincode.getText().trim());
    String address = txtAddress.getText().trim();
    long phone = Long.parseLong(txtPhone.getText().trim());
    String email = txtEmail.getText().trim();

    Connection con = null;

    try {
        con = DBConnection.getConnection();

        //  Disable auto commit
        con.setAutoCommit(false);

        //Insert into Users
        String userSql = "INSERT INTO Users VALUES (user_seq.nextval, ?, ?, 'customer', 'active')";
        PreparedStatement pst1 = con.prepareStatement(userSql);
        pst1.setString(1, username);
        pst1.setString(2, password);
        pst1.executeUpdate();

        //Get user_id
        String getId = "SELECT user_id FROM Users WHERE username=?";
        PreparedStatement pst2 = con.prepareStatement(getId);
        pst2.setString(1, username);
        ResultSet rs = pst2.executeQuery();

        int userId = 0;
        if (rs.next()) {
            userId = rs.getInt("user_id");
        }

        //Insert into Customers
        String custSql = "INSERT INTO Customers VALUES (cust_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst3 = con.prepareStatement(custSql);

        pst3.setInt(1, userId);
        pst3.setString(2, name);
        pst3.setDate(3, java.sql.Date.valueOf(dob));
        pst3.setString(4, locality);
        pst3.setInt(5, pincode);
        pst3.setString(6, address);
        pst3.setLong(7, phone);
        pst3.setString(8, email);

        pst3.executeUpdate();

        //If both succeed
        con.commit();

        JOptionPane.showMessageDialog(this,
                "Customer Registered Successfully!");

        new LoginForm().setVisible(true);
        dispose();

    } catch (Exception e) {

        try {
            if (con != null)
                con.rollback();   // rollback everything
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(this,
                "Registration failed: " + e.getMessage());

    } finally {
        try {
            if (con != null)
                con.setAutoCommit(true);  // restore default
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
    
}
