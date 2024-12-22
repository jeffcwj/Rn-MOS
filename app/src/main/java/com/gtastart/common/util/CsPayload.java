package com.gtastart.common.util;

public enum CsPayload {

    CSMOS("1csmos"),
    CM("1cm");

    private String payload;

    CsPayload(String payload) {
        this.payload = payload;
    }
    public String getPayload() {
        return payload;
    }
}
