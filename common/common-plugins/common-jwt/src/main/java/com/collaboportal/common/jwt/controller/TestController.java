package com.collaboportal.common.jwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;


import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;





@Controller
@RequestMapping("/testEnv")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping
    public void redirectToHtml(HttpServletResponse response) {
        logger.info("Redirecting to /testEnv.html");


        response.setStatus(HttpServletResponse.SC_FOUND);   // 302


        response.setHeader("Location", "/testEnv.html");

    }
}


