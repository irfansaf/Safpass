package com.irfansaf.safpass.ui;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatTextField;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.irfansaf.safpass.Safpass;
import com.irfansaf.safpass.util.Configuration;


public class PurchaseCodeDialog extends JDialog {
    private static final Logger LOG = Logger.getLogger(PurchaseCodeDialog.class.getName());

    private final FlatTextField purchaseCodeField = new FlatTextField();
    private JLabel messageLabel;
    private final FlatButton submitButton = new FlatButton();

    public PurchaseCodeDialog() {
        setTitle("Enter Purchase Code");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setModal(true);

        purchaseCodeField.setColumns(16);
        messageLabel = new JLabel("Please Enter your purchase code:");
        submitButton.setText("Submit");

        JPanel centerPanel = new JPanel();
        centerPanel.add(purchaseCodeField);

        JPanel southPanel = new JPanel();
        southPanel.add(submitButton);

        add(messageLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String purchaseCode = purchaseCodeField.getText().trim();
                if (purchaseCode.isEmpty()) {
                    messageLabel.setText("Please enter a valid purchase code.");
                    return;
                }

                try {
                    URL url = new URL("http://127.0.0.1:8000/api/validate-purchase-code");
                    LOG.log(Level.INFO, "Request URL: {0}", url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setDoOutput(true);

                    String requestBody = String.format("{\"purchase_code\": \"%s\"}", purchaseCode);
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();

                    LOG.log(Level.INFO, "Status code: {0}", responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Configuration.getInstance().savePurchaseCode(purchaseCode);
                        messageLabel.setText("Purchase code validated successfully.");
                        dispose();
                    } else {
                        messageLabel.setText("Invalid purchase code. Please try again.");
                    }
                } catch (IOException | BackingStoreException ex) {
                    LOG.log(Level.SEVERE, "Error validating the purchase code. Please try again", e);
                    messageLabel.setText("Error validating the purchase code. Please try again");
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!Safpass.isPurchaseCodeValid()) {
                    System.exit(0);
                }
            }
        });
    }
}
