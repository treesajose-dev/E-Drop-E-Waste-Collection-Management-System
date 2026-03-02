package ecms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class EditCustomerProfile extends JFrame {

    JTextField txtName, txtLocality, txtPincode, txtAddress, txtPhone;

    String username;
    int userId;
    CustomerDashboard dashboard; 

    public EditCustomerProfile(String username, CustomerDashboard dashboard) {

        this.username = username;
        this.dashboard = dashboard;

        setTitle("Edit Profile");
        setSize(450,450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(0,102,204));

        fetchUserId();
        createUI();
        loadCustomerData();
    }

    private void fetchUserId() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                    "SELECT user_id FROM USERS WHERE username=?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                userId = rs.getInt(1);
            }

            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createUI() {

        JLabel heading = new JLabel("Edit Customer Profile");
        heading.setBounds(100,20,300,30);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(Color.WHITE);
        add(heading);

        JPanel panel = new JPanel(null);
        panel.setBounds(50,70,340,250);
        panel.setBackground(new Color(173,216,230));
        add(panel);

        txtName = new JTextField();
        txtLocality = new JTextField();
        txtPincode = new JTextField();
        txtAddress = new JTextField();
        txtPhone = new JTextField();

        addField(panel,"Name",txtName,20);
        addField(panel,"Locality",txtLocality,60);
        addField(panel,"Pincode",txtPincode,100);
        addField(panel,"Address Link",txtAddress,140);
        addField(panel,"Phone",txtPhone,180);

        JButton btnUpdate = new JButton("Update Profile");
        btnUpdate.setBounds(80,340,130,35);
        add(btnUpdate);

        JButton btnChangePass = new JButton("Change Password");
        btnChangePass.setBounds(220,340,150,35);
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

    private void loadCustomerData() {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT name, locality, pincode, address_link, phno " +
                    "FROM CUSTOMERS WHERE user_id=?");

            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                txtName.setText(rs.getString("name"));
                txtLocality.setText(rs.getString("locality"));
                txtPincode.setText(String.valueOf(rs.getInt("pincode")));
                txtAddress.setText(rs.getString("address_link"));
                txtPhone.setText(String.valueOf(rs.getLong("phno")));
            }

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {

    String name = txtName.getText().trim();
    String locality = txtLocality.getText().trim();
    String pincode = txtPincode.getText().trim();
    String address = txtAddress.getText().trim();
    String phone = txtPhone.getText().trim();

    // Name
    if (!name.matches("^[A-Za-z ]+$")) {
        JOptionPane.showMessageDialog(this,
                "Name can contain only alphabets and spaces.");
        return false;
    }

    // Locality
    if (!locality.matches("^[A-Za-z0-9 ]+$")) {
        JOptionPane.showMessageDialog(this,
                "Locality can contain alphabets, numbers and spaces only.");
        return false;
    }

    // Pincode
    if (!pincode.matches("^\\d{6}$")) {
        JOptionPane.showMessageDialog(this,
                "Pincode must be exactly 6 digits.");
        return false;
    }

    // Google Maps link
    if (!(address.contains("google.com/maps")
      || address.contains("goo.gl/maps")
      || address.contains("maps.app.goo.gl"))) {

        JOptionPane.showMessageDialog(this,
                "Please enter a valid Google Maps link.");
        return false;
    }

    // Phone
    if (!phone.matches("^\\d{10}$")) {
        JOptionPane.showMessageDialog(this,
            "Phone number must be exactly 10 digits.");
        return false;
    }
    return true;
}
    
    private void updateProfile() {
        if (!validateForm())
            return;
        
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE CUSTOMERS SET name=?, locality=?, pincode=?, address_link=?, phno=? " +
                    "WHERE user_id=?");

            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtLocality.getText().trim());
            pst.setInt(3, Integer.parseInt(txtPincode.getText().trim()));
            pst.setString(4, txtAddress.getText().trim());
            pst.setLong(5, Long.parseLong(txtPhone.getText().trim()));
            pst.setInt(6, userId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Profile Updated Successfully");
            
            dashboard.loadCounts();

            con.close();
            dispose();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}