package com.care.medi.emails;

import com.care.medi.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendAppointmentConfirmation(
            String toEmail,
            String patientName,
            String doctorName,
            String date,
            String time,
            Long appointmentId
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, Constants.ENCODING);

            helper.setTo(toEmail);
            helper.setSubject("✅ Appointment Confirmed - Hospital Management System");
            helper.setText(buildAppointmentConfirmationEmail(patientName, doctorName, date, time, appointmentId), true);

            mailSender.send(mimeMessage);
            log.info("Appointment confirmation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error(Constants.FAILED_TO_SEND_NOTIFICATION, toEmail, e);
        }
    }

    @Async
    public void sendAppointmentCancellation(
            String toEmail,
            String patientName,
            String doctorName,
            String date,
            String time,
            Long appointmentId
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, Constants.ENCODING);

            helper.setTo(toEmail);
            helper.setSubject("❌ Appointment Cancelled - Hospital Management System");
            helper.setText(buildAppointmentCancellationEmail(patientName, doctorName, date, time, appointmentId), true);

            mailSender.send(mimeMessage);
            log.info("Successfully sent the appointment cancellation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error(Constants.FAILED_TO_SEND_NOTIFICATION, toEmail, e);
        }
    }

    @Async
    public void sendAppointmentReminder(
            String toEmail,
            String patientName,
            String doctorName,
            String date,
            String time,
            Long appointmentId
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, Constants.ENCODING);

            helper.setTo(toEmail);
            helper.setSubject("⏰ Appointment Reminder - Hospital Management System");
            helper.setText(buildAppointmentReminderEmail(patientName, doctorName, date, time, appointmentId), true);

            mailSender.send(mimeMessage);
            log.info("Successfully sent the appointment reminder email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error(Constants.FAILED_TO_SEND_NOTIFICATION, toEmail, e);
        }
    }

    private String buildAppointmentConfirmationEmail(String patientName, String doctorName,
                                                     String date, String time, Long appointmentId
    ) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Appointment Confirmation</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f7fa;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        background: #ffffff;
                        margin: 20px auto;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
                    }
                    .header {
                        background: #2a7de1;
                        color: #ffffff;
                        padding: 20px;
                        text-align: center;
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        padding: 25px;
                        color: #333333;
                        line-height: 1.6;
                    }
                    .details-table {
                        width: 100%%;
                        border-collapse: collapse;
                        margin-top: 15px;
                        background: #f9f9f9;
                        border-radius: 5px;
                    }
                    .details-table td {
                        padding: 12px;
                        border-bottom: 1px solid #e0e0e0;
                    }
                    .details-table tr:last-child td {
                        border-bottom: none;
                    }
                    .details-table td:first-child {
                        font-weight: bold;
                        color: #555;
                        width: 40%%;
                    }
                    .footer {
                        background: #f0f0f0;
                        text-align: center;
                        padding: 15px;
                        font-size: 13px;
                        color: #666666;
                    }
                    .success-badge {
                        display: inline-block;
                        background: #4caf50;
                        color: white;
                        padding: 5px 15px;
                        border-radius: 20px;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                    .note {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 12px;
                        margin-top: 20px;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>✅ Appointment Confirmed</h2>
                    </div>
                    <div class="content">
                        <span class="success-badge">Booking Successful</span>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your appointment has been successfully booked. Below are your appointment details:</p>
                        <table class="details-table">
                            <tr>
                                <td>Appointment ID:</td>
                                <td>#%s</td>
                            </tr>
                            <tr>
                                <td>Date:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Time:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Doctor:</td>
                                <td>Dr. %s</td>
                            </tr>
                        </table>
                        <div class="note">
                            <strong>📌 Important:</strong> Please arrive 15 minutes before your appointment time.
                        </div>
                        <p>If you need to modify or cancel your appointment, please contact us at least 24 hours in advance.</p>
                        <p>Thank you for choosing our hospital. We look forward to serving you.</p>
                    </div>
                    <div class="footer">
                        © %d Hospital Management System – All Rights Reserved
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName, appointmentId, date, time, doctorName, Year.now().getValue());
    }

    private String buildAppointmentCancellationEmail(String patientName, String doctorName,
                                                     String date, String time, Long appointmentId) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Appointment Cancelled</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f7fa;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        background: #ffffff;
                        margin: 20px auto;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
                    }
                    .header {
                        background: #e74c3c;
                        color: #ffffff;
                        padding: 20px;
                        text-align: center;
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        padding: 25px;
                        color: #333333;
                        line-height: 1.6;
                    }
                    .details-table {
                        width: 100%%;
                        border-collapse: collapse;
                        margin-top: 15px;
                        background: #f9f9f9;
                        border-radius: 5px;
                    }
                    .details-table td {
                        padding: 12px;
                        border-bottom: 1px solid #e0e0e0;
                    }
                    .details-table tr:last-child td {
                        border-bottom: none;
                    }
                    .details-table td:first-child {
                        font-weight: bold;
                        color: #555;
                        width: 40%%;
                    }
                    .footer {
                        background: #f0f0f0;
                        text-align: center;
                        padding: 15px;
                        font-size: 13px;
                        color: #666666;
                    }
                    .cancelled-badge {
                        display: inline-block;
                        background: #e74c3c;
                        color: white;
                        padding: 5px 15px;
                        border-radius: 20px;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>❌ Appointment Cancelled</h2>
                    </div>
                    <div class="content">
                        <span class="cancelled-badge">Cancellation Confirmed</span>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your appointment has been cancelled. Below are the details of the cancelled appointment:</p>
                        <table class="details-table">
                            <tr>
                                <td>Appointment ID:</td>
                                <td>#%s</td>
                            </tr>
                            <tr>
                                <td>Date:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Time:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Doctor:</td>
                                <td>Dr. %s</td>
                            </tr>
                        </table>
                        <p>If you wish to reschedule, please contact us or book a new appointment through our system.</p>
                        <p>Thank you for choosing our hospital.</p>
                    </div>
                    <div class="footer">
                        © %d Hospital Management System – All Rights Reserved
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName, appointmentId, date, time, doctorName, Year.now().getValue());
    }

    private String buildAppointmentReminderEmail(String patientName, String doctorName,
                                                 String date, String time, Long appointmentId) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Appointment Reminder</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f7fa;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        background: #ffffff;
                        margin: 20px auto;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
                    }
                    .header {
                        background: #ff9800;
                        color: #ffffff;
                        padding: 20px;
                        text-align: center;
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        padding: 25px;
                        color: #333333;
                        line-height: 1.6;
                    }
                    .details-table {
                        width: 100%%;
                        border-collapse: collapse;
                        margin-top: 15px;
                        background: #f9f9f9;
                        border-radius: 5px;
                    }
                    .details-table td {
                        padding: 12px;
                        border-bottom: 1px solid #e0e0e0;
                    }
                    .details-table tr:last-child td {
                        border-bottom: none;
                    }
                    .details-table td:first-child {
                        font-weight: bold;
                        color: #555;
                        width: 40%%;
                    }
                    .footer {
                        background: #f0f0f0;
                        text-align: center;
                        padding: 15px;
                        font-size: 13px;
                        color: #666666;
                    }
                    .reminder-badge {
                        display: inline-block;
                        background: #ff9800;
                        color: white;
                        padding: 5px 15px;
                        border-radius: 20px;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                    .note {
                        background: #e3f2fd;
                        border-left: 4px solid #2196f3;
                        padding: 12px;
                        margin-top: 20px;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>⏰ Appointment Reminder</h2>
                    </div>
                    <div class="content">
                        <span class="reminder-badge">Upcoming Appointment</span>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>This is a reminder about your upcoming appointment:</p>
                        <table class="details-table">
                            <tr>
                                <td>Appointment ID:</td>
                                <td>#%s</td>
                            </tr>
                            <tr>
                                <td>Date:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Time:</td>
                                <td>%s</td>
                            </tr>
                            <tr>
                                <td>Doctor:</td>
                                <td>Dr. %s</td>
                            </tr>
                        </table>
                        <div class="note">
                            <strong>📌 Reminder:</strong> Please arrive 15 minutes before your appointment time.
                        </div>
                        <p>We look forward to seeing you!</p>
                    </div>
                    <div class="footer">
                        © %d Hospital Management System – All Rights Reserved
                    </div>
                </div>
            </body>
            </html>
            """.formatted(patientName, appointmentId, date, time, doctorName, Year.now().getValue());
    }
}


