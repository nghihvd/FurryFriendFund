package org.example.furryfriendfund.OTP;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setFrom("nghihvdse182563@fpt.edu.vn");
        String htmlBody = "<html><body>"+
                "<h2 style = 'color: #ceab21;'>Verification code of Furry Friend Fund </h2>"+
                "<p style = 'font-size: 16px;'>Here is your verification code</p>"+
                "<p style='text-align: center; font-size: 30px;'><strong>"+body+"</strong></p>"+
                "</body></html>";
        message.setText(htmlBody, true);

        mailSender.send(mimeMessage);
    }

    public void sendThankyouAdoptEmail(String toEmail, String subject,String petName,String accountName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setFrom("nghihvdse182563@fpt.cn");
        String htmlBody = "<html><body>"+
                " <p style = 'font-size: 16px;' ><strong>Dear "+accountName+",</strong></p>"+
                "<p>On behalf of everyone at Furry Friend Fund, we would like to express our heartfelt gratitude for choosing to adopt <strong>"+petName+"</strong>. Your decision to give  a loving home means the world to us and, most importantly, to "+petName+".</p>" +
                "<p>"+petName+" has been a cherished member of our shelter family, and we are overjoyed to see  embark on this new chapter with you. We trust that "+petName+" will bring endless joy, laughter, and companionship into your life.</p>" +
                "<p>If you ever have any questions, need advice, or simply want to share updates about "+petName+", please don’t hesitate to reach out to us. We love hearing stories about our animals thriving in their forever homes. </p>" +
                "<p><strong>Thank you once again for opening your heart and home. You have made a profound difference in the life of a deserving animal.<strong></p>"+
                "<p>Sincerely,</p>"+
                "<p>Furry Friend Fund</p>"+
                "</body></html>";
        message.setText(htmlBody, true);
        mailSender.send(mimeMessage);
    }
}


