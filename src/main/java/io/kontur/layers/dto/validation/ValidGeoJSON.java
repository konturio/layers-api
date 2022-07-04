package io.kontur.layers.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = GeoJSONValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidGeoJSON {

    String message() default "Geometry is not topologically valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
