package com.learning.tasklist.repository;

import com.learning.tasklist.domain.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskListDao extends JpaRepository<TaskList, UUID> {
}
