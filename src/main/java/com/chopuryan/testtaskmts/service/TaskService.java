package com.chopuryan.testtaskmts.service;

import com.chopuryan.testtaskmts.model.Status;

public interface TaskService {

    String createTask();

    Status getTask(String id) throws Exception;
}
