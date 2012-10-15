package com.googlesource.gerrit.plugins.gitblit.auth;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gitblit.GitBlit;
import com.gitblit.models.UserModel;
import com.google.gerrit.httpd.WebSession;
import com.google.inject.Provider;

public class GerritAuthFilter {

  /**
   * Returns the user making the request, if the user has authenticated.
   * 
   * @param httpRequest
   * @return user
   */
  public UserModel getUser(HttpServletRequest httpRequest) {
    UserModel user = null;
    String username = (String) httpRequest.getAttribute("gerrit-username");
    String token = (String) httpRequest.getAttribute("gerrit-token");

    if (token == null || username == null) {
      return null;
    }

    user =
        GitBlit.self().authenticate(username,
            (GerritToGitBlitUserService.SESSIONAUTH + token).toCharArray());
    if (user != null) {
      return user;
    }

    return null;
  }

  public boolean doFilter(final Provider<WebSession> webSession,
      ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (webSession.get().isSignedIn()
        || httpRequest.getHeader("Authorization") != null) {
      request.setAttribute("gerrit-username", webSession.get().getCurrentUser()
          .getUserName());
      request.setAttribute("gerrit-token", webSession.get().getToken());
      return true;
    } else {
      httpResponse.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
      httpResponse.setHeader("WWW-Authenticate",
          "Basic realm=\"Gerrit Code Review\"");
      return false;
    }
  }

}
