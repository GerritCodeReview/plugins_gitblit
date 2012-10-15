package com.googlesource.gerrit.plugins.gitblit.app;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import com.gitblit.GitBlit;
import com.gitblit.models.UserModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.gitblit.auth.GerritToGitBlitUserService;

@Singleton
public class GerritGitBlit extends GitBlit {

  @Inject
  public GerritGitBlit(GerritToGitBlitUserService userService) {
    super(userService);
  }

  public UserModel authenticate(HttpServletRequest request) {
    String user = (String) request.getAttribute("gerrit-username");
    String token = (String) request.getAttribute("gerrit-token");
    if (token == null) {
      return null;
    }

    return GitBlit.self().authenticate(user,
        (GerritToGitBlitUserService.SESSIONAUTH + token).toCharArray());
  }

  @Override
  public InputStream getResourceAsStream(String file)
      throws ResourceStreamNotFoundException {
    String resourceName = "/static/" + file;
    InputStream is = getClass().getResourceAsStream(resourceName);
    if (is == null) {
      throw new ResourceStreamNotFoundException("Cannot access resource "
          + resourceName + " using class-loader " + getClass().getClassLoader());
    }

    return is;
  }
}
