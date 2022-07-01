package io.kontur.layers.dto;

import java.time.OffsetDateTime;

public class DateTimeRange {

    private boolean includeFrom = true;
    private boolean includeTo = true;
    private OffsetDateTime from;
    private OffsetDateTime to;

    public DateTimeRange(final OffsetDateTime from, final OffsetDateTime to) {
        this(from, true, to, true);
    }

    public DateTimeRange(final OffsetDateTime from, final boolean includeFrom, final OffsetDateTime to,
                         final boolean includeTo) {
        this.includeFrom = includeFrom;
        this.includeTo = includeTo;
        this.from = from;
        this.to = to;
    }

    public DateTimeRange(String val) {
        if (val.contains("/")) {
            final String[] split = val.split("/");
            if (!split[0].isEmpty() && !split[0].equals("..")) {
                from = OffsetDateTime.parse(split[0]);
            }
            if (!split[1].isEmpty() && !split[1].equals("..")) {
                to = OffsetDateTime.parse(split[1]);
            }
            if (from == null && to == null) {
                throw new IllegalArgumentException("interval should not be opened from both ends");
            }
            if (from != null && to != null && from.isAfter(to)) {
                throw new IllegalArgumentException("range lower bound must be less than or equal to range upper bound");
            }
        } else {
            final OffsetDateTime dt = OffsetDateTime.parse(val);
            from = dt;
            to = dt;
        }
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public OffsetDateTime getTo() {
        return to;
    }

    public boolean isIncludeFrom() {
        return includeFrom;
    }

    public boolean isIncludeTo() {
        return includeTo;
    }

    @Override
    public String toString() {
        return "DateTimeRange{" +
                "includeFrom=" + includeFrom +
                ", includeTo=" + includeTo +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
