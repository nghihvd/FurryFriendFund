package org.example.furryfriendfund.OTP;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) throws MessagingException {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("nghihvdse182563@fpt.edu.vn");
//
//        mailSender.send(message);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setFrom("nghihvdse182563@fpt.edu.vn");
        String htmlBody = "<html><body>"+
                "<h2 style = 'color: #ceab21;'>Verification code of Furry Friend Fund </h2>"+
                "<p style = 'font-size: 16px;'>Here is your verification code</p>"+
                "<p style='text-align: center; font-size: 30px;'><strong>"+body+"</strong></p>"+
                "<img src = 'cid:image1'width='1000' height='500'/>"+
                "</body></html>";
        message.setText(htmlBody, true);
        FileSystemResource src = new FileSystemResource("static/images/homepage.png");
        message.addInline("image1", src);
        mailSender.send(mimeMessage);
    }
}


