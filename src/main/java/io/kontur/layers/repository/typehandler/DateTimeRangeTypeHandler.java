package io.kontur.layers.repository.typehandler;

import io.kontur.layers.dto.DateTimeRange;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@MappedTypes(DateTimeRange.class)
public class DateTimeRangeTypeHandler extends BaseTypeHandler<DateTimeRange> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            DateTimeRange dateTimeRange,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, toSqlString(dateTimeRange));
    }

    @Override
    public DateTimeRange getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromSqlString(rs.getString(columnName));
    }

    @Override
    public DateTimeRange getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromSqlString(rs.getString(columnIndex));
    }

    @Override
    public DateTimeRange getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromSqlString(cs.getString(columnIndex));
    }

    private static String toSqlString(DateTimeRange dtr) {
        return dtr == null ? null : new StringBuilder()
                .append(dtr.isIncludeFrom() ? "[" : "(")
                .append(dtr.getFrom() == null ? "" : dtr.getFrom().toString())
                .append(",")
                .append(dtr.getTo() == null ? "" : dtr.getTo().toString())
                .append(dtr.isIncludeTo() ? "]" : ")")
                .toString();

    }

    private static DateTimeRange fromSqlString(String s) {
//        ["2020-04-15 15:30:59+03","2020-04-15 15:31:01+03"]
        if (s == null || "empty".equals(s)) {
            return null;
        }
        final String[] split = s.split(",");
        String from = split[0];
        String to = split[1];
        boolean includeFrom = split[0].startsWith("[");
        boolean includeTo = split[1].startsWith("]");
        from = StringUtils.strip(from, "[(\"").replace(" ", "T");
        to = StringUtils.strip(to, "])\"").replace(" ", "T");
        final OffsetDateTime dateFrom = from.isEmpty() ? null : OffsetDateTime.parse(from);
        final OffsetDateTime dateTo = to.isEmpty() ? null : OffsetDateTime.parse(to);
        return dateFrom == null && dateTo == null
                ? null
                : new DateTimeRange(dateFrom, includeFrom, dateTo, includeTo);
    }
}
