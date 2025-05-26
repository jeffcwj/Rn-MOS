package com.gtastart.common.util;

public enum CsPayload {

    CSMOS("1csmos", "CS:MOS", "CSMOS"),
    CM("1cm", "ClientMod", "CM"),
    CSSO("1csso", "CS:SO", "CSSO");

    private String payload;
    private String title;
    private String csType;

    CsPayload(String payload, String title, String csType) {
        this.payload = payload;
        this.title = title;
        this.csType = csType;
    }
    public String getPayload() {
        return payload;
    }
    public String getCsType() {
        return csType;
    }
    public String getTitle() {
        return title;
    }
}
