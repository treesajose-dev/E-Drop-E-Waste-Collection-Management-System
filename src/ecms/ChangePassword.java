package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ChangePassword extends JFrame {

    JPasswordField txtOld, txtNew;
    String username;

    public ChangePassword(String username){

        this.username = username;

        setTitle("Change Password");
        setSize(350,250);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(0,102,204));

        JLabel lbl1 = new JLabel("Old Password");
        lbl1.setBounds(50,40,120,25);
        lbl1.setForeground(Color.WHITE);
        add(lbl1);

        txtOld = new JPasswordField();
        txtOld.setBounds(170,40,120,25);
        add(txtOld);

        JLabel lbl2 = new JLabel("New Password");
        lbl2.setBounds(50,80,120,25);
        lbl2.setForeground(Color.WHITE);
        add(lbl2);

        txtNew = new JPasswordField();
        txtNew.setBounds(170,80,120,25);
        add(txtNew);

        JButton btn = new JButton("Update");
        btn.setBounds(110,130,100,30);
        add(btn);

        btn.addActionListener(e -> changePassword());
    }

    private void changePassword(){

    String oldPass = String.valueOf(txtOld.getPassword()).trim();
    String newPass = String.valueOf(txtNew.getPassword()).trim();

    // Check empty fields
    if(oldPass.isEmpty() || newPass.isEmpty()){
        JOptionPane.showMessageDialog(this,
                "All fields are required.");
        return;
    }

    // Validate new password format
    if (!newPass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#!]).{8,}$")) {
        JOptionPane.showMessageDialog(this,
                "Password must be minimum 8 characters\n"
                + "Include 1 uppercase, 1 lowercase,\n"
                + "1 number and 1 special character (@,#,!)");
        return;
    }

    try{
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
                "SELECT password FROM USERS WHERE username=?");

        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if(rs.next()){
            String dbPass = rs.getString(1);

            // Check old password match
            if(!dbPass.equals(oldPass)){
                JOptionPane.showMessageDialog(this,
                        "Old Password Incorrect");
                return;
            }

            // Prevent same password
            if(oldPass.equals(newPass)){
                JOptionPane.showMessageDialog(this,
                        "New password cannot be same as old password.");
                return;
            }
        }

        PreparedStatement update = con.prepareStatement(
                "UPDATE USERS SET password=? WHERE username=?");

        update.setString(1,newPass);
        update.setString(2,username);
        update.executeUpdate();

        JOptionPane.showMessageDialog(this,
                "Password Updated Successfully");

        dispose();
        con.close();

    }catch(Exception e){
        e.printStackTrace();
    }
}
}