package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Information about the exception: an error code plus an optional description.
 */
@Schema(description = "Information about the exception: an error code plus an optional description.")
public class Exception {

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    public Exception code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     **/
    @JsonProperty("code")
    @Schema(required = true, description = "")
    @NotNull
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Exception description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @JsonProperty("description")
    @Schema(description = "")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exception exception = (Exception) o;
        return Objects.equals(this.code, exception.code) &&
                Objects.equals(this.description, exception.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description);
    }
}
