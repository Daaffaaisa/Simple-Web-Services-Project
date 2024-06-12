package gradleproject1;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GUIManager {

    private JFrame frame;
    private JTextField portField;
    private JTextField directoryField;
    private JTextField logDirField;
    private JTextArea logArea;
    private ServerSetup serverSetup;
    private Properties properties;
    private static final String CONFIG_FILE = "config.properties";

    public GUIManager() {
        properties = new Properties();
        loadProperties();
        createAndShowGUI();
    }

    public void setServerSetup(ServerSetup serverSetup) {
        this.serverSetup = serverSetup;
    }

    private void createAndShowGUI() {
        frame = new JFrame("EARLY BIRD - WEB SERVICES");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Port configuration
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(properties.getProperty("port", "8080"), 10);
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.add(portLabel);
        portPanel.add(portField);

        // Directory chooser for files
        JLabel directoryLabel = new JLabel("File Directory:");
        directoryField = new JTextField(properties.getProperty("directory", ""), 20);
        JButton directoryButton = new JButton("Browse...");
        directoryButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
                properties.setProperty("directory", chooser.getSelectedFile().getAbsolutePath());
                //saveProperties();
            }
        });
        JPanel directoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        directoryPanel.add(directoryLabel);
        directoryPanel.add(directoryField);
        directoryPanel.add(directoryButton);

        // Directory chooser for logs
        JLabel logDirLabel = new JLabel("Log Directory:");
        logDirField = new JTextField(properties.getProperty("logDirectory", ""), 20);
        JButton logDirButton = new JButton("Browse...");
        logDirButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                logDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                properties.setProperty("logDirectory", chooser.getSelectedFile().getAbsolutePath());
                //saveProperties();
            }
        });
        JPanel logDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logDirPanel.add(logDirLabel);
        logDirPanel.add(logDirField);
        logDirPanel.add(logDirButton);

        // Log area
        logArea = new JTextArea(10, 35);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        // Start and stop buttons
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                String directory = directoryField.getText().trim();
                String logDirectory = logDirField.getText().trim();
                
                serverSetup = new ServerSetup(port, directory, this);
                serverSetup.setLogDirectory(logDirectory); // Set log directory before starting the server
                serverSetup.setupServer();
                logArea.append("Server started on port " + port + "\n");
                properties.setProperty("port", String.valueOf(port));
                properties.setProperty("directory", directory);
                properties.setProperty("logDirectory", logDirectory);
                saveProperties();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error starting server: " + ex.getMessage());
            }
        });

        stopButton.addActionListener(e -> {
            try {
                if (serverSetup != null) {
                    serverSetup.stopServer();
                    logArea.append("Server stopped\n");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error stopping server: " + ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        // Add components to frame
        frame.getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(portPanel);
        topPanel.add(directoryPanel);
        topPanel.add(logDirPanel);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(logScrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void loadProperties() {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveProperties() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLogArea(String logMessage) {
        SwingUtilities.invokeLater(() -> logArea.append(logMessage + "\n"));
    }
}