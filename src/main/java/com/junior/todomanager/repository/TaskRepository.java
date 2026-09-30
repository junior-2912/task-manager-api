package com.junior.todomanager.repository;

import com.junior.todomanager.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// TODO - Implement specification
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
