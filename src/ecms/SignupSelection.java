/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecms;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author 2tree
 */
public class SignupSelection extends JFrame{
    public SignupSelection() {

        setTitle("Signup As");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(new Color(0, 102, 204));
        setLayout(null);

        JLabel label = new JLabel("Signup As");
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(Color.WHITE);
        label.setBounds(180, 40, 200, 40);
        add(label);

        JButton btnCustomer = new JButton("Customer");
        btnCustomer.setBounds(150, 120, 200, 40);
        btnCustomer.setBackground(new Color(0, 51, 153));
        btnCustomer.setForeground(Color.WHITE);
        add(btnCustomer);

        JButton btnCollector = new JButton("Collector");
        btnCollector.setBounds(150, 170, 200, 40);
        btnCollector.setBackground(new Color(0, 51, 153));
        btnCollector.setForeground(Color.WHITE);
        add(btnCollector);

        btnCustomer.addActionListener(e -> {
            new CustomerSignup();
            dispose();
        });

        btnCollector.addActionListener(e -> {
            new CollectorSignup();
            dispose();
        });

        setVisible(true);
    }
}
