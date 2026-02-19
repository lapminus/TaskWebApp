package com.learning.tasklist.repository;

import com.learning.tasklist.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskDao extends JpaRepository<Task, UUID> {

    @Query(value = "SELECT * FROM task t WHERE t.task_list_id = :id ORDER BY t.created ASC",
           nativeQuery = true)
    List<Task> findAllTasksByTaskListId(@Param("id") UUID id);

    @Query(value = "SELECT * FROM task t WHERE t.task_list_id = :id AND t.id = :taskId",
           nativeQuery = true)
    Optional<Task> findTaskByTaskListIdAndTaskId(@Param("id") UUID id,
                                                 @Param("taskId") UUID taskId);

    @Modifying
    @Query(value = "DELETE FROM task t WHERE t.task_list_id = :id AND t.id = :taskId",
           nativeQuery = true)
    void deleteTaskByTaskListIdAndTaskId(UUID id, UUID taskId);
}
