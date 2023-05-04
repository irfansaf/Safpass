package com.irfansaf.safpass.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatPasswordField;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.irfansaf.safpass.util.SpringUtilities;
import com.irfansaf.safpass.ui.TextComponentFactory;
import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;

public class LoginDialog extends JDialog implements ActionListener {
    private JPanel buttonPanel;
    private JPanel fieldPanel;
    private JTextField emailOrUsernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private boolean registerRequested = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);

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
            dispose();
        } else {
            dispose();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

    }

    private void authenticateUser() {
        String emailOrUsername = emailOrUsernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            MessageDialog.showWarningMessage(this,"Please enter your email/username and password");
            return;
        }

        try {
            URL url = new URL("http:127.0.0.1:8000/api/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            String jsonInputString = String.format("{\"email_or_username\": \"%s\", \"password\": \"%s\"}", emailOrUsername, password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                MessageDialog.showInformationMessage(this,"Login success.");
                dispose();
            } else {
                MessageDialog.showWarningMessage(this,"Invalid email/username or password. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.showWarningMessage(this,"Error connecting to the server. Please try again");
        }
    }

    private void showRegistrationDialog() {
        RegistrationDialog registrationDialog = new RegistrationDialog(LoginDialog.this);
        registrationDialog.setVisible(true);
    }
}
