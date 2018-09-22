package com.chopuryan.testtaskmts.model;

import java.io.Serializable;

public class Status implements Serializable {

    private String status;
    private String timestamp;

    public Status() {
    }

    public Status(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
