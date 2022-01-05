package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DELETE /collections/{id}")
public class CollectionsDeleteIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("should delete a layer")
    @WithMockUser("owner_1")
    public void testDeleteCollection() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        String id = layer.getPublicId();

        //WHEN
        mockMvc.perform(delete("/collections/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        //THEN
        mockMvc.perform(get("/collections/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("can not delete other user's layer")
    @WithMockUser("pigeon")
    public void testCannotDeleteIfNotOwner() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        String id = layer.getPublicId();
        ReflectionTestUtils.setField(layer, "isPublic", true);
        testDataMapper.insertLayer(layer);

        //WHEN
        mockMvc.perform(delete("/collections/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        //THEN
        mockMvc.perform(get("/collections/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("can not not delete if not authorized")
    public void testCannotDeleteIfNotAuthorized() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        String id = layer.getPublicId();

        //WHEN
        mockMvc.perform(delete("/collections/" + id))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}