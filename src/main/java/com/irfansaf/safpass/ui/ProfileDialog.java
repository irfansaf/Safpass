package com.irfansaf.safpass.ui;

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
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.prefs.BackingStoreException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.irfansaf.safpass.model.User;
import com.irfansaf.safpass.util.Configuration;
import com.irfansaf.safpass.util.CryptUtils;
import com.irfansaf.safpass.util.SpringUtilities;

public class ProfileDialog extends JDialog{
    private User userId;
    private String accessToken;
    private SafPassFrame parentFrame;
    private JPanel profilePanel;
    private JPanel licensePanel;
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton editUsernameButton;
    private JButton editNameButton;
    private JButton editEmailButton;
    private JButton editPasswordButton;
    private JPasswordField licenseField;
    private JButton deleteLicenseButton;


    public ProfileDialog(SafPassFrame parent, User userId, String accessToken) {
        super(parent, "Profile", true);
        this.parentFrame = parent;
        this.userId = userId;
        this.accessToken = accessToken;

        getContentPane().setLayout(new GridLayout(2, 1));
        setupLayout();
        fetchUserInfo();
        setupLicensePanel();

        getContentPane().add(this.profilePanel, BorderLayout.NORTH);
        getContentPane().add(this.licensePanel, BorderLayout.SOUTH);

        setSize(400,300);
        setMinimumSize(new Dimension(270, 200));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupLicensePanel() {
        this.licensePanel = new JPanel(new SpringLayout());
        this.licenseField = TextComponentFactory.newPasswordField();
        licenseField.setEditable(false);
        licenseField.setEchoChar('*');

        licenseField.setText(Configuration.getInstance().getPurchaseCodeKey());

        this.deleteLicenseButton = new JButton("Delete");
        deleteLicenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Configuration.getInstance().deletePurchaseCode();
                    licenseField.setText("");
                    MessageDialog.showInformationMessage(ProfileDialog.this, "License Removed Successfully");
                } catch (BackingStoreException backingStoreException) {
                    MessageDialog.showErrorMessage(ProfileDialog.this, "Error while removing license");
                    backingStoreException.printStackTrace();
                }
            }
        });
        licensePanel.add(new JLabel("License:"));
        licensePanel.add(licenseField);
        licensePanel.add(deleteLicenseButton);
        licensePanel.setPreferredSize(new Dimension(400,50));
        SpringUtilities.makeCompactGrid(licensePanel,
                1,3,
                6,6,
                6,6);
    }

    public void updateLicense(String license) {
        this.licenseField.setText(license);
    }

    private void setupLayout() {
        this.usernameField = TextComponentFactory.newTextField();
        usernameField.setEditable(false);
        this.nameField = TextComponentFactory.newTextField();
        nameField.setEditable(false);
        this.emailField = TextComponentFactory.newTextField();
        emailField.setEditable(false);
        this.passwordField = TextComponentFactory.newPasswordField(false);
        passwordField.setEditable(false);
        passwordField.setEchoChar('\u2022');

        editUsernameButton = new JButton("", MessageDialog.getIcon("entry_edit_dark"));
        editUsernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditable(usernameField, editUsernameButton);
            }
        });

        editNameButton = new JButton("", MessageDialog.getIcon("entry_edit_dark"));
        editNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditable(nameField, editNameButton);
            }
        });

        editEmailButton = new JButton("", MessageDialog.getIcon("entry_edit_dark"));
        editEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditable(emailField, editEmailButton);
            }
        });

        editPasswordButton = new JButton("", MessageDialog.getIcon("entry_edit_dark"));
        editPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditable(passwordField, editPasswordButton);
            }
        });

        // Set up the layout with the UI components
        this.profilePanel = new JPanel(new SpringLayout());
        this.profilePanel.add(new JLabel("Username:"));
        this.profilePanel.add(this.usernameField);
        this.profilePanel.add(this.editUsernameButton);
        this.profilePanel.add(new JLabel("Name:"));
        this.profilePanel.add(this.nameField);
        this.profilePanel.add(this.editNameButton);
        this.profilePanel.add(new JLabel("Email:"));
        this.profilePanel.add(this.emailField);
        this.profilePanel.add(this.editEmailButton);
        this.profilePanel.add(new JLabel("Pasword:"));
        this.profilePanel.add(this.passwordField);
        this.profilePanel.add(this.editPasswordButton);
        SpringUtilities.makeCompactGrid(this.profilePanel,
                4, 3,
                6,6,
                6,6);
    }

    private void updateUserInfo(String fieldName, String newValue) {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put(fieldName, newValue);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                MessageDialog.showInformationMessage(this, "User information updated successfully");
            } else {
                MessageDialog.showWarningMessage(this, "Error updating user information. Please try again.");
            }
        } catch (IOException e) {
            MessageDialog.showErrorMessage(this, "Error updating user information. Please try again");
        }
    }

    private void fetchUserInfo() {
        String accessToken = parentFrame.getAccessToken();
        String userId = parentFrame.getUserId();
        try {
            URL url = new URL("http://127.0.0.1:8000/api/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(content.toString());

                JsonNode userData = jsonNode.get("data");

                // Populate the fields with the fetched user information
                usernameField.setText(userData.get("username").asText());
                nameField.setText(userData.get("first_name").asText() + " " + userData.get("last_name").asText());
                emailField.setText(userData.get("email").asText());
//                passwordField.setText("");
            } else {
                BufferedReader error = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorLine;
                StringBuilder errorContent = new StringBuilder();
                while ((errorLine = error.readLine()) != null) {
                    errorContent.append(errorLine);
                }
                error.close();
                System.out.println("Error response from the server: " + errorContent.toString());
                System.out.println("userId: " + userId);
                MessageDialog.showWarningMessage(this, "Error Fetching user information. Please check your internet connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.showWarningMessage(this, "Error Fetching user information.");
        }
    }

    private void toggleEditable(JTextComponent textField, JButton editButton) {
        textField.setEditable(!textField.isEditable());

        if (textField.isEditable()) {
            editButton = new JButton("", MessageDialog.getIcon("accept"));
        } else {
            editButton = new JButton("", MessageDialog.getIcon("entry_edit_dark"));

            String fieldName = null;
            if (textField == usernameField) {
                fieldName = "username";
                updateUserInfo(fieldName, textField.getText());
            } else if (textField == nameField) {
                String[] nameParts = textField.getText().split("\\s", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                updateUserInfo("first_name", firstName);
                updateUserInfo("last_name", lastName);
            } else if (textField == emailField) {
                fieldName = "email";
                updateUserInfo(fieldName, textField.getText());
            } else if (textField == passwordField) {
                fieldName = "password";
                updateUserInfo(fieldName, textField.getText());
            }
            if (fieldName != null) {
                updateUserInfo(fieldName, textField.getText());
            }
        }
    }
}
