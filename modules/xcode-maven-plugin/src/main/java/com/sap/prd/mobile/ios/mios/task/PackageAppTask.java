/*
 * #%L
 * xcode-maven-plugin
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sap.prd.mobile.ios.mios.task;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import com.sap.prd.mobile.ios.mios.ScriptRunner;
import com.sap.prd.mobile.ios.mios.XCodeBuildLayout;
import com.sap.prd.mobile.ios.mios.XCodeException;
import com.sap.prd.mobile.ios.mios.buddy.ProductNameBuddy;

public class PackageAppTask
{

  private Log log;
  private String productName;
  private MavenProjectHelper projectHelper;
  private File compileDir;
  private MavenProject mavenProject;

  private String configuration;

  private String sdk;

  public PackageAppTask setLog(Log log)
  {
    this.log = log;
    return this;
  }

  public PackageAppTask setProductName(String productName)
  {
    this.productName = productName;
    return this;
  }

  public PackageAppTask setMavenProject(MavenProject mavenProject)
  {
    this.mavenProject = mavenProject;
    return this;
  }

  public PackageAppTask setConfiguration(String configuration)
  {
    this.configuration = configuration;
    return this;
  }

  public PackageAppTask setSdk(String sdk)
  {
    this.sdk = sdk;
    return this;
  }

  public PackageAppTask setProjectHelper(MavenProjectHelper projectHelper)
  {
    this.projectHelper = projectHelper;
    return this;
  }

  public PackageAppTask setCompileDir(File compileDir)
  {
    this.compileDir = compileDir;
    return this;
  }

  /**
   * Packages the app into an app.zip file
   */
  public void execute() throws XCodeException
  {

    try {

      String productName = ProductNameBuddy.getProductName(log, this.productName, mavenProject, sdk,
            configuration);
      String strippedProductName = ProductNameBuddy.stripProductName(productName);

      final File rootDir = XCodeBuildLayout.getAppFolder(compileDir, configuration, sdk);

      final File appZipFile = ScriptRunner.zipSubfolder(
            new File(mavenProject.getBuild().getDirectory(), "scripts").getCanonicalFile(),
            rootDir, productName + ".app", strippedProductName + ".app.zip", null);

      log.info("Application file packaged (" + appZipFile + ")");

      projectHelper.attachArtifact(mavenProject, "zip", configuration + "-" + sdk + "-app", appZipFile);
    }
    catch (IOException ex) {
      throw new XCodeException("Could not package the app: " + ex.getMessage(), ex);
    }
  }

}