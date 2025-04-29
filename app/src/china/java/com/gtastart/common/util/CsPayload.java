package com.gtastart.common.util;

public enum CsPayload {

    CSMOS("1csmos", "CS:MOS"),
    CM("1cm", "ClientMod");

    private String payload;
    private String title;

    CsPayload(String payload, String title) {
        this.payload = payload;
        this.title = title;
    }
    public String getPayload() {
        return payload;
    }
    public String getTitle() {
        return title;
    }
}
