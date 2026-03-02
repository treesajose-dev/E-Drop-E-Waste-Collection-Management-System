/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JComboBox<String> cmbUserType;
    JButton btnLogin, btnSignup;

    public LoginForm() {

        setTitle("E-Drop Login");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(0, 102, 204));
        setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(173, 216, 230));
        panel.setBounds(80, 60, 330, 260);
        add(panel);

        JLabel title = new JLabel("E-Waste Collection Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(40, 20, 260, 30);
        panel.add(title);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 70, 100, 25);
        panel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 70, 150, 25);
        panel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(30, 105, 100, 25);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 105, 150, 25);
        panel.add(txtPassword);

        JLabel lblType = new JLabel("User Type:");
        lblType.setBounds(30, 140, 100, 25);
        panel.add(lblType);

        cmbUserType = new JComboBox<>(new String[]{"Customer", "Collector", "Admin"});
        cmbUserType.setBounds(130, 140, 150, 25);
        panel.add(cmbUserType);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 51, 153));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(100, 180, 120, 35);
        panel.add(btnLogin);

        btnSignup = new JButton("New User? Signup");
        btnSignup.setBounds(90, 220, 150, 25);
        btnSignup.setBorderPainted(false);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setForeground(Color.BLUE);
        panel.add(btnSignup);

        btnLogin.addActionListener(e -> loginUser());
        btnSignup.addActionListener(e -> {
            new SignupSelection().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    // ================= LOGIN LOGIC =================

    private void loginUser() {

        String username = txtUsername.getText().trim();
        String password = String.valueOf(txtPassword.getPassword());
        String userType = cmbUserType.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and Password cannot be empty.");
            return;
        }

        // ===== ADMIN LOGIN (Hardcoded) =====
        if (userType.equals("Admin")) {

            if (username.equals("admin") && password.equals("admin123@")) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                new AdminDashboard().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials.");
            }
            return;
        }

        // ===== CUSTOMER & COLLECTOR LOGIN =====

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT role FROM USERS WHERE username=? AND password=? AND status='active'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                String role = rs.getString("role");

                if (userType.equalsIgnoreCase(role)) {

                    JOptionPane.showMessageDialog(this, "Login Successful!");

                    if (role.equals("customer")) {
                        new CustomerDashboard(username).setVisible(true);
                    } else if (role.equals("collector")) {
                        new CollectorDashboard(username).setVisible(true);
                    }

                    dispose();

                } else {
                    JOptionPane.showMessageDialog(this,
                            "User type mismatch!");
                }

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage());
        }
    }
}