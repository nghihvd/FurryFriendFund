package org.example.furryfriendfund.OTP;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {
    private final Map<String,String> otpData = new HashMap<>();
    private final Random random = new Random();

    public String generateOTP(String id) {
        String otp = String.format("%04d",random.nextInt(1000));
        otpData.put(id,otp);
        return otp;
    }

    public boolean validateOTP(String otp,String id) {
        String storedOTP = otpData.get(id);
        return storedOTP != null && otp.equals(storedOTP);
    }
}
