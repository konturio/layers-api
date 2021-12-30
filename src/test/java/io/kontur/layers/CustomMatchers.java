package io.kontur.layers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.net.URLDecoder.decode;

public class CustomMatchers {

    public static UrlMatcher url(String url) {
        return new UrlMatcher(url);
    }

    public static DatePatternMatcher matchesDatePattern(String pattern) {
        return new DatePatternMatcher(pattern);
    }

    public static DatePatternMatcher matchesRfc3339DatePattern() {
        return matchesDatePattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    }

    private static class DatePatternMatcher extends TypeSafeMatcher<String> {

        private final String pattern;
        private final SimpleDateFormat dateFormat;

        public DatePatternMatcher(String pattern) {
            this.pattern = pattern;
            dateFormat = new SimpleDateFormat(pattern);
        }

        @Override
        protected boolean matchesSafely(String s) {
            try {
                dateFormat.parse(s);
                return true;
            } catch (ParseException nfe) {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("must match '" + pattern + "' pattern");
        }
    }

    private static class UrlMatcher extends TypeSafeMatcher<String> {

        private final URL url;

        public UrlMatcher(String url) {
            try {
                this.url = new URL(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected boolean matchesSafely(String u) {
            try {
                final URL url2 = new URL(u);
                boolean b = url.getProtocol().equals(url2.getProtocol())
                        && url.getPort() == url2.getPort()
                        && url.getHost().equals(url2.getHost())
                        && decode(url.getPath(), Charset.defaultCharset())
                        .equals(decode(url2.getPath(), Charset.defaultCharset()));
                if (!b) {
                    return false;
                }
                final String q1 = url.getQuery();
                final String q2 = url2.getQuery();
                var set1 = q1 == null ? Set.of() : Arrays.stream(q1.split("&"))
                        .map(s -> decode(s, Charset.defaultCharset()))
                        .collect(Collectors.toSet());
                var set2 = q2 == null ? Set.of() : Arrays.stream(q2.split("&"))
                        .map(s -> decode(s, Charset.defaultCharset()))
                        .collect(Collectors.toSet());
                return set1.equals(set2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("url '" + url + "'");
        }
    }
}
