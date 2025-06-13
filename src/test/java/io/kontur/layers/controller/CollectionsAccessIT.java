package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("access management for collections")
public class CollectionsAccessIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("owner can grant and revoke access")
    @WithMockUser("owner_1")
    public void ownerCanGrantAndRevoke() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        testDataMapper.insertLayer(layer);

        mockMvc.perform(get("/collections/" + layer.getPublicId()).with(user("pigeon")))
                .andDo(print())
                .andExpect(status().isNotFound());

        //WHEN
        mockMvc.perform(put("/collections/" + layer.getPublicId() + "/access/pigeon"))
                .andDo(print())
                .andExpect(status().isNoContent());

        //THEN
        mockMvc.perform(get("/collections/" + layer.getPublicId()).with(user("pigeon")))
                .andDo(print())
                .andExpect(status().isOk());

        //WHEN revoke
        mockMvc.perform(delete("/collections/" + layer.getPublicId() + "/access/pigeon"))
                .andDo(print())
                .andExpect(status().isNoContent());

        //THEN access removed
        mockMvc.perform(get("/collections/" + layer.getPublicId()).with(user("pigeon")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("non owner cannot grant access")
    @WithMockUser("stranger")
    public void notOwnerForbidden() throws Exception {
        Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        testDataMapper.insertLayer(layer);

        mockMvc.perform(put("/collections/" + layer.getPublicId() + "/access/pigeon"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("superadmin can grant access")
    @WithMockUser(username = "admin", roles = {"SUPERADMIN"})
    public void superAdminAllowed() throws Exception {
        Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        testDataMapper.insertLayer(layer);

        mockMvc.perform(put("/collections/" + layer.getPublicId() + "/access/pigeon"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
