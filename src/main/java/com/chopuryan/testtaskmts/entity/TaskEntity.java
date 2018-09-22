package com.chopuryan.testtaskmts.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "task")
public class TaskEntity implements Serializable {

    @Id
    private String id;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public TaskEntity() {
    }

    public TaskEntity(String id, String status, Date timestamp) {
        this.id = id;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}