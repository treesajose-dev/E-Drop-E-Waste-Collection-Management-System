/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author 2tree
 */
public class LandingPage extends JFrame {

    public LandingPage() {

        setTitle("E-Drop: E-Waste Collection Management System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Remove default layout
        setLayout(null);

        ImageIcon icon = new ImageIcon(getClass().getResource("/ecms/images/bg.jpg"));

// Get original image
Image img = icon.getImage();

// Scale image to frame size
Image scaledImg = img.getScaledInstance(600, 400, Image.SCALE_SMOOTH);

// Set scaled image
JLabel background = new JLabel(new ImageIcon(scaledImg));
background.setBounds(0, 0, 600, 400);

setContentPane(background);
background.setLayout(null);

        // Title Label
        JLabel title = new JLabel("E-Drop: E-Waste Collection Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(70, 70, 500, 40);
        background.add(title);

        // Login Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(200, 160, 180, 40);
        btnLogin.setBackground(new Color(0, 51, 153));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        background.add(btnLogin);

        // Signup Button
        JButton btnSignup = new JButton("Signup");
        btnSignup.setBounds(200, 220, 180, 40);
        btnSignup.setBackground(new Color(0, 51, 153));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        background.add(btnSignup);

        btnLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        btnSignup.addActionListener(e -> {
            new SignupSelection().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LandingPage();
    }
}
