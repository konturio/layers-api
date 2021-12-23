package io.kontur.layers.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Bbox {

    private List<BigDecimal> bbox;

    public Bbox(String value) {
        this.bbox = valueOf(value);
    }

    public static List<BigDecimal> valueOf(String value) {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        List<BigDecimal> coords = new ArrayList<>();
        Arrays.stream(value.split(",")).forEach(v -> coords.add(new BigDecimal(v)));
        return coords;
    }

    public List<BigDecimal> getBbox() {
        if (bbox == null) {
            bbox = new ArrayList<>();
        }
        return bbox;
    }

    @Override
    public String toString() {
        return bbox.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
