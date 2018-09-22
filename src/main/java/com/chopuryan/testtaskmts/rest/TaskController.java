package com.chopuryan.testtaskmts.rest;

import com.chopuryan.testtaskmts.model.Status;
import com.chopuryan.testtaskmts.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class TaskController {

    @Autowired
    private TaskService TaskService;


    @RequestMapping(path = "/task", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @ResponseBody
    public String createTask() {
        return TaskService.createTask();
    }

    @RequestMapping(path = "/task/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @ResponseBody
    public Status getTask(@PathVariable String id) throws Exception {
        return TaskService.getTask(id);
    }
}
