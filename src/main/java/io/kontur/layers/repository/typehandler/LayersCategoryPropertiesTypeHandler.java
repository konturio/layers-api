package io.kontur.layers.repository.typehandler;

import io.kontur.layers.repository.model.LayersCategoryProperties;
import io.kontur.layers.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(LayersCategoryProperties.class)
public class LayersCategoryPropertiesTypeHandler extends BaseTypeHandler<LayersCategoryProperties> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            LayersCategoryProperties geoJson,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.writeJson(geoJson));
    }

    private LayersCategoryProperties convertJson(String json) {
        return json == null || "{}".equals(json) ? null : JsonUtil.readJson(json, LayersCategoryProperties.class);
    }

    @Override
    public LayersCategoryProperties getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertJson(rs.getString(columnName));
    }

    @Override
    public LayersCategoryProperties getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertJson(rs.getString(columnIndex));
    }

    @Override
    public LayersCategoryProperties getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertJson(cs.getString(columnIndex));
    }
}
