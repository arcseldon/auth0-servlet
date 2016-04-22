package com.auth0.example;

import javax.servlet.http.HttpServletRequest;

public class Helpers {

    public static String buildUrlStr(final HttpServletRequest request) {
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
