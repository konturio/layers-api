package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfClasses {

    @JsonProperty("conformsTo")
    private List<String> conformsTo = new ArrayList<>();

    public ConfClasses conformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
        return this;
    }

    public ConfClasses addConformsToItem(String conformsToItem) {
        this.conformsTo.add(conformsToItem);
        return this;
    }

    @JsonProperty("conformsTo")
    @Schema(required = true, description = "")
    @NotNull
    public List<String> getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfClasses confClasses = (ConfClasses) o;
        return Objects.equals(this.conformsTo, confClasses.conformsTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conformsTo);
    }
}
