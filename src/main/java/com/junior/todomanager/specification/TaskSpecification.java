package com.junior.todomanager.specification;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.enums.TaskCategory;
import com.junior.todomanager.enums.TaskStatus;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class TaskSpecification {
    public static Specification<Task> hasStatus(TaskStatus taskStatus) {
        return (root, query, builder) ->
                builder.equal(root.get("taskStatus"), taskStatus);
    }

    public static Specification<Task> hasCategory(TaskCategory category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("taskCategory"), category);
    }

    public static Specification<Task> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }

    public static Specification<Task> overDue() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.get("dueDate"), LocalDateTime.now()),
                        criteriaBuilder.notEqual(root.get("taskStatus"), TaskStatus.FINISHED),
                        criteriaBuilder.notEqual(root.get("taskStatus"), TaskStatus.CANCELED)
                );
    }
}
