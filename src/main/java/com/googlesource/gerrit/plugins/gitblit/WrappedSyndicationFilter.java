// Copyright (C) 2014 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.gitblit;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.gitblit.manager.IAuthenticationManager;
import com.gitblit.manager.IProjectManager;
import com.gitblit.manager.IRepositoryManager;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.models.UserModel;
import com.gitblit.servlet.SyndicationFilter;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.WebSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.gitblit.auth.GerritAuthFilter;

@Singleton
public class WrappedSyndicationFilter extends SyndicationFilter {
  private final GerritAuthFilter gerritAuthFilter;
  private final DynamicItem<WebSession> webSession;

  static class SyndicationHttpServletRequest extends HttpServletRequestWrapper {
    public SyndicationHttpServletRequest(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getServletPath() {
      return super.getServletPath() + "/feed";
    }
  }

  @Inject
  public WrappedSyndicationFilter(DynamicItem<WebSession> webSession,
      GerritAuthFilter gerritAuthFilter,
      IRuntimeManager runtimeManager,
      IAuthenticationManager authenticationManager,
      IRepositoryManager repositoryManager,
      IProjectManager projectManager) {
    super(runtimeManager, authenticationManager, repositoryManager, projectManager);
    this.webSession = webSession;
    this.gerritAuthFilter = gerritAuthFilter;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    if (gerritAuthFilter.doFilter(webSession, request, response)) {
      super.doFilter(new SyndicationHttpServletRequest(
          (HttpServletRequest) request), response, chain);
    }
  }

  @Override
  protected UserModel getUser(HttpServletRequest httpRequest) {
    UserModel userModel = gerritAuthFilter.getUser(httpRequest);
    if (userModel == null) {
      return super.getUser(httpRequest);
    }
    return userModel;
  }
}
