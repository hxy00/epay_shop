package com.emt.shoppay.controller;

import com.emt.shoppay.pojo.ReturnObject;
import com.emt.shoppay.util.sms.SendSMS;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/epay/sms")
public class SMSController {

    @RequestMapping("/sendSMS")
    @ResponseBody
    public ReturnObject sendSMS(String tell, String content){
//        String smsContent = "订单号：" + 111 + "已完成支付，将该订单推送到应用系统时失败，次数已达20次！！！";
        return SendSMS.sendSmsCode(tell, content, "127.0.0.1");
    }
}
