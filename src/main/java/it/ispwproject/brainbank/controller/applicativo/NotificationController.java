package it.ispwproject.brainbank.controller.applicativo;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.exception.NotificationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationController {

    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Impossibile caricare db.properties");
        }
    }

    private static final String API_KEY    = properties.getProperty("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = properties.getProperty("SENDGRID_FROM_EMAIL");

    private static final String TEMPLATE_CONFIRMATION = "d-4d49f14c5f734b3cb51e504d56823d0e";
    private static final String TEMPLATE_CANCELLATION = "d-9535f2c985ad4dc5ab7c51980e6069b9";

    private NotificationController() {}

    // ================================================================== //
    //  Prenotazione confermata
    // ================================================================== //

    public static void sendBookingConfirmation(String toEmail, String studentName,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, studentName, booking);
        p.addDynamicTemplateData("meetLink", booking.getMeetLink());
        sendTemplateEmail(TEMPLATE_CONFIRMATION, p);
    }

    // ================================================================== //
    //  Prenotazione annullata
    // ================================================================== //

    public static void sendBookingCancellation(String toEmail, String studentName,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, studentName, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION, p);
    }

    // ================================================================== //
    //  Metodi privati
    // ================================================================== //

    private static Personalization buildPersonalization(String toEmail, String studentName,
                                                        BookingResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData("studentName", studentName);
        p.addDynamicTemplateData("subjectName", booking.getSubject().getName());
        p.addDynamicTemplateData("tutorName",   booking.getTutor().getName());
        p.addDynamicTemplateData("date",        booking.getTimeSlot().getDate().toString());
        p.addDynamicTemplateData("startTime",   booking.getTimeSlot().getStartTime().toString());
        p.addDynamicTemplateData("endTime",     booking.getTimeSlot().getEndTime().toString());
        return p;
    }

    private static void sendTemplateEmail(String templateId,
                                          Personalization personalization) throws NotificationException {
        Mail mail = new Mail();
        mail.setFrom(new Email(FROM_EMAIL, "BrainBank"));
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                throw new NotificationException(
                        "Errore invio email (status " + response.getStatusCode() +
                                "): " + response.getBody());
            }

        } catch (IOException e) {
            throw new NotificationException("Errore durante l'invio email: " + e.getMessage(), e);
        }
    }
}