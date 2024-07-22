import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LastBill extends JFrame implements ActionListener {
    private JTextArea orderDetailsArea;

    public LastBill () {
        super("View Orders");

        setSize(800, 500);
        setLocation(450, 200);
        setLayout(new BorderLayout());

        orderDetailsArea = new JTextArea();
        orderDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderDetailsArea);

        add(scrollPane, BorderLayout.CENTER);

        fetchOrderDetails();

        setVisible(true);
    }

    private void fetchOrderDetails() {
        conn databaseConnection = new conn();
        try {
            String query = "SELECT * FROM orders order by order_id desc limit 1";
            ResultSet resultSet = databaseConnection.s.executeQuery(query);

            while (resultSet.next()) {
                String orderDetails = resultSet.getString("order_details");
                orderDetailsArea.append(orderDetails + "\n\n");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching order details: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new LastBill ();
    }
}
