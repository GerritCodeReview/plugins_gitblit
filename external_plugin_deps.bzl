load("//tools/bzl:maven_jar.bzl", "maven_jar", "MAVEN_LOCAL")

GITBLIT = 'https://gitblit.github.io/gitblit-maven'

def external_plugin_deps():
  maven_jar(
    name = 'pf4j',
    artifact = 'ro.fortsoft.pf4j:pf4j:0.9.0',
    sha1 = 'ff412cadfee820c50bf02723187eda6165d70379',
  )

  maven_jar(
    name = 'gitblit_jar',
    artifact = 'com.gitblit:gitblit:1.9.0-SNAPSHOT',
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'wicket',
    artifact = 'org.apache.wicket:wicket:1.4.23',
    sha1 = '304d9e23e52e9488308644093663b568952abd0b',
  )

  maven_jar(
    name = 'wicket_auth_roles',
    artifact = 'org.apache.wicket:wicket-auth-roles:1.4.23',
    sha1 = '1b130dbf5578ace37507430a4a523f6594bf34fa',
  )

  maven_jar(
    name = 'wicket_extensions',
    artifact = 'org.apache.wicket:wicket-extensions:1.4.23',
    sha1 = '9ca61ca2273289d648dbb430e9033693c9b5eed3',
  )

  maven_jar(
    name = 'groovy',
    artifact = 'org.codehaus.groovy:groovy-all:2.4.1',
    sha1 = 'a9ca9c9de09361ec2a18d2c058d2524fbd8eae0c',
  )

  maven_jar(
    name = 'jdom',
    artifact = 'jdom:jdom:1.0',
    sha1 = 'a2ac1cd690ab4c80defe7f9bce14d35934c35cec',
  )

  maven_jar(
    name = 'markdownpapers',
    artifact = 'org.tautua.markdownpapers:markdownpapers-core:1.3.2',
    sha1 = 'da22db6660e90b9a677bbdfc2c511c619ea5c249',
  )

  maven_jar(
    name = 'rome',
    artifact = 'rome:rome:1.0',
    sha1 = '022b33347f315833e9348cec2751af1a5d5656e4',
  )

  maven_jar(
    name = 'unboundid',
    artifact = 'com.unboundid:unboundid-ldapsdk:2.3.8',
    sha1 = '1788564d03f0b786a695f4de67b4cb55eda45e14',
  )

  maven_jar(
    name = 'tika',
    artifact = 'org.apache.tika:tika-core:1.5',
    sha1 = '194ca0fb3d73b07737524806fbc3bec89063c03a',
  )

  maven_jar(
    name = 'wikitext_core',
    artifact = 'org.fusesource.wikitext:wikitext-core:1.4',
    sha1 = 'b877ee61d064c01cbf9834ab1b7146cd44acad65',
  )

  maven_jar(
    name = 'twiki_core',
    artifact = 'org.fusesource.wikitext:twiki-core:1.4',
    sha1 = '00c392027ae056d555040af2d1e0ed217fa94712',
  )

  maven_jar(
    name = 'textile_core',
    artifact = 'org.fusesource.wikitext:textile-core:1.4',
    sha1 = '9169c4a2865232c7b22137d759fb7ee2cbf019de',
  )

  maven_jar(
    name = 'tracwiki_core',
    artifact = 'org.fusesource.wikitext:tracwiki-core:1.4',
    sha1 = 'e2c8a5597695dc82256f2a97a505783e5ab5b0cb',
  )

  maven_jar(
    name = 'mediawiki_core',
    artifact = 'org.fusesource.wikitext:mediawiki-core:1.4',
    sha1 = '30d1b5551bbf97a17abc22d51fe8dd3b4d27f1ab',
  )

  maven_jar(
    name = 'confluence_core',
    artifact = 'org.fusesource.wikitext:confluence-core:1.4',
    sha1 = '08210b4af6f055ada934753facd27d7abf9d01a8',
  )

  maven_jar(
    name = 'ivy',
    artifact = 'org.apache.ivy:ivy:2.2.0',
    sha1 = 'f9d1e83e82fc085093510f7d2e77d81d52bc2081',
  )

  maven_jar(
    name = 'force_partner_api',
    artifact = 'com.force.api:force-partner-api:24.0.0',
    sha1 = 'ce3cd3e2ccd51735f27a83e90018123e8bd10314',
  )

  maven_jar(
    name = 'freemarker',
    artifact = 'org.freemarker:freemarker:2.3.22',
    sha1 = '473d784b3cd2dcb6d49a287ded0542b7862c7d68',
  )

  maven_jar(
    name = 'waffle_jna',
    artifact = 'com.github.dblock.waffle:waffle-jna:1.7.3',
    sha1 = '94ba74d3fa15bb61d4901b062b8fd5046c9e99b9',
  )

  maven_jar(
    name = 'libpam4j',
    artifact = 'org.kohsuke:libpam4j:1.8',
    sha1 = '548d4a1177adad8242fe03a6930c335669d669ad',
  )

  maven_jar(
    name = 'lucene-highlighter',
    artifact = 'org.apache.lucene:lucene-highlighter:5.5.4',
    sha1 = '433f53f03f1b14337c08d54e507a5410905376fa',
  )

  maven_jar(
    name = 'lucene-memory',
    artifact = 'org.apache.lucene:lucene-memory:5.5.4',
    sha1 = '4dbdc2e1a24837722294762a9edb479f79092ab9',
  )
