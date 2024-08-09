package com.etnopolino.HotelServer.service.mailing;

import com.etnopolino.HotelServer.entity.NotificationEmail;
import com.etnopolino.HotelServer.exceptions.SpringHotelException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailContentBuilder mailContentBuilder;

    public void sendMail(NotificationEmail notificationEmail){

        MimeMessagePreparator messagePreparator = (mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("hotelManagement@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.buildMessage(notificationEmail.getBody()));
        };

        try{
            javaMailSender.send(messagePreparator);
            log.info("Activation email sent !!");
        }catch(MailException e){
            log.error("Exception occurred when sending mail", e);
            throw new SpringHotelException("Exception occured when sending mail to "+notificationEmail.getRecipient(), e);
        }
    }
}
