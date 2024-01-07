package com.mata.controller;

import com.mata.dto.Result;
import com.mata.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send-email-code")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/login-code/{email}")
    public Result loginCode(@PathVariable("email") String email){
        return emailService.sendLoginCode(email);
    }

    @PostMapping("/change-password-code/{email}")
    public Result changePasswordCode(@PathVariable("email") String email){
        return emailService.sendChangePasswordCode(email);
    }

    @PostMapping("/send-buy/success-code/{goodName}")
    public Result sendBuySuccessCode(@PathVariable("goodName") String goodName,@RequestHeader("email") String email){
        return emailService.sendBuySuccessCode(email,goodName);
    }

    @PostMapping("/send-buy/fail-code/{goodName}")
    public Result sendBuyFailCode(@PathVariable("goodName") String goodName,@RequestHeader("email") String email){
        return emailService.sendBuyFailCode(email,goodName);
    }

}
