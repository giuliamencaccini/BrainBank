package it.ispwproject.brainbank.service;

/**
 * Servizio per l'invio di notifiche email tramite SendGrid.
 * Gestisce la comunicazione con il servizio email esterno,
 * mantenendo separata la logica applicativa dall'invio delle email.
 * In futuro può essere esteso per supportare altri canali
 * di notifica come SMS o notifiche push.
 */

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

public class NotificationService {

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

    private static final String TEMPLATE_CONFIRMATION       = "d-4d49f14c5f734b3cb51e504d56823d0e";
    private static final String TEMPLATE_CANCELLATION       = "d-9535f2c985ad4dc5ab7c51980e6069b9";
    private static final String TEMPLATE_NEW_ACTIVITY       = "d-5fc4b82a3df44ae4ac065f94932f1962";
    private static final String TEMPLATE_CONFIRMATION_TUTOR = "d-a56071917983440a8473eb642cac5d88";
    private static final String TEMPLATE_CANCELLATION_TUTOR = "d-5187d5081a2a4baeb955d1665654296";

    // Template data keys
    private static final String KEY_STUDENT_NAME = "studentName";
    private static final String KEY_TUTOR_NAME   = "tutorName";
    private static final String KEY_SUBJECT_NAME = "subjectName";
    private static final String KEY_DATE         = "date";
    private static final String KEY_START_TIME   = "startTime";
    private static final String KEY_END_TIME     = "endTime";
    private static final String KEY_MEET_LINK    = "meetLink";
    private static final String KEY_DESCRIPTION  = "description";

    private NotificationService() {}

    // ================================================================== //
    //  Prenotazione confermata – studente
    // ================================================================== //

    public static void sendBookingConfirmation(String toEmail, String studentName,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, studentName, booking);
        p.addDynamicTemplateData(KEY_MEET_LINK, booking.getMeetLink());
        sendTemplateEmail(TEMPLATE_CONFIRMATION, p);
    }

    // ================================================================== //
    //  Prenotazione annullata – studente
    // ================================================================== //

    public static void sendBookingCancellation(String toEmail, String studentName,
                                               BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalization(toEmail, studentName, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION, p);
    }

    // ================================================================== //
    //  Prenotazione confermata – tutor
    // ================================================================== //

    public static void sendBookingConfirmationToTutor(String toEmail,
                                                      BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForTutor(toEmail, booking);
        p.addDynamicTemplateData(KEY_MEET_LINK, booking.getMeetLink());
        sendTemplateEmail(TEMPLATE_CONFIRMATION_TUTOR, p);
    }

    // ================================================================== //
    //  Prenotazione annullata – tutor
    // ================================================================== //

    public static void sendBookingCancellationToTutor(String toEmail,
                                                      BookingResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForTutor(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION_TUTOR, p);
    }

    // ================================================================== //
    //  Nuova attività assegnata
    // ================================================================== //

    public static void sendNewActivity(String toEmail, String studentName,
                                       String tutorName, String description) throws NotificationException {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_STUDENT_NAME, studentName);
        p.addDynamicTemplateData(KEY_TUTOR_NAME,   tutorName);
        p.addDynamicTemplateData(KEY_DESCRIPTION,  description);
        sendTemplateEmail(TEMPLATE_NEW_ACTIVITY, p);
    }

    // ================================================================== //
    //  Metodi privati
    // ================================================================== //

    private static Personalization buildPersonalization(String toEmail, String studentName,
                                                        BookingResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_STUDENT_NAME, studentName);
        p.addDynamicTemplateData(KEY_SUBJECT_NAME, booking.getSubject().getName());
        p.addDynamicTemplateData(KEY_TUTOR_NAME,   booking.getTutor().getName());
        p.addDynamicTemplateData(KEY_DATE,         booking.getTimeSlot().getDate().toString());
        p.addDynamicTemplateData(KEY_START_TIME,   booking.getTimeSlot().getStartTime().toString());
        p.addDynamicTemplateData(KEY_END_TIME,     booking.getTimeSlot().getEndTime().toString());
        return p;
    }

    private static Personalization buildPersonalizationForTutor(String toEmail,
                                                                BookingResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_TUTOR_NAME,   booking.getTutor().getName() + " " + booking.getTutor().getSurname());
        p.addDynamicTemplateData(KEY_STUDENT_NAME, booking.getStudent() != null
                ? booking.getStudent().getName() + " " + booking.getStudent().getSurname() : "");
        p.addDynamicTemplateData(KEY_SUBJECT_NAME, booking.getSubject().getName());
        p.addDynamicTemplateData(KEY_DATE,         booking.getTimeSlot().getDate().toString());
        p.addDynamicTemplateData(KEY_START_TIME,   booking.getTimeSlot().getStartTime().toString());
        p.addDynamicTemplateData(KEY_END_TIME,     booking.getTimeSlot().getEndTime().toString());
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