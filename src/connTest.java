import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class connTest {
    Connection c;
    Statement s;

    public connTest() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/vicky", "root", "vicky310105");
            if (c != null) {
                System.out.println("Connected to the database successfully!");

                s = c.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS Year (\n"
                        + "    YearID INT AUTO_INCREMENT PRIMARY KEY,\n"
                        + "    YearValue INT,\n"
                        + "    StartDate DATE,\n"
                        + "    EndDate DATE\n"
                        + ");";
                s.executeUpdate(sql);
                System.out.println("Table 'Year' created successfully!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new connTest();
    }
}
