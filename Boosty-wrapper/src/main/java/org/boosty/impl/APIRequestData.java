package org.boosty.impl;

import org.boosty.utils.JsonUtil;

public interface APIRequestData {

    default String toJson() {
        return JsonUtil.toJson(this);
    }
}