package md.java.taskhub.task.repository;

import md.java.taskhub.auth.entity.User;
import md.java.taskhub.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByCreatedBy(User createdBy);
    List<Task> findByAssignedTo(User assignedTo);
}
