package com.campus.events.service;

import com.campus.events.model.Event;
import com.campus.events.model.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_API = "https://api.resend.com/emails";

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    @Value("${app.name:Smart Campus Events}")
    private String appName;

    @Async
    public void sendRegistrationConfirmation(Registration registration) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("RESEND_API_KEY not configured — skipping email for {}", registration.getEmail());
            return;
        }

        try {
            String html = buildHtml(registration);
            String subject = "✅ Registration Confirmed — " + registration.getEvent().getTitle();

            String jsonBody = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "%s",
                  "html": %s
                }
                """.formatted(
                    fromEmail,
                    registration.getEmail(),
                    subject.replace("\"", "\\\""),
                    toJsonString(html)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RESEND_API))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.info("✅ Confirmation email sent to {}", registration.getEmail());
            } else {
                log.error("❌ Resend API error {} : {}", response.statusCode(), response.body());
            }

        } catch (Exception e) {
            log.error("❌ Failed to send email to {} : {}", registration.getEmail(), e.getMessage());
        }
    }

    private String toJsonString(String text) {
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            + "\"";
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
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width,initial-scale=1"/>
            </head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:32px 0;">
                <tr><td align="center">
                  <table width="580" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:580px;width:100%%;">

                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#1a73e8,#0d47a1);
                                 padding:36px 40px;text-align:center;">
                        <div style="font-size:36px;margin-bottom:8px;">🎓</div>
                        <h1 style="color:#ffffff;margin:0;font-size:24px;font-weight:700;">
                          Registration Confirmed!
                        </h1>
                        <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:15px;">
                          You're all set for the event
                        </p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="color:#374151;font-size:16px;margin:0 0 24px;">
                          Hi <strong>%s</strong>,<br/><br/>
                          Your registration has been successfully confirmed. Here are your event details:
                        </p>

                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="background:#f0f7ff;border:1.5px solid #bfdbfe;
                                      border-radius:10px;margin-bottom:28px;">
                          <tr>
                            <td style="padding:24px 28px;">
                              <h2 style="color:#1a73e8;margin:0 0 20px;font-size:20px;">%s</h2>
                              <table cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="padding:5px 16px 5px 0;color:#6b7280;font-size:13px;
                                             font-weight:600;white-space:nowrap;">📅 Date</td>
                                  <td style="padding:5px 0;color:#111827;font-size:14px;
                                             font-weight:600;">%s</td>
                                </tr>
                                <tr>
                                  <td style="padding:5px 16px 5px 0;color:#6b7280;font-size:13px;
                                             font-weight:600;">🕐 Time</td>
                                  <td style="padding:5px 0;color:#111827;font-size:14px;
                                             font-weight:600;">%s</td>
                                </tr>
                                <tr>
                                  <td style="padding:5px 16px 5px 0;color:#6b7280;font-size:13px;
                                             font-weight:600;">📍 Venue</td>
                                  <td style="padding:5px 0;color:#111827;font-size:14px;
                                             font-weight:600;">%s</td>
                                </tr>
                                <tr>
                                  <td style="padding:5px 16px 5px 0;color:#6b7280;font-size:13px;
                                             font-weight:600;">🏫 Type</td>
                                  <td style="padding:5px 0;color:#111827;font-size:14px;
                                             font-weight:600;">%s</td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>

                        <h3 style="color:#374151;font-size:15px;margin:0 0 12px;
                                   font-weight:700;text-transform:uppercase;">
                          Your Registration Details
                        </h3>
                        <table width="100%%" cellpadding="0" cellspacing="0"
                               style="border:1px solid #e5e7eb;border-radius:8px;margin-bottom:28px;">
                          <tr style="background:#f9fafb;">
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;
                                       font-weight:600;border-bottom:1px solid #e5e7eb;width:40%%;">
                              Name
                            </td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;
                                       border-bottom:1px solid #e5e7eb;">%s</td>
                          </tr>
                          <tr>
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;
                                       font-weight:600;border-bottom:1px solid #e5e7eb;">
                              Roll Number
                            </td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;
                                       border-bottom:1px solid #e5e7eb;">%s</td>
                          </tr>
                          <tr style="background:#f9fafb;">
                            <td style="padding:10px 16px;color:#6b7280;font-size:13px;font-weight:600;">
                              Department
                            </td>
                            <td style="padding:10px 16px;color:#111827;font-size:13px;">%s</td>
                          </tr>
                        </table>

                        <div style="background:#fef3c7;border:1px solid #fcd34d;border-radius:8px;
                                    padding:16px 20px;margin-bottom:28px;">
                          <p style="margin:0;color:#92400e;font-size:13px;line-height:1.6;">
                            <strong>📌 Important:</strong> Please carry this email or your roll number
                            on the day of the event for verification.
                          </p>
                        </div>

                        <p style="color:#374151;font-size:15px;margin:0;">
                          See you there! 🎉<br/>
                          <strong>%s Team</strong>
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#f9fafb;border-top:1px solid #e5e7eb;
                                 padding:20px 40px;text-align:center;">
                        <p style="color:#9ca3af;font-size:12px;margin:0;line-height:1.6;">
                          This is an automated confirmation from %s.<br/>
                          Please do not reply to this email.
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                reg.getStudentName(),
                event.getTitle(),
                dateStr,
                timeStr,
                event.getVenue(),
                event.getEventType(),
                reg.getStudentName(),
                reg.getRollNumber(),
                reg.getDepartment(),
                appName,
                appName
        );
    }
}