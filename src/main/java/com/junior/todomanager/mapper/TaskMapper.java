package com.junior.todomanager.mapper;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskResponseDto;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    Task requestPostToTaskEntity(TaskRequestPostDto taskRequestPostDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    Task requestPutToTaskEntity(TaskRequestPutDto taskRequestPutDto);

    @Mapping(source = "taskStatus", target = "status")
    @Mapping(source = "taskCategory", target = "category")
    TaskResponseDto entityToTaskResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(TaskRequestPutDto taskRequestPutDto, @MappingTarget Task entity);
}
