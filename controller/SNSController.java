package com.sploot.api.controller;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
//TODO remove this .. just for testing purpose.
public class SNSController {
    @Autowired
    private AmazonSNSClient amazonSNSClient;

    private String TOPIC_ARN = "arn:aws:sns:ap-south-1:528742024584:otp\n";

//    @GetMapping("/subscribe/{mobileNo}")
//    public String subscribeToSNSTopic(@PathVariable("email") String mobileNo) {
//        SubscribeRequest subscribeRequest =
//                new SubscribeRequest(TOPIC_ARN, "sms", mobileNo);
//        return "check your mobile ";
//    }

    @GetMapping("/publish/{mobileNo}")
    public String publishToSNSTopic(@PathVariable() String mobileNo) {
        PublishRequest publishRequest =
                new PublishRequest(TOPIC_ARN,"test", "subject");
        amazonSNSClient.publish(publishRequest);
        return "check your mobile ";
    }
}
