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

import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.httpd.GitWebConfig;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.AnonymousUser;
import com.google.gerrit.server.git.LocalDiskRepositoryManager;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.gerrit.server.project.ProjectControl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@Singleton
public class GitBlitServlet extends HttpServlet {

  private static final Logger log = LoggerFactory
      .getLogger(GitBlitServlet.class);

  private final Set<String> deniedActions;

  private final LocalDiskRepositoryManager repoManager;
  private final ProjectControl.Factory projectControl;
  private final Provider<AnonymousUser> anonymousUserProvider;
  private final GitWebConfig gitWebConfig;

  @Inject
  public GitBlitServlet(final LocalDiskRepositoryManager repoManager,
      final ProjectControl.Factory projectControl,
      final Provider<AnonymousUser> anonymousUserProvider,
      final GerritConfig gerritConfig, final GitWebConfig gitWebConfig) {

    this.gitWebConfig = gitWebConfig;
    this.repoManager = repoManager;
    this.projectControl = projectControl;
    this.anonymousUserProvider = anonymousUserProvider;

    this.deniedActions = new HashSet<String>();
    deniedActions.add("forks");
    deniedActions.add("opml");
    deniedActions.add("project_list");
    deniedActions.add("project_index");
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse rsp)
      throws ServletException, IOException {

    if (req.getQueryString() == null || req.getQueryString().isEmpty()) {
      // No query string? They want the project list, which we don't
      // currently support. Return to Gerrit's own web UI.
      //
      rsp.sendRedirect(req.getContextPath() + "/");
      return;
    }

    final Map<String, String> params = getParameters(req);
    if (deniedActions.contains(params.get("a"))) {
      rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String name = params.get("p");
    if (name == null) {
      rsp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    if (name.endsWith(".git")) {
      name = name.substring(0, name.length() - 4);
    }

    final Project.NameKey nameKey = new Project.NameKey(name);
    final ProjectControl project;
    try {
      project = projectControl.validateFor(nameKey);
      if (!project.allRefsAreVisible()) {
        // Pretend the project doesn't exist
        throw new NoSuchProjectException(nameKey);
      }
    } catch (NoSuchProjectException e) {
      rsp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    final Repository repo;
    try {
      repo = repoManager.openRepository(nameKey);
    } catch (RepositoryNotFoundException e) {
      getServletContext().log("Cannot open repository", e);
      rsp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
    try {
      rsp.setHeader("Expires", "Fri, 01 Jan 1980 00:00:00 GMT");
      rsp.setHeader("Pragma", "no-cache");
      rsp.setHeader("Cache-Control", "no-cache, must-revalidate");

      exec(req, rsp, project, repo);
    } finally {
      repo.close();
    }
  }

  private void exec(final HttpServletRequest req,
      final HttpServletResponse rsp, final ProjectControl project,
      final Repository repo){

    //TODO call GitBlit here
  }

  private static Map<String, String> getParameters(final HttpServletRequest req)
      throws UnsupportedEncodingException {
    final Map<String, String> params = new HashMap<String, String>();
    for (final String pair : req.getQueryString().split("[&;]")) {
      final int eq = pair.indexOf('=');
      if (0 < eq) {
        String name = pair.substring(0, eq);
        String value = pair.substring(eq + 1);

        name = URLDecoder.decode(name, "UTF-8");
        value = URLDecoder.decode(value, "UTF-8");
        params.put(name, value);
      }
    }
    return params;
  }
}
