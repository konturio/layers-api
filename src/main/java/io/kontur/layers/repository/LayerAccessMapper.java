package io.kontur.layers.repository;

import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

@Mapper
@Timed(value = "db.query", histogram = true)
public interface LayerAccessMapper {

    @Timed(value = "db.query", histogram = true)
    @Insert("INSERT INTO layers_access (layer_id, user_name) VALUES (#{layerId}, #{userName}) ON CONFLICT DO NOTHING")
    void grantAccess(@Param("layerId") String layerId, @Param("userName") String userName);

    @Timed(value = "db.query", histogram = true)
    @Delete("DELETE FROM layers_access WHERE layer_id = #{layerId} AND user_name = #{userName}")
    void revokeAccess(@Param("layerId") String layerId, @Param("userName") String userName);
}
