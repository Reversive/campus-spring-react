package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.MailingService;
import ar.edu.itba.paw.models.Course;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class MailingServiceImpl implements MailingService {

    private final Session session;
    private final SpringTemplateEngine templateEngine;

    private final String SERVER_MAIL = "mpvcampus@gmail.com";

    @Autowired
    public MailingServiceImpl(Session session, SpringTemplateEngine templateEngine) {
        this.session = session;
        this.templateEngine = templateEngine;
    }



    public void sendTeacherEmail(User student, String to, String subject, String content, Course course) {
        Map<String,Object> model = new HashMap<>();
        model.put("subjectName", course.getSubject().getName());
        model.put("student",student);
        model.put("subject", subject);
        model.put("content", content);
        model.put("year", "2021");
        sendThymeleafTemplateEmail(getMimeMessage(student.getEmail()), Collections.singletonList(to), subject, model, "student-email-to-teacher.html");
    }


    @Override
    @Async
    public void sendNewAnnouncementNotification(List<String> to, String title, String content, Course course, User author) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("content", content);
        model.put("subjectName", course.getSubject().getName());
        model.put("year", "2021");
        model.put("courseId", course.getCourseId());
        model.put("author", author);
        sendThymeleafTemplateEmail(to, "Nuevo anuncio en curso: " + course.getSubject().getName(), model, "new-announcement-notification.html");
    }

    private void sendEmail(Message message, List<String> to, String subject, String content, String contentType) {
        try {
            for (String destination : to) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            }
            message.setSubject(subject);
            message.setContent(content, contentType);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private MimeMessage getMimeMessage(String replyTo) {
        try {
            MimeMessage message = new MimeMessage(session);
            String from = String.format("\"%s\" <%s>", replyTo, SERVER_MAIL);
            message.setFrom(new InternetAddress(from));
            message.setReplyTo(new Address[]{new InternetAddress(replyTo)});
            return message;
        } catch (MessagingException mex) {
            throw new RuntimeException(mex.getMessage());
        }
    }

    private void sendThymeleafTemplateEmail(List<String> to, String subject, Map<String, Object> args, String templateName) {
        Context context = new Context();
        context.setVariables(args);
        String htmlBody = templateEngine.process(templateName, context);
        sendHtmlBroadcastEmail(to, subject, htmlBody);
    }

    private void sendHtmlBroadcastEmail(List<String> to, String subject, String content) {
        sendEmail(new MimeMessage(session), to, subject, content, "text/html");
    }

    private void sendThymeleafTemplateEmail(Message message,List<String> to,String subject, Map<String, Object> args, String templateName) {
        Context context = new Context();
        context.setVariables(args);
        String htmlBody = templateEngine.process(templateName, context);
        sendEmail(message,to,subject,htmlBody, "text/html");
    }
}
