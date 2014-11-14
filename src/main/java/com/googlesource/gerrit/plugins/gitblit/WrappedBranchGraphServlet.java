package com.googlesource.gerrit.plugins.gitblit;

import com.gitblit.servlet.BranchGraphServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class WrappedBranchGraphServlet extends BranchGraphServlet {

  @Inject
  public WrappedBranchGraphServlet() {
    super();
  }
}
