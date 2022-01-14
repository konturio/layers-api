package io.kontur.layers.repository.model;

import lombok.Data;

@Data
public class LayersGroupProperties {

    private String name;
    private Boolean isOpened;
    private Boolean mutuallyExclusive;
    private Integer order;

}
