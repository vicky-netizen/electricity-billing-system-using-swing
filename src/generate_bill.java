import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class conn {
    Connection c;
    Statement s;

    public conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/vicky", "root", "vicky310105");
            s = c.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

abstract class Appliance {
    protected String name;
    protected double powerConsumption;
    protected double dailyUsageHours;
    protected int noOfDevices;

    public Appliance(String name, double powerConsumption, double dailyUsageHours, int noOfDevices) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.dailyUsageHours = dailyUsageHours;
        this.noOfDevices = noOfDevices;
    }

    public abstract String generateSchedule(double ratio);
}

class SingleDeviceAppliance extends Appliance {
    public SingleDeviceAppliance(String name, double powerConsumption, double dailyUsageHours, int noOfDevices) {
        super(name, powerConsumption, dailyUsageHours, noOfDevices);
    }

    @Override
    public String generateSchedule(double ratio) {
        StringBuilder schedule = new StringBuilder(name + " - Single Device\n");
        schedule.append(generateRandomTime(dailyUsageHours * ratio * 60));
        return schedule.toString();
    }

    private String generateRandomTime(double minutes) {
        int randomMinutes = (int) (Math.random() * 24 * 60);
        int endMinutes = randomMinutes + (int) minutes;
        return "Start Time: " + formatTime(randomMinutes) + "\nEnd Time: " + formatTime(endMinutes) + "\n";
    }

    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}

class MultiDeviceAppliance extends Appliance {
    public MultiDeviceAppliance(String name, double powerConsumption, double dailyUsageHours, int noOfDevices) {
        super(name, powerConsumption, dailyUsageHours, noOfDevices);
    }

    @Override
    public String generateSchedule(double ratio) {
        StringBuilder schedule = new StringBuilder();
        for (int i = 1; i <= noOfDevices; i++) {
            schedule.append(name).append(" - Device ").append(i).append("\n");
            schedule.append(generateRandomTime(dailyUsageHours * ratio * 60));
        }
        return schedule.toString();
    }

    private String generateRandomTime(double minutes) {
        int randomMinutes = (int) (Math.random() * 24 * 60);
        int endMinutes = randomMinutes + (int) minutes;
        return "Start Time: " + formatTime(randomMinutes) + "\nEnd Time: " + formatTime(endMinutes) + "\n";
    }

    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}

class ElectricityCalculator {
    private List<Appliance> appliances = new ArrayList<>();
    private conn databaseConnection = new conn();

    public void inputAppliances() {
        while (true) {
            String applianceName = JOptionPane.showInputDialog("Enter the name of the appliance (or 'done' to finish):");
            if (applianceName.equalsIgnoreCase("done")) {
                break;
            }

            double powerConsumption = Double.parseDouble(JOptionPane.showInputDialog(
                    "Enter the power consumption of " + applianceName + " (in watts):"));
            int noOfDevices = Integer.parseInt(JOptionPane.showInputDialog(
                    "How many devices of " + applianceName + " do you use per day?"));
            double dailyUsageHours = Double.parseDouble(JOptionPane.showInputDialog(
                    "On average, how many hours do you generally use " + applianceName + " per day?"));

            Appliance appliance;
            if (noOfDevices > 1) {
                appliance = new MultiDeviceAppliance(applianceName, powerConsumption, dailyUsageHours, noOfDevices);
            } else {
                appliance = new SingleDeviceAppliance(applianceName, powerConsumption, dailyUsageHours, noOfDevices);
            }

            appliances.add(appliance);

            // Insert appliance details into the 'appliances' table
            insertApplianceIntoDatabase(applianceName, powerConsumption, dailyUsageHours, noOfDevices);
        }
    }

    private void insertApplianceIntoDatabase(String name, double powerConsumption, double dailyUsageHours, int noOfDevices) {
        try {
            String insertQuery = String.format("INSERT INTO appliances (name, power_consumption, daily_usage_hours, no_of_devices) " +
                    "VALUES ('%s', %.2f, %.2f, %d)", name, powerConsumption, dailyUsageHours, noOfDevices);

            databaseConnection.s.executeUpdate(insertQuery);
        } catch (Exception e) {
            System.out.println("Error inserting appliance into database: " + e.getMessage());
        }
    }
    public double calculateElectricityCost() {
        double totalPower = appliances.stream()
                .mapToDouble(appliance ->
                        appliance.powerConsumption * appliance.noOfDevices * appliance.dailyUsageHours * 30 / 1000)
                .sum();

        return calculateElectricityBill(totalPower);
    }

