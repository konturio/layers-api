package io.kontur.layers.dto.validation;

import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GeoJSONValidator implements ConstraintValidator<ValidGeoJSON, GeoJSON> {

    private final GeoJSONReader geoJSONReader = new GeoJSONReader();

    @Override
    public void initialize(ValidGeoJSON constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(GeoJSON geom, ConstraintValidatorContext context) {
        if (geom == null) {
            return true;
        }
        if (geom instanceof FeatureCollection) {
            return validateFeatureCollection((FeatureCollection) geom);
        } else if (geom instanceof Feature) {
            return validateFeature((Feature) geom);
        } else {
            return validateGeometry(geom);
        }
    }

    private boolean validateFeatureCollection(FeatureCollection fc) {
        for (Feature feature : fc.getFeatures()) {
            if (!validateFeature(feature)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateFeature(Feature feature) {
        return validateGeometry(feature.getGeometry());
    }

    private boolean validateGeometry(GeoJSON geom) {
        if (geom == null) {
            return true;
        }
        try {
            return geoJSONReader.read(geom).isValid();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
