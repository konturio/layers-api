package io.kontur.layers.controller.validation;


import io.kontur.layers.dto.Bbox;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BboxValidator implements ConstraintValidator<ValidBbox, Bbox> {
    @Override
    public void initialize(ValidBbox value) {
    }

    @Override
    public boolean isValid(Bbox bbox, ConstraintValidatorContext ctx) {
        if (bbox == null || bbox.getBbox() == null || bbox.getBbox().isEmpty()) {
            return true;
        }
        if (bbox.getBbox().size() != 4 && bbox.getBbox().size() != 6) {
            ctx.buildConstraintViolationWithTemplate("bbox should be provided as 4 or 6 numbers.")
                    .addConstraintViolation();
            return false;
        }
        var b = (bbox.getBbox().size() == 4
                && checkLon(bbox.getBbox().get(0))
                && checkLat(bbox.getBbox().get(1))
                && checkLon(bbox.getBbox().get(2))
                && checkLat(bbox.getBbox().get(3)))
                || (bbox.getBbox().size() == 6
                && checkLon(bbox.getBbox().get(0))
                && checkLat(bbox.getBbox().get(1))
                && checkLon(bbox.getBbox().get(3))
                && checkLat(bbox.getBbox().get(4)));
        if (!b) {
            ctx.buildConstraintViolationWithTemplate("bbox coordinates doesn't conform to WGS84 coordinate system")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean checkLat(BigDecimal lat) {
        return BigDecimal.valueOf(90).compareTo(lat) >= 0 && BigDecimal.valueOf(-90).compareTo(lat) <= 0;
    }

    private boolean checkLon(BigDecimal lon) {
        return BigDecimal.valueOf(180).compareTo(lon) >= 0 && BigDecimal.valueOf(-180).compareTo(lon) <= 0;
    }
}
