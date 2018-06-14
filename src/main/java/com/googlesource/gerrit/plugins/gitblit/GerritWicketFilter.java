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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.Constants;
import com.gitblit.IStoredSettings;
import com.gitblit.manager.IProjectManager;
import com.gitblit.manager.IRepositoryManager;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.wicket.GitBlitWebApp;
import com.gitblit.wicket.GitblitWicketFilter;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.WebSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.gitblit.auth.GerritAuthFilter;

@Singleton
public class GerritWicketFilter extends GitblitWicketFilter {
  private static final Logger log = LoggerFactory
      .getLogger(GerritWicketFilter.class);
  private static final Set<String> RESOURCES_SUFFIXES =
      new ImmutableSet.Builder<String>().add("css", "js", "png", "gif", "ttf",
          "swf", "afm", "eot", "otf", "scss", "svg", "woff").build();

  private final DynamicItem<WebSession> webSession;

  private final GerritAuthFilter gerritAuthFilter;

  private final GerritGitBlitContext gerritGitblitContext;

  @Inject
  public GerritWicketFilter(
      DynamicItem<WebSession> webSession,
      GerritAuthFilter gerritAuthFilter,
      GerritGitBlitContext gerritGitblitContext,
      IStoredSettings settings,
      IRuntimeManager runtimeManager,
      IRepositoryManager repositoryManager,
      IProjectManager projectManager,
      GitBlitWebApp webapp) {
    super(settings, runtimeManager, repositoryManager, projectManager, webapp);
    this.webSession = webSession;
    this.gerritAuthFilter = gerritAuthFilter;
    this.gerritGitblitContext = gerritGitblitContext;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    ServletContext servletContext = filterConfig.getServletContext();
    showGitBlitBanner();
    gerritGitblitContext.init(servletContext);

    try {
      super.init(new CustomFilterConfig(filterConfig));
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void showGitBlitBanner() {
    log.info(Constants.BORDER);
    log.info("            _____  _  _    _      _  _  _");
    log.info("           |  __ \\(_)| |  | |    | |(_)| |");
    log.info("           | |  \\/ _ | |_ | |__  | | _ | |_");
    log.info("           | | __ | || __|| '_ \\ | || || __|");
    log.info("           | |_\\ \\| || |_ | |_) || || || |_");
    log.info("            \\____/|_| \\__||_.__/ |_||_| \\__|");
    String submsg = Constants.getGitBlitVersion();
    int spacing = (Constants.BORDER.length() - submsg.length()) / 2;
    StringBuilder sb = new StringBuilder();
    while (spacing > 0) {
      spacing--;
      sb.append(' ');
    }
    log.info(sb.toString() + submsg);
    log.info("");
    log.info(Constants.BORDER);
  }

  @Override
  protected ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String requestPathInfo = httpServletRequest.getPathInfo();

    if (isStaticResourceRequest(requestPathInfo)) {
      if (!requestPathInfo.startsWith("/static")
          && !requestPathInfo.startsWith("/resources")) {
        httpServletRequest = new StaticHttpServletRequest(httpServletRequest);
      }
      super.doFilter(httpServletRequest, response, chain);
    } else if (gerritAuthFilter.doFilter(webSession, httpServletRequest,
        response, chain)) {
      super.doFilter(httpServletRequest, response, chain);
    }
  }

  private boolean isStaticResourceRequest(String requestPathInfo) {
    return RESOURCES_SUFFIXES.contains(getResourceSuffix(requestPathInfo)
        .toLowerCase());
  }

  private String getResourceSuffix(String requestPathInfo) {
    int requestPathLastDot = requestPathInfo.lastIndexOf('.');
    if (requestPathLastDot < 0) {
      return "";
    }
    return requestPathInfo.substring(requestPathLastDot + 1);
  }

  class CustomFilterConfig implements FilterConfig {
    private final HashMap<String, String> gitBlitParams = getGitblitInitParams();
    private FilterConfig parentFilterConfig;

    private HashMap<String, String> getGitblitInitParams() {
      HashMap<String, String> props = new HashMap<>();
      props.put("filterMappingUrlPattern", "/*");
      props.put("ignorePaths", "pages/,feed/");
      return props;
    }

    public CustomFilterConfig(FilterConfig parent) {
      this.parentFilterConfig = parent;
    }

    @Override
    public String getFilterName() {
      return "gerritWicketFilter";
    }

    @Override
    public ServletContext getServletContext() {
      return parentFilterConfig.getServletContext();
    }

    @Override
    public String getInitParameter(String paramString) {
      return gitBlitParams.get(paramString);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
      return new Vector<>(gitBlitParams.keySet()).elements();
    }

    class ParamEnum implements Enumeration<String> {
      Vector<String> items;
      Iterator<String> iter;

      public ParamEnum(Vector<String> items) {
        this.items = items;
        this.iter = this.items.iterator();
      }

      @Override
      public boolean hasMoreElements() {
        return iter.hasNext();
      }

      @Override
      public String nextElement() {
        return iter.next();
      }
    }
  }
}
