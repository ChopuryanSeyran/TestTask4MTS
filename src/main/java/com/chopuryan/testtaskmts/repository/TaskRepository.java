package com.chopuryan.testtaskmts.repository;

import com.chopuryan.testtaskmts.entity.TaskEntity;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<TaskEntity, String> {
}
