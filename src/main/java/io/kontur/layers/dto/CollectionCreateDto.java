package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=true)
public class CollectionCreateDto extends CollectionUpdateDto {

    @JsonProperty("id")
    @NotNull
    @NotEmpty
    private String id;
}
