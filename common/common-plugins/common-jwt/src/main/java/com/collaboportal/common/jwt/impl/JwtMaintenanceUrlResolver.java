package com.collaboportal.common.jwt.impl;

import com.collaboportal.common.spi.MaintenanceUrlResolver;
import com.collaboportal.common.jwt.utils.JwtMaintenanceUtil;
import org.springframework.stereotype.Component;

@Component
public class JwtMaintenanceUrlResolver implements MaintenanceUrlResolver {
    @Override
    public String resolveLogoutUrl() {
        return JwtMaintenanceUtil.resolveLogoutUrl();
    }
}
