import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class customer_details extends JFrame {
    private JTextArea textArea;

    public customer_details() {
        setTitle("Customer Details");
        setSize(400, 300);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        displayCustomersFromDatabase();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayCustomersFromDatabase() {
        conn connection = new conn();

        try {
            Statement statement = connection.c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");

                String customerDetails = String.format(
                        "Customer ID: %d\nName: %s\nAddress: %s\nContact: %s\n\n",
                        customerId, name, address, contact);

                textArea.append(customerDetails);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching customer details from the database.");
        }
    }

    public static void main(String[] args) {
        new customer_details();
    }
}
