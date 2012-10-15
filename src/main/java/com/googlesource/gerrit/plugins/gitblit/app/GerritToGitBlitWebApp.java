package com.googlesource.gerrit.plugins.gitblit.app;

import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;

import com.gitblit.wicket.GitBlitWebApp;

public class GerritToGitBlitWebApp extends GitBlitWebApp {
    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        return new WebRequestCycleProcessor() {
            protected IRequestCodingStrategy newRequestCodingStrategy() {
                return new StaticCodingStrategy();
            }
        };
    }
}
