package com.googlesource.gerrit.plugins.gitblit;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.util.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class GitBlitInitStep extends InitStepPlugin implements InitStep {

  private final ConsoleUI ui;
  private String pluginName;

  @Inject
  public GitBlitInitStep(final ConsoleUI ui, final Injector initInjector, @PluginName String pluginName) {
    super(initInjector);
    this.ui = ui;
    this.pluginName = pluginName;
  }

  @Override
  public void run() throws Exception {
    ui.header("\nGitBlit Integration");

    if(ui.yesno(true, "Do you want to use GitBlit as your GitWeb viewer ?")) {
      configureGitBlit();
    }
  }

  private void configureGitBlit() {
    Section gitWeb = getSection("gitweb", null);
    gitWeb.set("type", "custom");
    gitWeb.set("url", "plugins/" + pluginName + "/");
    gitWeb.set("project", "summary/${project}");
    gitWeb.set("revision", "commit/${project}/${commit}");
    gitWeb.set("branch", "log/${project}/${branch}");
    gitWeb.set("filehistory", "history/${project}/${branch}/${file}");
    gitWeb.string("Link name", "linkname", "GitBlit");
  }

}
