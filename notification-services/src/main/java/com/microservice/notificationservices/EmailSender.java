package com.microservice.notificationservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSender {
    public void sendEmail(String ordernumber) {
        log.info("Order Placed Successfully - Order Number is " + ordernumber);
        log.info("Email Sent");
    }
}
