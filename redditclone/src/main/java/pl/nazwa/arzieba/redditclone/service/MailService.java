package pl.nazwa.arzieba.redditclone.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.nazwa.arzieba.redditclone.exceptions.SpringRedditException;
import pl.nazwa.arzieba.redditclone.model.NotificationEmail;

@Service
@Slf4j
public class MailService {


    private  JavaMailSender mailSender;
    private  MailContentBuilder mailContentBuilder;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public
    MailService(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Async
    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(from);
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try {
            mailSender.send(messagePreparator);
            log.info("Activation email sent successfully!");
        } catch (MailException e) {
            throw new SpringRedditException("Exception occurred when sending mail to "+notificationEmail.getRecipient());
        }
    }
}
