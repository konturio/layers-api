package io.kontur.layers.repository.typehandler;

import io.kontur.layers.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.wololo.geojson.Geometry;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Geometry.class)
public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            Geometry geoJson,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.writeJson(geoJson));
    }

    private Geometry convertJson(String json) {
        return json == null ? null : JsonUtil.readJson(json, Geometry.class);
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertJson(rs.getString(columnName));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertJson(rs.getString(columnIndex));
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertJson(cs.getString(columnIndex));
    }
}
