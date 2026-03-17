package com.collaboportal.api.controller;

import com.collaboportal.api.model.SystemSettingsResponseBody;
import com.collaboportal.common.spi.MaintenanceUrlResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/system_setting")
public class SystemSettingsController {
    private final MaintenanceUrlResolver maintenanceUrlResolver;

    public SystemSettingsController(MaintenanceUrlResolver maintenanceUrlResolver) {
        this.maintenanceUrlResolver = maintenanceUrlResolver;
    }

    @Value("${URL_PASS_CHANGE}")
    private String passChangeUrl;

    @Value("${URL_LOGOUT}")
    private String logoutUrl;

    @Value("${collaboportal_client_id_web}")
    private String clientId;

    @Value("${collaboportal_baseurl}")
    private String baseUrl;

    @GetMapping()
    public SystemSettingsResponseBody getSystemSettings() {
        String createLogoutUrl = logoutUrl + "?returnTo=" + maintenanceUrlResolver.resolveLogoutUrl() + "&client_id="
                + clientId;
        return new SystemSettingsResponseBody(passChangeUrl, createLogoutUrl);
    }
}
