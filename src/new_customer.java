import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class new_customer extends JFrame implements ActionListener {
    JTextField tfName, tfAddress, tfContact;

    public new_customer() {
        setTitle("Add New Customer");
        setSize(400, 200);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Customer Name:"));
        tfName = new JTextField();
        add(tfName);

        add(new JLabel("Address:"));
        tfAddress = new JTextField();
        add(tfAddress);

        add(new JLabel("Contact:"));
        tfContact = new JTextField();
        add(tfContact);

        JButton addButton = new JButton("Add Customer");
        addButton.addActionListener(this);
        add(addButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Add Customer")) {
            String name = tfName.getText();
            String address = tfAddress.getText();
            String contact = tfContact.getText();

            // Create a new customer in the database
            addCustomerToDatabase(name, address, contact);
        }
    }

    private void addCustomerToDatabase(String name, String address, String contact) {
        // Assuming you have a conn class for database connection
        conn connection = new conn();
        try {
            Statement statement = connection.c.createStatement();

            // Create a table if not exists
            String createTableQuery = "CREATE TABLE IF NOT EXISTS customers ("
                    + "customer_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255), "
                    + "address VARCHAR(255), "
                    + "contact VARCHAR(20))";

            statement.executeUpdate(createTableQuery);

            // Insert new customer into the table
            String insertQuery = "INSERT INTO customers (name, address, contact) VALUES "
                    + "('" + name + "', '" + address + "', '" + contact + "')";

            statement.executeUpdate(insertQuery);

            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding customer to the database.");
        }
    }

    public static void main(String[] args) {
        new new_customer();
    }
}
