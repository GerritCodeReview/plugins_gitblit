package com.googlesource.gerrit.plugins.gitblit.app;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

public class StaticCodingStrategy extends WebRequestCodingStrategy {

  @Override
  public String rewriteStaticRelativeUrl(String url) {
    // Avoid rewriting of non-static resources
    String[] urlParts = url.split("/");
    if (urlParts[urlParts.length - 1].indexOf('.') < 0) {
      return url;
    }

    int depth =
        ((ServletWebRequest) RequestCycle.get().getRequest())
            .getDepthRelativeToWicketHandler();
    return getRelativeStaticUrl(url, depth);
  }

  public static String getRelativePrefix(Request request) {
    int depth = ((ServletWebRequest) request).getDepthRelativeToWicketHandler();

    StringBuffer urlBuffer = new StringBuffer();
    for (; depth > 0; depth--) {
      urlBuffer.append("../");
    }

    return urlBuffer.toString();
  }

  public static String getStaticRelativePrefix(Request request) {
    int depth = ((ServletWebRequest) request).getDepthRelativeToWicketHandler();
    return getRelativeStaticUrl("", depth);
  }

  public static String getRelativeStaticUrl(String url, int depth) {
    StringBuffer urlBuffer = new StringBuffer();
    for (; depth > 0; depth--) {
      urlBuffer.append("../");
    }
    urlBuffer.append("static/"); // tells to Gerrit plugin runtime to load
                                 // static resources from gitblit plugin jar
                                 // file
    urlBuffer.append(url);

    return urlBuffer.toString();
  }
}
