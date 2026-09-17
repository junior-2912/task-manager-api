package com.junior.todomanager.repository;

import com.junior.todomanager.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRespository extends JpaRepository<Task, Long> {
}
