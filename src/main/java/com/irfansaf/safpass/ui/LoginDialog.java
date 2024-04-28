package com.irfansaf.safpass.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irfansaf.safpass.Safpass;
import com.irfansaf.safpass.model.User;
import com.irfansaf.safpass.util.Configuration;
import com.irfansaf.safpass.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.prefs.Preferences;

public class LoginDialog extends JDialog implements ActionListener {
    private SafPassFrame parentFrame;
    private String accessToken;
    private String userId;
    private JPanel buttonPanel;
    private JPanel fieldPanel;
    private JTextField emailOrUsernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginDialog(SafPassFrame parent) {
        super(parent, "Login", true);
        this.parentFrame = parent;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.emailOrUsernameField = TextComponentFactory.newTextField();
        this.emailOrUsernameField.setPreferredSize(new Dimension(200, 25));
        this.passwordField = TextComponentFactory.newPasswordField(true);
        this.passwordField.setPreferredSize(new Dimension(200, 25));
        this.loginButton = new JButton("Login", MessageDialog.getIcon("accept"));
        this.loginButton.setActionCommand("login_button");
        this.loginButton.setMnemonic(KeyEvent.VK_L);
        this.loginButton.addActionListener(this);
        this.registerButton = new JButton("Register", MessageDialog.getIcon("keyring"));
        this.registerButton.setActionCommand("register_button");
        this.registerButton.setMnemonic(KeyEvent.VK_R);
        this.registerButton.addActionListener(this);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.buttonPanel.add(this.loginButton);
        this.buttonPanel.add(Box.createHorizontalStrut(10));
        this.buttonPanel.add(this.registerButton);

        this.fieldPanel = new JPanel(new SpringLayout());
        this.fieldPanel.add(new JLabel("Email/Username"));
        this.fieldPanel.add(this.emailOrUsernameField);
        this.fieldPanel.add(new JLabel("Password"));
        this.fieldPanel.add(this.passwordField);
        this.fieldPanel.add(new JLabel(""));
        this.fieldPanel.add(this.buttonPanel);
        SpringUtilities.makeCompactGrid(this.fieldPanel,
                3, 2,
                5, 5,
                5, 5);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // add some padding around the content panel
        contentPanel.add(this.fieldPanel, BorderLayout.CENTER);
        contentPanel.add(this.buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setSize(350, 200);
        setMinimumSize(new Dimension(270, 200));
        setLocationRelativeTo(parent);
        setVisible(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("login_button".equals(command)) {
            authenticateUser();
        } else if ("register_button".equals(command)) {
            showRegistrationDialog();
        } else {
            dispose();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void authenticateUser() {
        String emailOrUsername = emailOrUsernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            MessageDialog.showWarningMessage(this,"Please enter your email/username and password");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {

            File file = new File("src/main/java/com/irfansaf/safpass/data/users.json");
            System.out.println("Absolute path: " + file.getAbsolutePath());

            if (!file.exists()) {
                MessageDialog.showErrorMessage(this, "User data file not found. Please register first.");
                return;
            }
            List<User> users = objectMapper.readValue(file, new TypeReference<List<User>>() {});

            // Check if user credentials match
            Optional<User> matchedUser = users.stream()
                    .filter(user -> user.getUsername().equals(emailOrUsername) && user.getPassword().equals(password))
                    .findFirst();

            if (matchedUser.isPresent()) {
                MessageDialog.showInformationMessage(this, "Login Success");
                User user = matchedUser.get();
                parentFrame.setUsername(user.getUsername());
                parentFrame.setUserId(user.getEmail());

                dispose();
                // Proceed with the rest of the application
            } else {
                MessageDialog.showWarningMessage(this,"Invalid email/username or password. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.showErrorMessage(this, "Error authenticating user. Please try again.");
        }

        /**
        try {
            HttpURLConnection connection = getHttpURLConnection(emailOrUsername, password);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    StringBuilder response = new StringBuilder();
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // Parse JSON response to get the access token
                    String jsonResponse = response.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                    String accessToken = jsonNode.get("access_token").asText();

                    parentFrame.setAccessToken(accessToken);

                    if (jsonNode.has("user_id")) {
                        String userId = jsonNode.get("user_id").asText();
                        parentFrame.setUserId(userId);
                    } else {
                        System.out.println("User ID not found in response");
                    }

                    MessageDialog.showInformationMessage(this, "Login Success");
                    dispose();

                    // Purchase Code Dialog
                    SwingUtilities.invokeLater(() -> {
                        while (!isPurchaseCodeValid()) {
                            PurchaseCodeDialog purchaseCodeDialog=  new PurchaseCodeDialog(userId);
                            purchaseCodeDialog.setModal(true);
                            purchaseCodeDialog.setVisible(true);
                        }
                    });
                }
        } else {
            MessageDialog.showWarningMessage(this,"Invalid email/username or password. Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.showErrorMessage(this, "Error authenticating user. Please try again.");
        }
        */
    }

    private static HttpURLConnection getHttpURLConnection(String emailOrUsername, String password) throws IOException {
        URL url = new URL("http://safpass.irfansaf.com/api/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        String jsonInputString = String.format("{\"login\": \"%s\", \"password\": \"%s\"}", emailOrUsername, password);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    public static boolean isPurchaseCodeValid() {
        return Safpass.isPurchaseCodeValid();
    }

    private void showRegistrationDialog() {
        RegistrationDialog registrationDialog = new RegistrationDialog(LoginDialog.this);
        registrationDialog.setVisible(true);
    }
}
