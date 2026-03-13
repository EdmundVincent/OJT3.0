package com.collaboportal.common.model.VO;

public enum MoveUrl {
    ORDERLIST("/orderlist"),
    ORDERINPUT("/orderinput"),
    SITUATIONLIST("/situationlist"),
    PATIENTINFO("/patientinfo");

    private final String value;

    MoveUrl(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MoveUrl fromValue(String value) {
        for (MoveUrl url : MoveUrl.values()) {
            if (url.value.equals(value)) {
                return url;
            }
        }
        return null;
    }
}
