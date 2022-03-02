package io.kontur.layers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreateDto extends ApplicationUpdateDto {

    @NotNull
    private UUID id;
}
