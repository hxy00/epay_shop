package com.emt.shoptask.controller;

import com.emt.shoppay.sv.inter.IPayQueryApiSv;
import com.emt.shoptask.sv.impl.PayQuerySvImpl;
import com.emt.shoptask.sv.inter.IPayQuerySv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author: Mr.Huang
 * @Date: 2018-05-18
 * @Description:
 */
@Controller
@RequestMapping("/task/query")
public class TaskQueryController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPayQueryApiSv iPayQueryApiSv;

    @Autowired
    private IPayQuerySv iPayQuerySv;

    @RequestMapping("/queryFromABC")
    @ResponseBody
    public String queryFromABC(String orderId){
        Map<String, String> map = iPayQueryApiSv.queryFromABC("400001", orderId);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromAlipay")
    @ResponseBody
    public String queryFromAlipay(String orderId){
        Map<String, String> map = iPayQueryApiSv.queryFromAlipay("400001", orderId);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromBOC")
    @ResponseBody
    public String queryFromBOC(String orderId){
        Map<String, String> map = iPayQueryApiSv.queryFromBoc("400001", orderId);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromCCB")
    @ResponseBody
    public String queryFromCCB(String orderId, String orderDate){
        Map<String, String> map = iPayQueryApiSv.queryFromCcb("400001", orderId, orderDate);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromICBC")
    public String queryFromICBC(String orderId, String orderDate){
        Map<String, String> map = iPayQueryApiSv.queryFromIcbc("400001", orderId, orderDate);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromUnionpay")
    @ResponseBody
    public String queryFromUnionpay(String orderId, String orderDate){
        Map<String, String> map = iPayQueryApiSv.queryFromUnionpay("400001", orderId, orderDate);
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryFromWeixinpay")
    @ResponseBody
    public String queryFromWeixinpay(String orderId, String orderDate){
        Map<String, String> map = iPayQueryApiSv.queryFromWeixinpay("", "");
        logger.debug(map.toString());
        return map.toString();
    }

    @RequestMapping("/queryOrderState")
    @ResponseBody
    public String queryOrderState(String orderId){
        String result = iPayQuerySv.queryOrderState(orderId);
        return result;
    }
}
