package com.irfansaf.safpass.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.BackingStoreException;

import com.irfansaf.safpass.Safpass;
import com.irfansaf.safpass.util.Configuration;


public class PurchaseCodeDialog extends JDialog {
    private JTextField purchaseCodeField;
    private JLabel messageLabel;
    private JButton submitButton;

    public PurchaseCodeDialog() {
        setTitle("Enter Purchase Code");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setModal(true);

        purchaseCodeField = new JTextField(16);
        messageLabel = new JLabel("Please Enter your purchase code:");
        submitButton = new JButton("Submit");

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
                    URL url = new URL("http://localhost/api/validate-purchase-code/" + purchaseCode);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Configuration.getInstance().savePurchaseCode(purchaseCode);
                        messageLabel.setText("Purchase code validated successfully.");
                        dispose();
                    } else {
                        messageLabel.setText("Invalid purchase code. Please try again.");
                    }
                } catch (IOException | BackingStoreException ex) {
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
