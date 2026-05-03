package com.campus.events.service;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.name:Smart Campus Events}")
    private String appName;

    @Async
    public void sendRegistrationConfirmation(Registration registration) {
        log.info("📧 Attempting to send email to: {}", registration.getEmail());
        log.info("📧 From email configured as: {}", fromEmail);

        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("❌ MAIL_USERNAME not configured — skipping email");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(registration.getEmail());
            helper.setSubject("✅ Registration Confirmed — " + registration.getEvent().getTitle());
            helper.setText(buildHtml(registration), true);

            log.info("📧 Sending email via SMTP...");
            mailSender.send(message);
            log.info("✅ Email successfully sent to {}", registration.getEmail());

        } catch (MessagingException e) {
            log.error("❌ MessagingException sending email to {}: {}", registration.getEmail(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to {}: {}", registration.getEmail(), e.getMessage(), e);
        }
    }

    private String buildHtml(Registration reg) {
        Event event = reg.getEvent();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a");
        String dateStr = event.getEventDate() != null ? event.getEventDate().format(dateFmt) : "TBD";
        String timeStr = event.getEventTime() != null ? event.getEventTime().format(timeFmt) : "TBD";

        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"/></head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:32px 0;">
                <tr><td align="center">
                  <table width="580" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:580px;width:100%%;">
                    <tr>
                      <td style="background:linear-gradient(135deg,#1a73e8,#0d47a1);padding:36px 40px;text-align:center;">
                        <div style="font-size:36px;margin-bottom:8px;">🎓</div>
                        <h1 style="color:#ffffff;margin:0;font-size:24px;font-weight:700;">Registration Confirmed!</h1>
                        <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:15px;">You're all set for the event</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="color:#374151;font-size:16px;margin:0 0 24px;">
                          Hi <strong>%s</strong>,<br/><br/>
                          Your registration has been confirmed. Here are your event details:
                        </p>
                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="background:#f0f7ff;border:1.5px solid #bfdbfe;border-radius:10px;margin-bottom:28px;">
                          <tr><td style="padding:24px 28px;">
                            <h2 style="color:#1a73e8;margin:0 0 16px;font-size:20px;">%s</h2>
                            <p style="margin:6px 0;color:#374151;font-size:14px;">📅 <strong>Date:</strong> %s</p>
                            <p style="margin:6px 0;color:#374151;font-size:14px;">🕐 <strong>Time:</strong> %s</p>
                            <p style="margin:6px 0;color:#374151;font-size:14px;">📍 <strong>Venue:</strong> %s</p>
                            <p style="margin:6px 0;color:#374151;font-size:14px;">🏫 <strong>Type:</strong> %s</p>
                          </td></tr>
                        </table>
                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="border:1px solid #e5e7eb;border-radius:8px;margin-bottom:28px;">
                          <tr style="background:#f9fafb;">
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;font-weight:600;border-bottom:1px solid #e5e7eb;width:40%%;">Name</td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;border-bottom:1px solid #e5e7eb;">%s</td>
                          </tr>
                          <tr>
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;font-weight:600;border-bottom:1px solid #e5e7eb;">Roll Number</td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;border-bottom:1px solid #e5e7eb;">%s</td>
                          </tr>
                          <tr style="background:#f9fafb;">
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;font-weight:600;">Department</td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;">%s</td>
                          </tr>
                        </table>
                        <div style="background:#fef3c7;border:1px solid #fcd34d;border-radius:8px;padding:16px 20px;margin-bottom:28px;">
                          <p style="margin:0;color:#92400e;font-size:13px;">
                            <strong>📌 Important:</strong> Please carry this email or your roll number on the day of the event.
                          </p>
                        </div>
                        <p style="color:#374151;font-size:15px;margin:0;">See you there! 🎉<br/><strong>%s Team</strong></p>
                      </td>
                    </tr>
                    <tr>
                      <td style="background:#f9fafb;border-top:1px solid #e5e7eb;padding:20px 40px;text-align:center;">
                        <p style="color:#9ca3af;font-size:12px;margin:0;">Automated confirmation from %s. Do not reply.</p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                reg.getStudentName(), event.getTitle(),
                dateStr, timeStr, event.getVenue(), event.getEventType(),
                reg.getStudentName(), reg.getRollNumber(), reg.getDepartment(),
                appName, appName
        );
    }
}
