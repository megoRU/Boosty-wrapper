package org.boosty.entity.api.request;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SubscriberRequest extends APIRequest {

    public SubscriberRequest(@NotNull String url, @NotNull String blogName, int limit) {
        super(String.format("%s/v1/blog/%s/subscribers?sort_by=on_time&limit=%d&order=gt", url, URLEncoder.encode(blogName, StandardCharsets.UTF_8), limit), RequestMethod.GET);
    }

    public SubscriberRequest(@NotNull String url, @NotNull String blogName, int limit, long... levelIds) {
        super(String.format("%s/v1/blog/%s/subscribers?sort_by=on_time&limit=%d&level_ids=%s&order=gt",
                        url,
                        URLEncoder.encode(blogName, StandardCharsets.UTF_8),
                        limit,
                        Arrays.stream(levelIds)
                                .mapToObj(String::valueOf)
                                .collect(Collectors.joining("%2C"))),
                RequestMethod.GET);
    }
}