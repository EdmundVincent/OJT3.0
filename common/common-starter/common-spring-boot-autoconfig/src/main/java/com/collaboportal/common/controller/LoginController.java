package com.collaboportal.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("")
public class LoginController {

    @GetMapping("/orderlist")
    public void orderList() {
        BaseResponse baseResponse = CommonHolder.getResponse();
        baseResponse.redirect("/index.html");
        baseResponse.flush();
    }

    @GetMapping("/situationlist")
    public void situationList(@RequestParam String param) {
        BaseResponse baseResponse = CommonHolder.getResponse();
        baseResponse.redirect("/index.html");
        baseResponse.flush();
    }

    @GetMapping("/patientinfo")
    public void patientInfo(@RequestParam String param) {
        BaseResponse baseResponse = CommonHolder.getResponse();
        baseResponse.redirect("/index.html");
        baseResponse.flush();
    }

}
