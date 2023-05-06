package com.irfansaf.safpass.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.irfansaf.safpass.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationDialog  extends JDialog implements ActionListener {

    private static final Logger LOG = Logger.getLogger(PurchaseCodeDialog.class.getName());
    private JPanel fieldPanel;
    private JPanel buttonPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField userNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private LoginDialog parentDialog;

    public RegistrationDialog(LoginDialog parent) {
        super(parent, "Register", true);
        this.parentDialog = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.userNameField = TextComponentFactory.newTextField();
        this.firstNameField = TextComponentFactory.newTextField();
        this.lastNameField = TextComponentFactory.newTextField();
        this.emailField = TextComponentFactory.newTextField();

        this.passwordField = TextComponentFactory.newPasswordField(false);
        this.confirmPasswordField = TextComponentFactory.newPasswordField(false);

        this.registerButton = new JButton("Register", MessageDialog.getIcon("accept"));
        this.registerButton.setActionCommand("register_button");
        this.registerButton.setMnemonic(KeyEvent.VK_ENTER);
        this.registerButton.addActionListener(this);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.buttonPanel.add(this.registerButton);
        this.buttonPanel.add(Box.createHorizontalStrut(10));

        this.fieldPanel = new JPanel(new SpringLayout());
        this.fieldPanel.add(new JLabel("Username:"));
        this.fieldPanel.add(this.userNameField);
        this.fieldPanel.add(new JLabel("First Name:"));
        this.fieldPanel.add(this.firstNameField);
        this.fieldPanel.add(new JLabel("Last Name:"));
        this.fieldPanel.add(this.lastNameField);
        this.fieldPanel.add(new JLabel("Email:"));
        this.fieldPanel.add(this.emailField);
        this.fieldPanel.add(new JLabel("Password"));
        this.fieldPanel.add(this.passwordField);
        this.fieldPanel.add(new JLabel("Repeat Password"));
        this.fieldPanel.add(this.confirmPasswordField);
        SpringUtilities.makeCompactGrid(this.fieldPanel,
                6, 2,
                5, 5,
                5, 5);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // add some padding around the content panel
        contentPanel.add(this.fieldPanel, BorderLayout.CENTER);
        contentPanel.add(this.buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setSize(350, 300);
        setMinimumSize(new Dimension(270, 200));
        setLocationRelativeTo(parent);
        setVisible(false);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("register_button".equals(command)) {
            registerUser();
        }
    }

    public void registerUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = userNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
           MessageDialog.showWarningMessage(this, "Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            MessageDialog.showWarningMessage(this,"Password do not match");
            return;
        }

        try {
            URL url  = new URL("https://safpass.irfansaf.com/api/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();

            requestBody.put("first_name", firstName);
            requestBody.put("last_name", lastName);
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("password_confirmation", confirmPassword);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    StringBuilder response = new StringBuilder();
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    // Parse the JSON response to get the access token
                    String jsonResponse = response.toString();
                    System.out.println(jsonResponse);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                    String accessToken = jsonNode.get("access_token").asText();
                    String userId = jsonNode.get("id").asText();

                    parentDialog.setAccessToken(accessToken);
                    parentDialog.setUserId(userId);
                }

                MessageDialog.showInformationMessage(this,"User registered successfully");
                dispose();
            } else {
                MessageDialog.showWarningMessage(this,"Error registering user. Please try again.");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error registering user. Please try again.");
            MessageDialog.showErrorMessage(this,"Error registering user. Please try again");
        }
    }


}
