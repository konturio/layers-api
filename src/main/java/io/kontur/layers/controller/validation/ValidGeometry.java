package io.kontur.layers.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GeometryValidator.class)
@Documented
public @interface ValidGeometry {

    String message() default "geometry has wrong format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
