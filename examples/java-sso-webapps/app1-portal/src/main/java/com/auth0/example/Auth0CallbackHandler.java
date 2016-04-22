package com.auth0.example;

import com.auth0.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Custom Auth0ServletCallback to handle SSO interaction
 * both for portal logins and handling partner site logins
 */
public class Auth0CallbackHandler extends Auth0ServletCallback {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (isValidRequest(req, resp)) {
            try {
                final Tokens tokens = fetchTokens(req);
                final Auth0User user = fetchUser(tokens);
                store(tokens, user, req);
                final NonceStorage nonceStorage = new RequestNonceStorage(req);
                nonceStorage.setState(null);
                onSuccess(req, resp);
            } catch (IllegalArgumentException ex) {
                onFailure(req, resp, ex);
            } catch (IllegalStateException ex) {
                onFailure(req, resp, ex);
            }
        } else {
            onFailure(req, resp, new IllegalStateException("Invalid state or error"));
        }
    }

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final String externalReturnUrl = (String) req.getAttribute("externalReturnUrl");
        if (externalReturnUrl != null) {
            resp.sendRedirect(externalReturnUrl);
        } else {
            resp.sendRedirect(req.getContextPath() + redirectOnSuccess);
        }
    }

    @Override
    protected void onFailure(HttpServletRequest req, HttpServletResponse resp, Exception ex) throws ServletException, IOException {
        ex.printStackTrace();
        final boolean hasQueryParams = req.getQueryString() != null;
        final String externalReturnUrl = (String) req.getAttribute("externalReturnUrl");
        if (externalReturnUrl != null) {
            //TODO - improve error reporting
            final String errorQueryParam = "error_description=Callback_Failure";
            final String redirectExternalOnFailLocation = hasQueryParams ?
                    externalReturnUrl + "?" + req.getQueryString() + "&" + errorQueryParam :
                    externalReturnUrl + "?" + errorQueryParam;
            resp.sendRedirect(redirectExternalOnFailLocation);
        } else {
            String redirectOnFailLocation = req.getContextPath() + redirectOnFail;
            if (hasQueryParams) {
                redirectOnFailLocation = redirectOnFailLocation + "?" + req.getQueryString();
            }
            resp.sendRedirect(redirectOnFailLocation);
        }
    }

    @Override
    protected boolean isValidState(HttpServletRequest req) {
        final String stateValue = req.getParameter("state");
        try {
            final Map<String, String> pairs = Helpers.splitQuery(stateValue);
            final String externalReturnUrl = pairs.get("eru");
            final String state = pairs.get("nonce");
            if (externalReturnUrl != null) {
                req.setAttribute("externalReturnUrl", externalReturnUrl);
            }
            final boolean trusted = externalReturnUrl == null || Helpers.isTrustedExternalReturnUrl(externalReturnUrl);
            return state != null && state.equals(getNonceStorage(req).getState()) && trusted;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }


}
