package org.example.furryfriendfund.QRCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class QRService {

    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG", outputStream);
        return outputStream.toByteArray();
    }

    public void generateQRCodeImageToFile(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE,width,height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG", path);
    }

    public String createMBQRCode(String accountID, String evnetName,String amount,String accNum){
        StringBuilder qrData = new StringBuilder();

        // QR type (00:01)
        qrData.append("00").append(String.format("%02d", 2)).append("01");

        //Bank code MB (38)
        qrData.append("38").append(String.format("%02d", accNum.length())).append(accNum);

        // Số tiền (54) - nếu có
        if (amount != null && !amount.isEmpty()) {
            qrData.append("54").append(String.format("%02d", amount.length())).append(amount);
        }

        // Mã quốc gia (58:VN)
        qrData.append("58").append(String.format("%02d", 2)).append("VN");


        qrData.append("59").append(String.format("%02d","HOANG VO DONG NGHI" .length())).append("HOANG VO DONG NGHI");

        // Nội dung chuyển khoản (60)
        if (evnetName != null && !evnetName.isEmpty()) {
            qrData.append("60").append(String.format("%02d", evnetName.length())).append(evnetName);
        }
        return qrData.toString();
    }
}
