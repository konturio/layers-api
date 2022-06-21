package io.kontur.layers.controller;

import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static io.kontur.layers.test.CustomMatchers.url;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GET /layers/")
public class LandingPageGetIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("response should match spec")
    public void testGetCollection() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").doesNotExist())
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.links[?(@.rel=='service-desc' && @.type=='application/vnd.oai.openapi;version=3.0')].href").value(
                        contains(url(BASE_URL + "/v3/api-docs.yaml"))))
                .andExpect(jsonPath("$.links[?(@.rel=='self' && @.type=='application/json')].href").value(
                        contains(url(BASE_URL + "/"))))
                .andExpect(jsonPath("$.links[?(@.rel=='conformance' && @.type=='application/json')].href").value(
                        contains(url(BASE_URL + "/conformance"))))
                .andExpect(jsonPath("$.links[?(@.rel=='data' && @.type=='application/json')].href").value(
                        contains(url(BASE_URL + "/collections"))));
    }

}