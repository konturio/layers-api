package io.kontur.layers.controller.validation;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GeometryValidator implements ConstraintValidator<ValidGeometry, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        WKTReader reader = new WKTReader();
        try {
            Geometry geometry = reader.read(value);
            String type = geometry.getGeometryType();
            if (!type.equalsIgnoreCase("point") && !type.equalsIgnoreCase("multipoint")) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
