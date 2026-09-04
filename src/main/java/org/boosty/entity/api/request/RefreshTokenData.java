package org.boosty.entity.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.boosty.impl.APIRequestData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
@AllArgsConstructor
public class RefreshTokenData implements APIRequestData {

    private final String deviceId;
    private final String refreshToken;

    @Override
    public String toJson() {
        return "device_id=" + encode(deviceId)
                + "&device_os=web"
                + "&grant_type=refresh_token"
                + "&refresh_token=" + encode(refreshToken);
    }

    private static String encode(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
