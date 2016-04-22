package com.auth0.example;

import com.auth0.Auth0ServletCallback;
import com.auth0.Auth0User;
import com.auth0.Tokens;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom Auth0ServletCallback - once auth0-servlet updated, this becomes redundant
 */
public class Auth0CallbackHandler extends Auth0ServletCallback {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (isValidRequest(req, resp)) {
            try {
                Tokens tokens = fetchTokens(req);
                Auth0User user = fetchUser(tokens);
                store(tokens, user, req);
                onSuccess(req, resp);
            } catch (IllegalArgumentException ex) {
                onFailure(req, resp, ex);
            } catch (IllegalStateException ex) {
                onFailure(req, resp, ex);
            }

        } else {
            onFailure(req, resp, new IllegalStateException("Invalid Request"));
        }
    }

    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Redirect user to home
        resp.sendRedirect(req.getContextPath() + redirectOnSuccess);
    }

    protected void onFailure(HttpServletRequest req, HttpServletResponse resp,
                             Exception ex) throws ServletException, IOException {
        ex.printStackTrace();
        String redirectOnFailLocation = req.getContextPath() + redirectOnFail;
        if (req.getQueryString() != null) {
            redirectOnFailLocation = redirectOnFailLocation + "?" + req.getQueryString();
        }
        resp.sendRedirect(redirectOnFailLocation);
    }

}
