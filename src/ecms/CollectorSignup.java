/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class CollectorSignup extends JFrame {

    JTextField txtUsername, txtName, txtDob, txtPhone, txtEmail, txtGovNo;
    JPasswordField txtPassword;
    JButton btnSignup;

    public CollectorSignup() {

        setTitle("Collector Registration");
        setSize(550, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(0, 102, 204));
        setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(173, 216, 230));
        panel.setBounds(80, 40, 380, 440);
        add(panel);

        JLabel title = new JLabel("Collector Registration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(60, 20, 300, 30);
        panel.add(title);

        int y = 70;

        panel.add(createLabel("Username:", y));
        txtUsername = createField(y);
        panel.add(txtUsername);

        y += 40;
        panel.add(createLabel("Password:", y));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, y, 180, 25);
        panel.add(txtPassword);

        y += 40;
        panel.add(createLabel("Company Name:", y));
        txtName = createField(y);
        panel.add(txtName);

        y += 40;
        panel.add(createLabel("DOB (yyyy-mm-dd):", y));
        txtDob = createField(y);
        panel.add(txtDob);

        y += 40;
        panel.add(createLabel("Phone:                  91+", y));
        txtPhone = createField(y);
        panel.add(txtPhone);

        y += 40;
        panel.add(createLabel("Email:", y));
        txtEmail = createField(y);
        panel.add(txtEmail);

        y += 40;
        panel.add(createLabel("Gov Reg Number:", y));
        txtGovNo = createField(y);
        panel.add(txtGovNo);

        y += 50;
        btnSignup = new JButton("Register");
        btnSignup.setBackground(new Color(0, 51, 153));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setBounds(120, y, 130, 35);
        panel.add(btnSignup);

        btnSignup.addActionListener(e -> registerCollector());

        setVisible(true);
    }

    private JLabel createLabel(String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(20, y, 130, 25);
        return lbl;
    }

    private JTextField createField(int y) {
        JTextField txt = new JTextField();
        txt.setBounds(150, y, 180, 25);
        return txt;
    }

    // ================= VALIDATION =================

    private boolean validateForm() {

        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());
        String name = txtName.getText().trim();
        String dob = txtDob.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String govNo = txtGovNo.getText().trim();

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Username can contain only alphabets,numbers and underscore.");
            return false;
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#!]).{8,}$")) {
            JOptionPane.showMessageDialog(this,
                    "Password must contain 8 characters,\n1 uppercase, 1 lowercase,\n1 number & 1 special (@#!)");
            return false;
        }

        if (!name.matches("^[A-Za-z ]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Name can contain only alphabets and spaces.");
            return false;
        }

        if (!dob.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            JOptionPane.showMessageDialog(this,
                    "DOB must be yyyy-mm-dd format.");
            return false;
        }

        try {
            LocalDate birth = LocalDate.parse(dob);
            LocalDate today = LocalDate.now();

            if (!birth.isBefore(today)) {
                JOptionPane.showMessageDialog(this,
                        "DOB cannot be future date.");
                return false;
            }

            int age = Period.between(birth, today).getYears();
            if (age < 18) {
                JOptionPane.showMessageDialog(this,
                        "Collector must be at least 18 years old.");
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Date.");
            return false;
        }

        if (!phone.matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(this,
                    "Phone must be exactly 10 digits.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Email.");
            return false;
        }

        if (!govNo.matches("^\\d+$")) {
            JOptionPane.showMessageDialog(this,
                    "Gov Registration Number must be numeric.");
            return false;
        }

        return true;
    }

    // ================= USERNAME CHECK =================

    private boolean isUsernameUnique(String username) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT 1 FROM USERS WHERE USERNAME=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            return !rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= GOV REG NO CHECK =================

    private boolean isGovNoUnique(String govNo) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT 1 FROM COLLECTORS WHERE GOV_REGISTRATION_NO=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setLong(1, Long.parseLong(govNo));
            ResultSet rs = pst.executeQuery();
            return !rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= REGISTER =================

    private void registerCollector() {

    if (!validateForm()) return;

    String username = txtUsername.getText().trim();
    if (!isUsernameUnique(username)) {
        JOptionPane.showMessageDialog(this, "Username already exists.");
        return;
    }

    String govNo = txtGovNo.getText().trim();
    if (!isGovNoUnique(govNo)) {
        JOptionPane.showMessageDialog(this,
                "Gov Registration Number already exists.");
        return;
    }

    Connection con = null;

    try {
        con = DBConnection.getConnection();

        // 🔥 Disable auto commit
        con.setAutoCommit(false);

        // 1️⃣ Insert into USERS
        String userSql =
                "INSERT INTO USERS VALUES " +
                "(user_seq.nextval, ?, ?, 'collector', 'active')";

        PreparedStatement pst1 = con.prepareStatement(userSql);
        pst1.setString(1, username);
        pst1.setString(2,
                String.valueOf(txtPassword.getPassword()));
        pst1.executeUpdate();

        // 2️⃣ Get user_id
        String getId =
                "SELECT user_id FROM USERS WHERE username=?";
        PreparedStatement pst2 = con.prepareStatement(getId);
        pst2.setString(1, username);

        ResultSet rs = pst2.executeQuery();

        int userId = 0;
        if (rs.next())
            userId = rs.getInt("user_id");

        // 3️⃣ Insert into COLLECTORS
        String sql =
                "INSERT INTO COLLECTORS VALUES " +
                "(collector_seq.nextval, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pst3 = con.prepareStatement(sql);

        pst3.setInt(1, userId);
        pst3.setString(2, txtName.getText().trim());
        pst3.setDate(3,
                java.sql.Date.valueOf(txtDob.getText().trim()));
        pst3.setLong(4,
                Long.parseLong(txtPhone.getText().trim()));
        pst3.setString(5, txtEmail.getText().trim());
        pst3.setLong(6, Long.parseLong(govNo));

        pst3.executeUpdate();

        // ✅ If everything succeeds
        con.commit();

        JOptionPane.showMessageDialog(this,
                "Collector Registered Successfully!");

        new LoginForm().setVisible(true);
        dispose();

    } catch (Exception e) {

        try {
            if (con != null)
                con.rollback();   // 🔥 rollback everything
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
