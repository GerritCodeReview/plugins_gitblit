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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.wicket.protocol.http.WicketFilter;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.Constants;
import com.gitblit.GitBlit;
import com.gitblit.IStoredSettings;
import com.gitblit.Keys;
import com.google.common.base.Objects;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.httpd.GitWebConfig;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.git.LocalDiskRepositoryManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class GerritWicketFilter extends WicketFilter {

  private Logger logger = LoggerFactory.getLogger(GerritWicketFilter.class);

  private final LocalDiskRepositoryManager repoManager;
  private final Provider<WebSession> webSession;
  private final GitWebConfig gitwebConfig;
  private final GitBlit gitBlit;
  private final String pluginName;
  private Config gerritConfig;

  @Inject
  public GerritWicketFilter(final LocalDiskRepositoryManager repoManager,
      final Provider<WebSession> webSession, final GitWebConfig gitwebConfig,
      final GitBlit gitBlit,@PluginName final String pluginName, @GerritServerConfig Config gerritConfig) {

    this.repoManager = repoManager;
    this.webSession = webSession;
    this.gitwebConfig = gitwebConfig;
    this.gitBlit = gitBlit;
    this.pluginName = pluginName;
    this.gerritConfig = gerritConfig;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

    showGitBlitBanner();
    
    try {
      InputStream resin =
          getClass().getResourceAsStream("reference.properties");
      Properties properties = null;
      try {
        properties = new Properties();
        properties.load(resin);
        properties.put("git.repositoriesFolder", repoManager.getBasePath()
            .toString());
      } finally {
        resin.close();
      }
      IStoredSettings settings = new GitBlitSettings(properties);
      GitBlit.self().configureContext(settings, false);
      GitBlit.self().contextInitialized(
          new ServletContextEvent(filterConfig.getServletContext()));
      super.init(new CustomFilterConfig(filterConfig));
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void showGitBlitBanner() {
    logger.info(Constants.BORDER);
    logger.info("            _____  _  _    _      _  _  _");
    logger.info("           |  __ \\(_)| |  | |    | |(_)| |");
    logger.info("           | |  \\/ _ | |_ | |__  | | _ | |_");
    logger.info("           | | __ | || __|| '_ \\ | || || __|");
    logger.info("           | |_\\ \\| || |_ | |_) || || || |_");
    logger.info("            \\____/|_| \\__||_.__/ |_||_| \\__|");
    String submsg = Constants.getGitBlitVersion() + " #Gerrit fork";
    int spacing = (Constants.BORDER.length() - submsg.length()) / 2;
    StringBuilder sb = new StringBuilder();
    while (spacing > 0) {
      spacing--;
      sb.append(' ');
    }
    logger.info(sb.toString() + submsg);
    logger.info("");
    logger.info(Constants.BORDER);
  }

  public static String getGitblitUrl(Config gerritConfig) {
    return Objects.firstNonNull(gerritConfig.getString("gitweb", null, "url"),
        "/plugins/gitblit/");
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
      return;
    }
  }

  class CustomFilterConfig implements FilterConfig {

    private FilterConfig parentFilterConfig;

    public CustomFilterConfig(FilterConfig parent) {
      this.parentFilterConfig = parent;
    }

    public String getFilterName() {
      return "gerritWicketFilter";
    }

    public ServletContext getServletContext() {
      return parentFilterConfig.getServletContext();
    }

    public String getInitParameter(String paramString) {
      if ("applicationClassName".equals(paramString)) {
        return "com.gitblit.wicket.GitBlitWebApp";
      } else if ("ignorePaths".equals(paramString)) {
        return "pages/,feed/";
      } else if ("filterMappingUrlPattern".equals(paramString)) {
        return "/*";
      } 
      return null;
    }

    public Enumeration<String> getInitParameterNames() {
      Vector<String> result = new Vector<String>();
      result.add("applicationName");
      result.add("ignorePaths");
      result.add("filterMappingUrlPattern");
      return new ParamEnum(result);
    }

    class ParamEnum implements Enumeration<String> {

      Vector<String> items;
      Iterator<String> iter;

      public ParamEnum(Vector<String> items) {
        this.items = items;
        this.iter = this.items.iterator();
      }

      public boolean hasMoreElements() {
        return iter.hasNext();
      }

      public String nextElement() {
        return iter.next();
      }
    }
  }
}
