package com.chopuryan.testtaskmts.integration;

import com.chopuryan.testtaskmts.entity.TaskEntity;
import com.chopuryan.testtaskmts.model.Status;
import com.chopuryan.testtaskmts.model.StatusEnum;
import com.chopuryan.testtaskmts.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chopuryan.testtaskmts.Constants.DATE_FORMAT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new ObjectMapper();
    }

    @Test
    public void getTaskWithInvalidId() throws Exception {
        final String taskId = "1";
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getTaskWithWrongId() throws Exception {
        final String taskId = UUID.randomUUID().toString();
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getTask() throws Exception {
        String taskId = UUID.randomUUID().toString();
        TaskEntity task = new TaskEntity();

        task.setId(taskId);
        task.setTimestamp(new Date());
        task.setStatus(StatusEnum.FINISHED.toString());
        repository.save(task);

        final MvcResult result = mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andReturn();
        final String content = result.getResponse().getContentAsString();
        Status status = this.mapper.readValue(content, Status.class);

        Assert.assertEquals(status.getStatus(), task.getStatus());
        Assert.assertEquals(
                status.getTimestamp(),
                new SimpleDateFormat(DATE_FORMAT)
                        .format(task.getTimestamp())
        );
    }


    @Test
    public void createTask() throws Exception {
        final String taskId;
        TaskEntity task;
        AtomicInteger taskCountBefore = new AtomicInteger();
        AtomicInteger taskCountAfter = new AtomicInteger();
        repository.findAll().forEach((TaskEntity inst) -> taskCountBefore.incrementAndGet());

        final MvcResult result = mockMvc.perform(
                post("/task"))
                .andExpect(status().isAccepted())
                .andReturn();

        repository.findAll().forEach((TaskEntity inst) -> taskCountAfter.getAndIncrement());
        taskId = result.getResponse().getContentAsString();
        task = repository.findOne(taskId);

        Assert.assertEquals(task.getStatus(), StatusEnum.CREATED.toString());
        Assert.assertEquals(taskCountAfter.get(), taskCountBefore.get()+1);

        TimeUnit.SECONDS.sleep(30);
        task = repository.findOne(taskId);
        Assert.assertEquals(task.getStatus(), StatusEnum.RUNNING.toString());

        TimeUnit.MINUTES.sleep(2);
        task = repository.findOne(taskId);
        Assert.assertEquals(task.getStatus(), StatusEnum.FINISHED.toString());
    }
}
