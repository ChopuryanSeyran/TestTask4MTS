package com.chopuryan.testtaskmts.rest;

import com.chopuryan.testtaskmts.exception.TaskNotFoundException;
import com.chopuryan.testtaskmts.exception.WrongUuidException;
import com.chopuryan.testtaskmts.model.Status;
import com.chopuryan.testtaskmts.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/")
@Api(value = "TaskControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(path = "/task", method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(ACCEPTED)
    @ResponseBody
    @ApiOperation("Create task")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "ACCEPTED", response = String.class)
    })
    public String createTask() {
        return taskService.createTask();
    }

    @RequestMapping(path = "/task/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @ResponseBody
    @ApiOperation("Gets the task with specific id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Status.class),
            @ApiResponse(code = 400, message = "Wrong UUID"),
            @ApiResponse(code = 404, message = "Task not found")
    })
    public Status getTask(@PathVariable String id) throws Exception {
        return taskService.getTask(id);
    }
}
