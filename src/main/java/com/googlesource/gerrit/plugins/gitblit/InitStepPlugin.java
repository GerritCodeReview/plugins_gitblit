package com.googlesource.gerrit.plugins.gitblit;



import com.google.gerrit.pgm.init.InitStep;
import com.google.gerrit.server.config.FactoryModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlesource.gerrit.plugins.gitblit.Section.Factory;

public abstract class InitStepPlugin implements InitStep {

  private final Factory sections;

  public InitStepPlugin(final Injector initInjector) {
    Injector initInjectorPlugin =
        initInjector.createChildInjector(new FactoryModule() {
          @Override
          protected void configure() {
            factory(Section.Factory.class);
          }
        });

    sections = initInjectorPlugin.getInstance(Section.Factory.class);
  }

  protected Section getSection(String section, String subsection) {
    return sections.get(section, subsection);
  }

}
