package org.example.furryfriendfund.OTP;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilloService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    private void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendVerificationCode(String toPhoneNumber,String verificationCode) {
        Message.creator(new PhoneNumber(toPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                "Your verification code is " + verificationCode).create();
    }
}
