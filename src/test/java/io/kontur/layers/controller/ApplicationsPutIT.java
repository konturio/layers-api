package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.ApplicationCreateDto;
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
import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PUT /apps/{id}")
public class ApplicationsPutIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("owner_1")
    public void updateApplication() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);

        //WHEN
        application.setIsPublic(false);
        application.setShowAllPublicLayers(false);
        String response = mockMvc.perform(put("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(application)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.id", is(application.getId().toString())));
        assertThat(json, hasJsonPath("$.showAllPublicLayers", is(application.getShowAllPublicLayers())));
        assertThat(json, hasJsonPath("$.isPublic", is(application.getIsPublic())));
    }

    @Test
    @WithMockUser("owner_1")
    public void updateApplicationWithPublicAppLayers() throws Exception {
        //GIVEN
        Layer layer1 = buildLayerN(1);
        testDataMapper.insertLayer(layer1);
        Layer layer2 = buildLayerN(2);
        testDataMapper.insertLayer(layer2);

        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);

        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        applicationDto.getLayers().add(buildApplicationLayerDto(layer1.getPublicId(), 1));
        applicationDto.getLayers().add(buildApplicationLayerDto(layer2.getPublicId(), 2));

        //WHEN
        applicationDto.setPublic(false);
        applicationDto.setShowAllPublicLayers(false);
        String json = mockMvc.perform(put("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.id", is(application.getId().toString())));
        assertThat(json, hasJsonPath("$.showAllPublicLayers", is(applicationDto.isShowAllPublicLayers())));
        assertThat(json, hasJsonPath("$.isPublic", is(applicationDto.isPublic())));

        json = mockMvc.perform(get("/apps/" + application.getId())
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
    public void notFound() throws Exception {
        //GIVEN
        ApplicationCreateDto applicationDto = buildApplicationCreateDto();
        //WHEN
        //THEN
        mockMvc.perform(put("/apps/" + applicationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(applicationDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAuthentication() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);
        //WHEN
        //THEN
        mockMvc.perform(put("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(application)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser("pigeon")
    public void testUserIsNotAuthorized() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);
        //WHEN
        //THEN
        mockMvc.perform(put("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(application)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
