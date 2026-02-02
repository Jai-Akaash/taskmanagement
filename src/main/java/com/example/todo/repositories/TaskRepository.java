package com.example.todo.repositories;

import com.example.todo.enums.Priority;
import com.example.todo.enums.Status;
import com.example.todo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>
         {

                 List<Task> findByStatus(Status status);

                 List<Task> findByStatusIn(List<Status> statuses);

                 List<Task> findByPriority(Priority priority);

                 List<Task> findByPriorityIn(List<Priority> priorities);

                 List<Task> findByAssignedToId(UUID userId);

                 List<Task> findByAssignedToIsNull();

                 List<Task> findByCreatedById(UUID userId);

                 @Query("SELECT t FROM Task t " +
                         "WHERE t.dueDate < CURRENT_DATE " +
                         "AND t.status != com.example.TaskManagement.enums.TaskStatus.COMPLETED " +
                         "AND t.status != com.example.TaskManagement.enums.TaskStatus.CANCELLED " +
                         "ORDER BY t.dueDate ASC")
                 List<Task> findOverdueTasks();

                 @Query("SELECT t FROM Task t " +
                         "WHERE CAST(t.createdAt AS date) BETWEEN :startDate AND :endDate " +
                         "ORDER BY t.createdAt DESC")
                 List<Task> findTasksCreatedBetween(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

                 @Query("SELECT t FROM Task t " +
                         "WHERE CAST(t.updatedAt AS date) BETWEEN :startDate AND :endDate " +
                         "ORDER BY t.updatedAt DESC")
                 List<Task> findTasksUpdatedBetween(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

                 @Query("SELECT t FROM Task t " +
                         "WHERE t.status = com.example.TaskManagement.enums.TaskStatus.COMPLETED " +
                         "AND CAST(t.updatedAt AS date) BETWEEN :startDate AND :endDate " +
                         "ORDER BY t.updatedAt DESC")
                 List<Task> findCompletedTasksBetween(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);


                 @Query(value = "SELECT DISTINCT t.* FROM tasks t " +
                         "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                         "WHERE tt.tag IN :tags " +
                         "GROUP BY t.id " +
                         "HAVING COUNT(DISTINCT tt.tag) = :tagCount " +
                         "ORDER BY t.created_at DESC",
                         nativeQuery = true)
                 List<Task> findTasksWithAllTags(@Param("tags") List<String> tags,
                                                 @Param("tagCount") Long tagCount);

                 @Query(value = "SELECT DISTINCT t.* FROM tasks t " +
                         "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                         "WHERE tt.tag IN :tags " +
                         "ORDER BY t.created_at DESC",
                         nativeQuery = true)
                 List<Task> findTasksWithAnyTag(@Param("tags") List<String> tags);

                 List<Task> findByStatusAndPriority(Status status, Priority priority);

                 List<Task> findByStatusAndAssignedToId(Status status, UUID userId);

                 List<Task> findByPriorityAndAssignedToId(Priority priority, UUID userId);

                 @Query("SELECT t FROM Task t " +
                         "WHERE t.status = com.example.TaskManagement.enums.TaskStatus.OPEN " +
                         "AND (t.priority = com.example.TaskManagement.enums.Priority.HIGH " +
                         "OR t.priority = com.example.TaskManagement.enums.Priority.CRITICAL) " +
                         "ORDER BY t.dueDate ASC")
                 List<Task> findUrgentOpenTasks();

                 @Query("SELECT t FROM Task t ORDER BY t.priority DESC, t.createdAt DESC")
                 List<Task> findAllOrderByPriorityDesc();

                 @Query("SELECT t FROM Task t ORDER BY t.dueDate ASC, t.priority DESC")
                 List<Task> findAllOrderByDueDateAsc();

                 @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
                 List<Task> findAllOrderByCreatedAtDesc();

                 @Query("SELECT t FROM Task t ORDER BY t.status ASC, t.priority DESC")
                 List<Task> findAllOrderByStatus();

                 @Query("SELECT t FROM Task t ORDER BY t.status ASC, t.priority DESC, t.dueDate ASC")
                 List<Task> findAllOrderByStatusAndPriority();

                 long countByStatus(Status status);

                 @Query("SELECT COUNT(t) FROM Task t " +
                         "WHERE t.dueDate < CURRENT_DATE " +
                         "AND t.status != com.example.TaskManagement.enums.TaskStatus.COMPLETED " +
                         "AND t.status != com.example.TaskManagement.enums.TaskStatus.CANCELLED")
                 long countOverdueTasks();

                 long countByAssignedToId(UUID userId);

                 long countByCreatedById(UUID userId);
             }

