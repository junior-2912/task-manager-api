package com.junior.todomanager.dto.request;

import com.junior.todomanager.enums.TaskCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestPatchDto {
    // Usando @Pattern para invalidar campos em branco ou somente espaços e deixar passar nulos ou omissões de valor.
    // O valor nulo ou omitido teria como resultado preservar o valor existente do atributo persistido.
    @Pattern(regexp = "(?s).*\\S.*")
    @Size(max = 100)
    private String title;
    @Pattern(regexp = "(?s).*\\S.*")
    @Size(max = 300)
    private String description;
    private LocalDateTime dueDate;
    private TaskCategory taskCategory;
}
