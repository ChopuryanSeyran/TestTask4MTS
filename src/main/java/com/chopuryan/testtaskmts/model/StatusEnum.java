package com.chopuryan.testtaskmts.model;

public enum StatusEnum {
    CREATED("created"),
    RUNNING("running"),
    FINISHED("finished");

    String description;
    private StatusEnum() {
    }

    private StatusEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
