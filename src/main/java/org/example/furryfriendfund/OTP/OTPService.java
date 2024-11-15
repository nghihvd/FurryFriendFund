package org.example.furryfriendfund.OTP;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {
    private final Map<String,String> otpData = new HashMap<>();
    private final Random random = new Random();

    public String generateOTP(String email) {
        String otp = String.format("%04d",random.nextInt(1000));
        otpData.put(email,otp);
        return otp;
    }

    public boolean validateOTP(String otp,String phoneNumber) {
        String storedOTP = otpData.get(phoneNumber);
        return storedOTP != null && otp.equals(storedOTP);
    }
}
