package com.chopuryan.testtaskmts.service;

import com.chopuryan.testtaskmts.entity.TaskEntity;
import com.chopuryan.testtaskmts.exception.TaskNotFoundException;
import com.chopuryan.testtaskmts.exception.WrongUuidException;
import com.chopuryan.testtaskmts.model.Status;
import com.chopuryan.testtaskmts.model.StatusEnum;
import com.chopuryan.testtaskmts.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.chopuryan.testtaskmts.Constants.DATE_FORMAT;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    @Autowired
    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    public String createTask() {
        String uuid = UUID.randomUUID().toString();
        TaskEntity task = new TaskEntity();
        Runnable runnable = () -> {
            try {
                task.setTimestamp(new Date());
                task.setStatus(StatusEnum.RUNNING.toString());
                repository.save(task);
                TimeUnit.MINUTES.sleep(2);
                task.setTimestamp(new Date());
                task.setStatus(StatusEnum.FINISHED.toString());
                repository.save(task);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);

        task.setId(uuid);
        task.setStatus(StatusEnum.CREATED.toString());
        task.setTimestamp(new Date());
        repository.save(task);
        thread.start();
        return uuid;
    }

    public Status getTask(String id) {
        try {
            UUID.fromString(id);
            TaskEntity task = repository.findOne(id);
            if (task == null) {
                throw new TaskNotFoundException();
            }
            Status status = new Status();
            status.setStatus(task.getStatus());
            status.setTimestamp(new SimpleDateFormat(DATE_FORMAT).format(task.getTimestamp()));
            return status;
        } catch (IllegalArgumentException e) {
            throw new WrongUuidException();
        }
    }
}
