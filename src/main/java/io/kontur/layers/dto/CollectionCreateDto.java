package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CollectionCreateDto extends CollectionUpdateDto {

    @JsonProperty("id")
    @NotNull
    private String id;
}
