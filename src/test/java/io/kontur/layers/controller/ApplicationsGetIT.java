package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.test.TestDataHelper.buildApplication;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /apps/{id}")
public class ApplicationsGetIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("owner_1")
    public void getApplication() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);

        //WHEN
        String json = mockMvc.perform(get("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.id", is(application.getId().toString())));
        assertThat(json, hasJsonPath("$.showAllPublicLayers", is(application.getShowAllPublicLayers())));
        assertThat(json, hasJsonPath("$.isPublic", is(application.getIsPublic())));
    }

    @Test
    @WithMockUser("pigeon")
    public void notFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/apps/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAuthenticationNotNeededForPublishedApps() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);
        //WHEN
        //THEN
        mockMvc.perform(get("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAuthenticationIsNeededForNotPublishedApps() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        application.setIsPublic(false);
        testDataMapper.insertApplication(application);
        //WHEN
        //THEN
        mockMvc.perform(get("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser("pigeon")
    public void testUserIsNotAuthorized() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        application.setIsPublic(false);
        testDataMapper.insertApplication(application);

        //WHEN
        String json = mockMvc.perform(get("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        //THEN
    }

}
