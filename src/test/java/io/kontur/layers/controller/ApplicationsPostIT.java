package io.kontur.layers.controller;

import io.kontur.layers.dto.ApplicationCreateDto;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /apps")
public class ApplicationsPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("pigeon")
    public void createApplication() throws Exception {
        //GIVEN
        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        //WHEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.id", is(applicationDto.getId().toString())));
        assertThat(json, hasJsonPath("$.showAllPublicLayers", is(applicationDto.isShowAllPublicLayers())));
        assertThat(json, hasJsonPath("$.isPublic", is(applicationDto.isPublic())));

        json = mockMvc.perform(get("/apps/" + applicationDto.getId())
                        .param("defaultCollections", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasNoJsonPath("$.defaultCollections"));
    }

    @Test
    @WithMockUser("pigeon")
    public void createApplicationWithPublicAppLayers() throws Exception {
        //GIVEN
        Layer layer1 = buildLayerN(1);
        testDataMapper.insertLayer(layer1);
        Layer layer2 = buildLayerN(2);
        testDataMapper.insertLayer(layer2);

        ApplicationCreateDto applicationDto = buildApplicationCreateDto();

        applicationDto.getLayers().add(buildApplicationLayerDto(layer1.getPublicId(), 1));
        applicationDto.getLayers().add(buildApplicationLayerDto(layer2.getPublicId(), 2));

        //WHEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.id", is(applicationDto.getId().toString())));
        assertThat(json, hasJsonPath("$.showAllPublicLayers", is(applicationDto.isShowAllPublicLayers())));
        assertThat(json, hasJsonPath("$.isPublic", is(applicationDto.isPublic())));

        json = mockMvc.perform(get("/apps/" + applicationDto.getId())
                        .param("includeDefaultCollections", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(json, hasJsonPath("$.defaultCollections", hasSize(2)));
        assertThat(json, hasJsonPath("$.defaultCollections[*].id",
                containsInAnyOrder(layer1.getPublicId(), layer2.getPublicId())));
    }

    @Test
    @WithMockUser("pigeon")
    public void createApplicationWithPartiallyPublicAppLayers() throws Exception {
        //GIVEN
        Layer layer1 = buildLayerN(1);
        testDataMapper.insertLayer(layer1);
        Layer layer2 = buildLayerN(2);
        layer2.setPublic(false);
        testDataMapper.insertLayer(layer2);

        ApplicationCreateDto applicationDto = buildApplicationCreateDto();

        applicationDto.getLayers().add(buildApplicationLayerDto(layer1.getPublicId(), 1));
        applicationDto.getLayers().add(buildApplicationLayerDto(layer2.getPublicId(), 2));

        //WHEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.msg", containsString(layer2.getPublicId())));

        mockMvc.perform(get("/apps/" + applicationDto.getId())
                        .param("includeDefaultCollections", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("pigeon")
    public void createApplicationWithNonVisibleAppLayers() throws Exception {
        //GIVEN
        Layer layer1 = buildLayerN(1);
        testDataMapper.insertLayer(layer1);
        Layer layer2 = buildLayerN(2);
        layer2.setVisible(false);
        testDataMapper.insertLayer(layer2);

        ApplicationCreateDto applicationDto = buildApplicationCreateDto();

        applicationDto.getLayers().add(buildApplicationLayerDto(layer1.getPublicId(), 1));
        applicationDto.getLayers().add(buildApplicationLayerDto(layer2.getPublicId(), 2));

        //WHEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.msg", containsString(layer2.getPublicId())));

        mockMvc.perform(get("/apps/" + applicationDto.getId())
                        .param("includeDefaultCollections", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("pigeon")
    public void cantCreateApplicationTwice() throws Exception {
        //GIVEN
        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        //WHEN
        //THEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertThat(json, hasJsonPath("$.msg", is("Application with such id already exists")));
    }

    @Test
    @WithMockUser("pigeon")
    public void createApplicationIdCantBeNull() throws Exception {
        //GIVEN
        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        applicationDto.setId(null);
        //WHEN
        String json = mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.id.msg", is("must not be null")));
    }

    @Test
    public void testAuthorization() throws Exception {
        //GIVEN
        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        //WHEN
        mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}
