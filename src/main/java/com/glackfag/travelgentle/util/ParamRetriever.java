package com.glackfag.travelgentle.util;

import java.util.HashMap;
import java.util.Map;

public class ParamRetriever {
    public static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> params = new HashMap<>();

        String[] parts = url.split("\\?");
        if (parts.length > 1) {
            String[] queryParameters = parts[1].split("&");

            for (String queryParameter : queryParameters) {
                String[] keyValue = queryParameter.split("=");

                if (keyValue.length == 2)
                    params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }
}
