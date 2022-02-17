package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper=true)
public class CollectionCreateDto extends CollectionUpdateDto {

    @JsonProperty("id")
    @Pattern(regexp = "[\\w]*")
    private String id;
}
