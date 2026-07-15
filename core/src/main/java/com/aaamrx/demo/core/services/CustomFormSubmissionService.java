package com.aaamrx.demo.core.services;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * OSGi Service to handle custom Adaptive Form submission logic, including
 * database interaction and sending confirmation emails.
 *
 * NOTE: For a real AEM project, the DataSource would be configured via the
 * Web Console and exposed as an OSGi Service.
 */
@Component(service = CustomFormSubmissionService.class)
public class CustomFormSubmissionService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFormSubmissionService.class);
    private static final String DATASOURCE_NAME = "AAAMRX_Ds"; // Must match the configured DataSource name

    // Inject the AEM Mail Service for sending emails
    @Reference
    private MessageGatewayService messageGatewayService;

    // Inject the configured DataSource for JDBC connection
    @Reference(target = "(datasource.name=" + DATASOURCE_NAME + ")")
    private DataSource dataSource;

    /**
     * Processes the form data by inserting a record into the database and sending an email.
     *
     * @param formData A map containing the submitted form fields (key: fieldName, value: fieldValue).
     * @return true if the submission was successful, false otherwise.
     */
    public boolean processSubmission(Map<String, String> formData) {
        if (formData == null || formData.isEmpty()) {
            LOG.error("Form data is null or empty.");
            return false;
        }

        // IMPORTANT: These field names (emailField, nameField, otherField) must match 
        // the field names used in your Adaptive Form.
        String formDataValues = formData.get("_guideValuesMap");
       
        if (formDataValues == null) {
            LOG.error("===================Parameter '_guideValuesMap' is missing.=================");
            return false;
        }
       
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> parsedData;

        try {
            parsedData = gson.fromJson(formDataValues, mapType);
        } catch (Exception e) {
            LOG.error("Error parsing nested JSON from _guideValuesMap: {}", e.getMessage(), e);
            return false;
        }

        String emailAddress = parsedData.get("CustomerEmail"); 
        String fullName = parsedData.get("CustomerName");     
        String shippingAddress = parsedData.get("ShippingAddress");
        String totalAmount = parsedData.get("TotalAmount");

        if (emailAddress == null || fullName == null) {
            LOG.error("===================Required fields (emailField or nameField) missing from form data.====================");
            return false;
        }

        boolean dbSuccess = insertRecord(fullName, emailAddress, shippingAddress, totalAmount);
        boolean emailSuccess = sendConfirmationEmail(emailAddress, fullName, shippingAddress, totalAmount);

        // Consider the entire submission successful only if both operations succeed
        return dbSuccess && emailSuccess;
    }

    /**
     * Inserts the form data into the MySQL database using JDBC.
     */
    private boolean insertRecord(String fullName, String emailAddress, String shippingAddress, String totalAmount) {
        LOG.info("Attempting to insert record for: {}", emailAddress);
        String sql = "INSERT INTO aaamrx_res.orders (CustomerName, CustomerEmail, ShippingAddress, TotalAmount) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {

            conn = dataSource.getConnection();
            
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false); // Disable auto-commit to manage the transaction
            }
            //Comment
            try (
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, emailAddress);
            ps.setString(3, shippingAddress);
            ps.setString(4, totalAmount);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                LOG.info("==================Successfully inserted record into database.===================");
                return true;
            } else {
                conn.rollback();
                LOG.error("==================Database insert failed: No rows affected.===================");
                return false;
            }
        }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOG.error("Error during transaction rollback: {}", rollbackEx.getMessage());
            }
            LOG.error("=========================Database error during insertion: {}============================", e.getMessage(), e);
            
        }finally {
            // Ensure the connection is always closed
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    LOG.error("====================Error closing connection: {}=============================", closeEx.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Sends a confirmation email using AEM's MessageGatewayService.
     */
    private boolean sendConfirmationEmail(String recipientEmail, String recipientName, String recepientShippingAddress, String recipientTotalAmount) {
        LOG.info("Attempting to send confirmation email to: {}", recipientEmail);

        try {
            // Get the default MessageGateway instance
            MessageGateway<Email> messageGateway = messageGatewayService.getGateway(SimpleEmail.class);

            if (messageGateway == null) {
                LOG.error("MessageGatewayService is not configured correctly or is unavailable.");
                return false;
            }

            // Construct the email object
            Email email = new SimpleEmail();
            email.setSubject("Your Form Submission Confirmation");
            email.setMsg("Hello " + recipientName + ",\n\nThank you for submitting the order. We have received your order and will delivered soon ASAP.\nTotal Amount: "+recipientTotalAmount+"\nThis will be delivered to the address: "+recepientShippingAddress);
            email.addTo(recipientEmail);
            email.setFrom("no-reply@aaamrx.com"); // Configure 'from' address

            // Send the email
            messageGateway.send(email);
            LOG.info("====================Confirmation email sent successfully to {}==============================", recipientEmail);
            return true;

        } catch (Exception e) {
            // Catches AddressException (from setFrom/addTo) and other potential mail exceptions
            LOG.error("==================================Failed to send confirmation email to {}: {}=============================================", recipientEmail, e.getMessage(), e);
            return false;
        }
    }
}