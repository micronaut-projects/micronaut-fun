package io.micronaut.documentation.search.mcp;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringInterpolator {
    private static final Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");

    public static String interpolate(String template, Map<String, Object> values) {
        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object defaultValue = matcher.group(0);
            Object value = values.getOrDefault(key, defaultValue);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value.toString()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
