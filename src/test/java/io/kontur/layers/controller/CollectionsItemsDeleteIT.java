package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static io.kontur.layers.test.TestDataHelper.buildPointN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Delete /collections/{id}/items/{itemId}")
public class CollectionsItemsDeleteIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("should delete item")
    @WithMockUser("owner_1")
    public void testDeleteItem() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        LayerFeature feature1 = buildPointN(1);
        String featureId = testDataMapper.insertFeature(id, feature1);
        testDataMapper.insertFeature(id, buildPointN(2));

        //WHEN
        mockMvc.perform(delete("/collections/" + layer.getPublicId() + "/items/" + featureId))
                .andDo(print())
                .andExpect(status().isNoContent());

        //THEN
        mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items/" + featureId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not be able to delete items from layers that I dont own")
    @WithMockUser("pigeon")
    public void testDeleteItemDontOwn() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        LayerFeature feature1 = buildPointN(1);
        String featureId = testDataMapper.insertFeature(id, feature1);
        testDataMapper.insertFeature(id, buildPointN(2));

        //WHEN
        mockMvc.perform(delete("/collections/" + layer.getPublicId() + "/items/" + featureId))
                .andDo(print())
                .andExpect(status().isNotFound());

        //THEN
    }

    @Test
    @DisplayName("restrict unauthorized access")
    public void testAuthorization() throws Exception {
        //GIVEN
        //WHEN
        mockMvc.perform(delete("/collections/whatever/items/xxx"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
    }
}