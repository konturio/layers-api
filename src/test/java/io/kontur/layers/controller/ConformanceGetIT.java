package io.kontur.layers.controller;

import io.kontur.layers.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GET /conformance")
public class ConformanceGetIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("should work")
    public void testGetCollection() throws Exception {
        mockMvc.perform(get("/conformance"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.conformsTo").value(hasSize(3)))
                .andExpect(jsonPath("$.conformsTo").value(
                        hasItem("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core")))
                .andExpect(jsonPath("$.conformsTo").value(
                        hasItem("http://docs.opengeospatial.org/is/17-069r3/17-069r3.html#_conformance_class_openapi_3_0")))
                .andExpect(jsonPath("$.conformsTo").value(
                        hasItem("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson")));
    }
}