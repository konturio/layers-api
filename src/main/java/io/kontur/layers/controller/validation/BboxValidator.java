package io.kontur.layers.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.List;

public class BboxValidator implements ConstraintValidator<ValidBbox, List<BigDecimal>> {

    @Override
    public void initialize(ValidBbox value) {
    }

    @Override
    public boolean isValid(List<BigDecimal> bbox, ConstraintValidatorContext ctx) {
        if (bbox == null || bbox.isEmpty()) {
            return true;
        }
        if (bbox.size() != 4 && bbox.size() != 6) {
            ctx.buildConstraintViolationWithTemplate("bbox should be provided as 4 or 6 numbers.")
                    .addConstraintViolation();
            return false;
        }
        var b = (bbox.size() == 4
                && checkLon(bbox.get(0))
                && checkLat(bbox.get(1))
                && checkLon(bbox.get(2))
                && checkLat(bbox.get(3)))
                || (bbox.size() == 6
                && checkLon(bbox.get(0))
                && checkLat(bbox.get(1))
                && checkLon(bbox.get(3))
                && checkLat(bbox.get(4)));
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
