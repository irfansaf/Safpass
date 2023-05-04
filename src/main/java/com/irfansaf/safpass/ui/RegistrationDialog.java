package com.irfansaf.safpass.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatPasswordField;
import com.formdev.flatlaf.extras.components.FlatTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationDialog  extends JDialog {

    private static final Logger LOG = Logger.getLogger(PurchaseCodeDialog.class.getName());
    private FlatTextField firstNameField;
    private FlatTextField lastNameField;
    private FlatTextField userNameField;
    private FlatTextField emailField;
    private FlatPasswordField passwordField;
    private FlatPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private JFrame parent;

    public RegistrationDialog(LoginDialog parent) {
        super(parent, "Register", true);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        firstNameField = new FlatTextField();
        lastNameField = new FlatTextField();
        userNameField = new FlatTextField();
        emailField = new FlatTextField();
        passwordField = new FlatPasswordField();
        confirmPasswordField = new FlatPasswordField();

        messageLabel = new JLabel();

        FlatButton registerButton = new FlatButton();
        registerButton.setText("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void setupLayout() {
        setLayout(new GridLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2,2);

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        add(lastNameField, gbc);

        // User Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(userNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // Message Label
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(messageLabel, gbc);

        // Register Button
        FlatButton registerButton = new FlatButton();
        registerButton.setText("Register");
        registerButton.addActionListener(e -> registerUser());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(registerButton, gbc);

        pack();
        setLocationRelativeTo(null);
    }

    public void registerUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = userNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Password do not match");
            return;
        }

        try {
            URL url  = new URL("http://127.0.0.1:8000/api/register");
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
//            requestBody.put("password_confirmation", confirmPassword);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                messageLabel.setText("User registered successfully");
                dispose();
            } else {
                messageLabel.setText("Error registering user. Please try again.");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error registering user. Please try again.");
            messageLabel.setText("Error registering user. Please try again");
        }
    }
}
