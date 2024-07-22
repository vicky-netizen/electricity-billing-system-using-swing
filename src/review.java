import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class review extends JFrame implements ActionListener {
    private JTextArea reviewArea;
    private JButton submitButton;

    public review() {
        super("Customer Reviews");

        setSize(800, 500);
        setLocation(450, 200);
        setLayout(new BorderLayout());

        reviewArea = new JTextArea();
        reviewArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(reviewArea);

        submitButton = new JButton("Submit Review");
        submitButton.addActionListener(this);

        add(scrollPane, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        fetchReviews();

        setVisible(true);
    }

    private void fetchReviews() {
        conn databaseConnection = new conn();
        try {
            String query = "SELECT * FROM reviews";
            ResultSet resultSet = databaseConnection.s.executeQuery(query);

            while (resultSet.next()) {
                String reviewText = resultSet.getString("review_text");
                reviewArea.append(reviewText + "\n\n");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching reviews: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String newReview = reviewArea.getText();
            saveReviewToDatabase(newReview);
            reviewArea.setText(""); // Clear the text area after submitting
            fetchReviews(); // Refresh the displayed reviews
        }
    }

    private void saveReviewToDatabase(String reviewText) {
        conn databaseConnection = new conn();
        try {
            String insertQuery = "INSERT INTO reviews (review_text) VALUES ('" + reviewText + "')";
            databaseConnection.s.executeUpdate(insertQuery);
            JOptionPane.showMessageDialog(null, "Review submitted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error submitting review: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new review();
    }
}
