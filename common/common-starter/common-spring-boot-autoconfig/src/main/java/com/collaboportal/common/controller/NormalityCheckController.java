package com.collaboportal.common.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.jwt.utils.JwtClaimUtils;
import com.collaboportal.common.utils.WebContextUtil;

@RestController
@RequestMapping("/api/v1")
public class NormalityCheckController {

    @GetMapping("/normality-check")
    public ResponseEntity<String> normalityCheck() {
        return ResponseEntity.ok("Normality check successful");
    }

    @GetMapping("/normality-check-2")
    public ResponseEntity<String> normalityCheck2() {
        return ResponseEntity.ok("Normality check successful 2");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String authToken = CommonHolder.getRequest().getCookieValue("AuthToken");
        if (authToken == null || authToken.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized: No AuthToken cookie found");
        }
        String sub = JwtClaimUtils.getSubject(authToken).orElse(null);
        String tokenType = JwtClaimUtils.getTokenType(authToken).orElse("");
        int role = JwtClaimUtils.getRoleAsInt(authToken).orElse(0);
        List<String> pids = JwtClaimUtils.getProjectIds(authToken);
        Long uid = JwtClaimUtils.get(authToken, "uid", Long.class).orElse(null);
        // 是否过期
        boolean expired = JwtClaimUtils.isExpired(authToken);

        // 全量 claims（不可变）
        Map<String, Object> all = JwtClaimUtils.getAllClaims(authToken);

        return ResponseEntity.ok("Login successful" +
                "\nsub: " + sub +
                "\ntokenType: " + tokenType +
                "\nrole: " + role +
                "\npids: " + pids +
                "\nuid: " + uid +
                "\nexpired: " + expired +
                "\nall claims: " + all.toString());
    }

}
