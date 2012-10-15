// Copyright (C) 2012 The Android Open Source Project
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
import java.net.HttpURLConnection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.gitblit.SyndicationFilter;
import com.google.gerrit.httpd.WebSession;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class WrappedSyndicationFilter extends SyndicationFilter {
  
  private final Provider<WebSession> webSession;
  
  @Inject
  public WrappedSyndicationFilter(final Provider<WebSession> webSession) {
    super();
    
    this.webSession = webSession;
  }
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (webSession.get().isSignedIn() || httpRequest.getHeader("Authorization") != null) {
      request.setAttribute("gerrit-username", webSession.get().getCurrentUser()
          .getUserName());
      request.setAttribute("gerrit-token", webSession.get().getToken());
      super.doFilter(request, response, chain);
    } else {
      httpResponse.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
      httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Gerrit Code Review\"");
    }
  }
}
