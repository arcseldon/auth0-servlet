package com.auth0.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Helpers {

    private static final Logger logger = LogManager.getLogger(Helpers.class);

    protected static boolean isTrustedExternalReturnUrl(final String externalReturnUrl) {
        // TODO - move to external config
        return "http://localhost:4000/portal/home".equals(externalReturnUrl) ||
                "http://app2.com:4000/portal/home".equals(externalReturnUrl);
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        if (query == null) {
            throw new NullPointerException("query cannot be null");
        }
        final Map<String, String> query_pairs = new LinkedHashMap<>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String buildUrlStr(final HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request cannot be null");
        }
        final String scheme = request.getScheme();
        final String serverName = request.getServerName();
        final int serverPort = request.getServerPort();
        final StringBuffer url = new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        return url.toString();
    }
}
