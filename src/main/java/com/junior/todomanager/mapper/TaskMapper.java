package com.junior.todomanager.mapper;

import com.junior.todomanager.domain.Task;
import com.junior.todomanager.dto.TaskRequestPostDto;
import com.junior.todomanager.dto.TaskRequestPutDto;
import com.junior.todomanager.dto.TaskResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    Task requestPostToTaskEntity(TaskRequestPostDto taskRequestPostDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    Task requestPutToTaskEntity(TaskRequestPutDto taskRequestPutDto);

    // TODO - O status esta retornando null pois o metodo aqui nao cria uma nova instancia de Task,
    //  o construtor da classe Task nao faz a atribucao de status = PENDING.
    @Mapping(source = "taskStatus", target = "status")
    @Mapping(source = "taskCategory", target = "category")
    TaskResponseDto entityToTaskResponse(Task task);

}
