package io.kontur.layers.repository.typehandler;

import io.kontur.layers.repository.model.LayersGroupProperties;
import io.kontur.layers.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(LayersGroupProperties.class)
public class LayersGroupPropertiesTypeHandler extends BaseTypeHandler<LayersGroupProperties> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            LayersGroupProperties geoJson,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.writeJson(geoJson));
    }

    private LayersGroupProperties convertJson(String json) {
        return json == null || "{}".equals(json) ? null : JsonUtil.readJson(json, LayersGroupProperties.class);
    }

    @Override
    public LayersGroupProperties getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertJson(rs.getString(columnName));
    }

    @Override
    public LayersGroupProperties getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertJson(rs.getString(columnIndex));
    }

    @Override
    public LayersGroupProperties getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertJson(cs.getString(columnIndex));
    }
}
