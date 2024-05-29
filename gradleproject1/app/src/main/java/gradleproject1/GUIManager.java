package gradleproject1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GUIManager {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private ServerSetup serverSetup;
    private JTextArea logArea;

    public GUIManager() {
        createAndShowGUI();
    }

    public void setServerSetup(ServerSetup serverSetup) {
        this.serverSetup = serverSetup;
    }

    private void createAndShowGUI() {
        frame = new JFrame("EARLY BIRD - WEB SERVICES");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);

        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("Table.header.font", new Font("Arial", Font.BOLD, 16));

        model = new DefaultTableModel();
        model.addColumn("Module");
        model.addColumn("Port(s)");
        model.addColumn("Status");

        table = new JTable(model);
        model.addRow(new Object[]{"Home Server", "8080", "Stopped"});
        JScrollPane tablescrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        logArea = new JTextArea(10, 35);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        startButton.addActionListener(e -> {
            try {
                serverSetup.setupServer();
                logArea.append("Server started on port 8080\n");
                model.setValueAt("Running", 0, 2);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error starting server: " + ex.getMessage());
            }
        });

        stopButton.addActionListener(e -> {
            try {
                serverSetup.stopServer();
                logArea.append("Server stopped\n");
                model.setValueAt("Stopped", 0, 2);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error stopping server: " + ex.getMessage());
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        frame.getContentPane().add(tablescrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(logScrollPane, BorderLayout.EAST);

        frame.setVisible(true);
    }

    public void updateLogArea(String logMessage) {
        System.out.println("Updating log area: " + logMessage);
        SwingUtilities.invokeLater(() -> logArea.append(logMessage + "\n"));
    }
}
