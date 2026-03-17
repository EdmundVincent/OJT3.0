package com.collaboportal.common.config;

import java.util.Map;

public class PositionCodeConfig implements BaseConfig {

    private Map<String, String> rules;

    @Override
    public String getConfigPrefix() {
        return "util.collaboportal.positioncode";
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public PositionCodeConfig setRules(Map<String, String> rules) {
        this.rules = rules;
        return this;
    }

}
