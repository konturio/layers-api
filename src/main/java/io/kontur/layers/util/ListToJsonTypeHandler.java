package io.kontur.layers.util;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kontur.layers.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class ListToJsonTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                List<String> parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, JsonUtil.writeJson(parameter), Types.OTHER);
    }


    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json != null ? JsonUtil.readJson(json, new TypeReference<List<String>>() {}) : Collections.emptyList();
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json != null ? JsonUtil.readJson(json, new TypeReference<List<String>>() {}) : Collections.emptyList();
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json != null ? JsonUtil.readJson(json, new TypeReference<List<String>>() {}) : Collections.emptyList();
    }
}
