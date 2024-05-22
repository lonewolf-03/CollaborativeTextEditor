package apt.project.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired    
    private  JavaMailSender mailSender;

    

    @Async
    public void send(String receiverMail, String content, String subject) throws Exception{
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setText(content);
            message.setSubject(subject);
            message.setTo(receiverMail);
            //message.setFrom("moayman200311@gmail.com");

            mailSender.send(message);
        }catch(Exception e){
            LOGGER.error("Failed to send a mail to " + receiverMail, e);
            throw new IllegalStateException("failed to send mail");
        }
    }
   
}
