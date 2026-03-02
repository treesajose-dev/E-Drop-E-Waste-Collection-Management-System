package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditCollectorProfile extends JFrame {

    JTextField txtName, txtPhone, txtEmail, txtGovNo;

    String username;
    int userId;
    CollectorDashboard dashboard;

    public EditCollectorProfile(String username, CollectorDashboard dashboard) {

        this.username = username;
        this.dashboard = dashboard;

        setTitle("Edit Collector Profile");
        setSize(450,420);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(0,102,204));

        fetchUserId();
        createUI();
        loadCollectorData();
    }

    private void fetchUserId() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    "SELECT user_id FROM USERS WHERE username=?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if(rs.next())
                userId = rs.getInt(1);

            con.close();
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void createUI() {

        JLabel heading = new JLabel("Edit Collector Profile");
        heading.setBounds(100,20,300,30);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(Color.WHITE);
        add(heading);

        JPanel panel = new JPanel(null);
        panel.setBounds(50,70,340,200);
        panel.setBackground(new Color(173,216,230));
        add(panel);

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtGovNo = new JTextField();

        addField(panel,"Name",txtName,20);
        addField(panel,"Phone",txtPhone,60);
        addField(panel,"Email",txtEmail,100);
        addField(panel,"Gov Reg No",txtGovNo,140);

        JButton btnUpdate = new JButton("Update Profile");
        btnUpdate.setBounds(70,300,130,35);
        add(btnUpdate);

        JButton btnChangePass = new JButton("Change Password");
        btnChangePass.setBounds(210,300,150,35);
        add(btnChangePass);

        btnUpdate.addActionListener(e -> updateProfile());
        btnChangePass.addActionListener(e ->
                new ChangePassword(username).setVisible(true));
    }

    private void addField(JPanel panel,String label,JTextField field,int y){
        JLabel lbl = new JLabel(label);
        lbl.setBounds(20,y,100,25);
        panel.add(lbl);

        field.setBounds(130,y,180,25);
        panel.add(field);
    }

    private void loadCollectorData() {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT name, phno, email, gov_registration_no " +
                    "FROM Collectors WHERE user_id=?");

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                txtName.setText(rs.getString("name"));
                txtPhone.setText(String.valueOf(rs.getLong("phno")));
                txtEmail.setText(rs.getString("email"));
                txtGovNo.setText(String.valueOf(rs.getLong("gov_registration_no")));
            }

            con.close();

        } catch(Exception e){ e.printStackTrace(); }
    }
    
    private boolean validateForm() {

    String name = txtName.getText().trim();
    String phone = txtPhone.getText().trim();
    String email = txtEmail.getText().trim();
    String govNo = txtGovNo.getText().trim();

    if (!name.matches("^[A-Za-z ]+$")) {
        JOptionPane.showMessageDialog(this,
                "Name can contain only alphabets and spaces.");
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

    if (!govNo.matches("^\\d{1,20}$")) {
        JOptionPane.showMessageDialog(this,
                "Gov Registration Number must be numeric (max 20 digits).");
        return false;
    }

    return true;
}
    
    private void updateProfile() {

    if(!validateForm()) return;

    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement pst = con.prepareStatement(
                "UPDATE Collectors SET name=?, phno=?, email=?, gov_registration_no=? " +
                "WHERE user_id=?");

        pst.setString(1, txtName.getText().trim());
        pst.setLong(2, Long.parseLong(txtPhone.getText().trim()));
        pst.setString(3, txtEmail.getText().trim());
        pst.setLong(4, Long.parseLong(txtGovNo.getText().trim()));
        pst.setInt(5, userId);

        pst.executeUpdate();

        JOptionPane.showMessageDialog(this,
                "Profile Updated Successfully");

        dashboard.loadCounts();  // refresh counts
        con.close();
        dispose();

    } catch(Exception e){
        e.printStackTrace();
    }
}
}
