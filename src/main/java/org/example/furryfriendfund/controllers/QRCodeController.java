package org.example.furryfriendfund.controllers;

import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.furryfriendfund.QRCode.QRService;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class QRCodeController {
    @Autowired
    private final QRService qrService;

    public QRCodeController(QRService qrService) {
        this.qrService = qrService;
    }

    @GetMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQR(@RequestParam String eventId,
                                             @RequestParam String amount, HttpServletRequest request) throws WriterException, IOException {
        HttpSession session = request.getSession(false);
        if(session == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Accounts acc = (Accounts) session.getAttribute("accountID");
        if(acc == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        try{
            // create transaction information
            String qrText = qrService.createMBQRCode(acc.getAccountID(), eventId, amount,"0919845822");

            //call service to creeate QR code
            byte[] qrImage = qrService.generateQRCode(qrText,350,350);

            // return QR in byte and PNG
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg");

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } catch(WriterException | IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
