    package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static io.kontur.layers.test.TestDataHelper.buildApplication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DELETE /apps/{id}")
public class ApplicationsDeleteIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("owner_1")
    public void deleteApplication() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);

        //WHEN
        mockMvc.perform(delete("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        //THEN
        mockMvc.perform(get("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser("pigeon")
    public void notFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(delete("/apps/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAuthentication() throws Exception {
        //GIVEN
        Application application = buildApplication(1);
        testDataMapper.insertApplication(application);
        //WHEN
        //THEN
        mockMvc.perform(delete("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(delete("/apps/" + application.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(application)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}
