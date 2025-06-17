package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.ApplicationLayerDto;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /apps/{id}/layers")
public class ApplicationLayersPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("owner_1")
    public void addLayerToApplication() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);
        ApplicationLayerDto dto = buildApplicationLayerDto(layer.getPublicId(), 1);

        //WHEN
        String json = mockMvc.perform(post("/apps/" + application.getId() + "/layers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        DocumentContext dc = JsonPath.parse(json);
        assertThat(dc, hasJsonPath("$.id", is(application.getId().toString())));
        assertThat(dc, hasJsonPath("$.defaultCollections", hasSize(1)));
        assertThat(dc, hasJsonPath("$.defaultCollections[0].id", is(layer.getPublicId())));
    }
}
