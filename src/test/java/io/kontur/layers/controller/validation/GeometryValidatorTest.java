package io.kontur.layers.controller.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryValidatorTest {

    private final GeometryValidator validator = new GeometryValidator();

    @Test
    void isValid() {
        assertTrue(validator.isValid("POINT(0 0)", null));
        assertTrue(validator.isValid("MULTIPOINT((0 0),(2.3 34.5))", null));
        assertTrue(validator.isValid("MULTIPOINT((0 0),(2.3 34.5),(2 3))", null));
        assertTrue(validator.isValid("MULTIPOINT((0 0),(2.3 34.5),(2 -3.40))", null));
        assertTrue(validator.isValid("MULTIPOINT((0 0),(1 2))", null));
        assertTrue(validator.isValid("MULTIPOINT(0 0)", null));

        assertFalse(validator.isValid("POLYGON((0 0,4 0,4 4,0 4,0 0),(1 1, 2 1, 2 2, 1 2,1 1))", null));
        assertFalse(validator.isValid("POINT((0 0),(1,1))", null));
        assertFalse(validator.isValid("POINT(0, 0)", null));
        assertFalse(validator.isValid("xxx", null));
    }
}