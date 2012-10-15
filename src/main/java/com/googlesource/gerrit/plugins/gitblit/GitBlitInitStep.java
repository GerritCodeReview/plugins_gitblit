package com.googlesource.gerrit.plugins.gitblit;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.pgm.init.Section;
import com.google.gerrit.pgm.init.Section.Factory;
import com.google.gerrit.pgm.util.ConsoleUI;
import com.google.inject.Inject;

public class GitBlitInitStep implements InitStep {

  private final ConsoleUI ui;
  private final String pluginName;
  private final Factory sections;

  @Inject
  public GitBlitInitStep(final ConsoleUI ui, final Section.Factory sections, @PluginName final String pluginName) {
    this.ui = ui;
    this.pluginName = pluginName;
    this.sections = sections;
  }

  @Override
  public void run() throws Exception {
    ui.message("\n");
    ui.header("GitBlit Integration");

    if(ui.yesno(true, "Do you want to use GitBlit as your GitWeb viewer ?")) {
      configureGitBlit();
    }
  }

  private void configureGitBlit() {
    Section gitWeb = sections.get("gitweb", null);
    gitWeb.set("type", "custom");
    gitWeb.set("url", "plugins/" + pluginName + "/");
    gitWeb.set("project", "summary/${project}");
    gitWeb.set("revision", "commit/${project}/${commit}");
    gitWeb.set("branch", "log/${project}/${branch}");
    gitWeb.set("filehistory", "history/${project}/${branch}/${file}");
    gitWeb.string("Link name", "linkname", "GitBlit");
  }

}