    private double calculateElectricityBill(double unitsConsumed) {
        double fixedCharges = 100;
        double variableChargesRate1 = 5;
        double variableChargesRate2 = 7;
        double taxRate = 5;

        double variableCharges = (unitsConsumed <= 100) ?
                variableChargesRate1 * unitsConsumed :
                (variableChargesRate1 * 100) + (variableChargesRate2 * (unitsConsumed - 100));

        double totalBill = fixedCharges + variableCharges;
        double totalBillWithTax = totalBill + (totalBill * (taxRate / 100));

        return totalBillWithTax;
    }

    public String generateApplianceSchedules(double ratio) {
        StringBuilder schedules = new StringBuilder();
        for (Appliance appliance : appliances) {
            schedules.append(appliance.generateSchedule(ratio));
        }
        return schedules.toString();
    }

    public double calculateSmartSwitchCost(String applianceSchedules) {
        int totalAppliances = calculateTotalAppliances(applianceSchedules);
        int smartSwitchCost = 300;
        int deliveryCost = 10;
        double taxRate = 5;

        double totalCost = (smartSwitchCost * totalAppliances) + deliveryCost;
        double totalCostWithTax = totalCost + (totalCost * (taxRate / 100));

        return totalCostWithTax;
    }

    private int calculateTotalAppliances(String applianceSchedules) {
        int count = 0;
        String[] lines = applianceSchedules.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                count++;
            }
        }
        return count/3;
    }
}


public class generate_bill extends JFrame {
    private JTextField budgetField;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JButton confirmOrderButton;

    private conn databaseConnection = new conn();
    private ElectricityCalculator calculator;

    public generate_bill() {
        setTitle("Electricity Billing System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        calculator = new ElectricityCalculator();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel budgetLabel = new JLabel("Enter budget:");
        budgetField = new JTextField();
        calculateButton = new JButton("Calculate");
        confirmOrderButton = new JButton("Confirm Order");

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateButtonClicked();
            }
        });

        confirmOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmOrderButtonClicked();
            }
        });

        panel.add(budgetLabel);
        panel.add(budgetField);
        panel.add(new JLabel());
        panel.add(calculateButton);
        panel.add(new JLabel());
        panel.add(confirmOrderButton);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Disable the Confirm Order button initially
        confirmOrderButton.setEnabled(false);

        setVisible(true);
    }

    private void calculateButtonClicked() {
        try {
            double budget = Double.parseDouble(budgetField.getText());

            // Run the calculation asynchronously
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    calculator.inputAppliances();

                    double monthlyCost = calculator.calculateElectricityCost();
                    appendToResultArea(String.format("Estimated monthly electricity cost (including tax): %.2f%n", monthlyCost));

                    double ratio = budget / monthlyCost;
                    if (ratio > 1) {
                        appendToResultArea("It's under budget\n");
                    } else {
                        String applianceSchedules = calculator.generateApplianceSchedules(ratio);
                        appendToResultArea("Appliance schedules:\n");
                        appendToResultArea(applianceSchedules);

                        double smartSwitchCost = calculator.calculateSmartSwitchCost(applianceSchedules);
                        appendToResultArea(String.format("Smart Switch Cost (including tax): %.2f%n", smartSwitchCost));

                        // Enable the Confirm Order button
                        confirmOrderButton.setEnabled(true);
                    }
                    return null;
                }
            };

            worker.execute();
        } catch (NumberFormatException ex) {
            appendToResultArea("Invalid budget input. Please enter a valid number.");
        }
    }

    private void confirmOrderButtonClicked() {
        // Run the confirmation asynchronously
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Get the order details (replace this with actual order details)
                    String orderDetails = resultArea.getText();

                    // Insert order details into the database
                    databaseConnection.s.executeUpdate("INSERT INTO orders (order_details) VALUES ('" + orderDetails + "')");

                    appendToResultArea("Order Confirmed. Details added to the database.\n");
                } catch (Exception ex) {
                    appendToResultArea("Error confirming order: " + ex.getMessage() + "\n");
                }
                return null;
            }
        };

        worker.execute();
    }

    private void appendToResultArea(String text) {
        SwingUtilities.invokeLater(() -> resultArea.append(text));
    }

    public static void main(String[] args) {
        new generate_bill();
    }
}
