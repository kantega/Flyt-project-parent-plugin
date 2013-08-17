/*
 * Copyright 2013 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kantega.openaksess.generateprojectxml;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

@Mojo(name = "generate-project-parent", requiresProject = true, defaultPhase = LifecyclePhase.INSTALL, threadSafe = true )
public class GenerateProjectParentMojo extends AbstractProjectParentMojo {

    @Parameter(defaultValue = "2.4.1")
    private String mavenreleasepluginversion;

    @Parameter(defaultValue = "2.4")
    private String mavenjarpluginversion;

    @Parameter(defaultValue = "2.15")
    private String mavensurefirepluginversion;

    @Parameter(defaultValue = "3.1")
    private String mavencompilerpluginversion;

    @Parameter(defaultValue = "1.7")
    private String mavencompilerplugintarget;

    @Component
    protected ArtifactInstaller installer;

    public void execute() throws MojoFailureException {
        getLog().info("Generating project parent pom");
        try {
            String projectParent = IOUtils.toString(getClass().getResource("/pom.xml.template"));
            projectParent = projectParent.replace("#{openaksessversion}", project.getVersion());
            projectParent = projectParent.replace("#{mavenreleasepluginversion}", mavenreleasepluginversion);
            projectParent = projectParent.replace("#{mavensurefirepluginversion}", mavensurefirepluginversion);
            projectParent = projectParent.replace("#{mavencompilerpluginversion}", mavencompilerpluginversion);
            projectParent = projectParent.replaceAll("#\\{mavencompilerplugintarget\\}", mavencompilerplugintarget);
            Properties properties = project.getProperties();
            StringBuilder propertiesNode = new StringBuilder("<properties>");
            propertiesNode.append("<openaksess.version>");
            propertiesNode.append(project.getVersion());
            propertiesNode.append("</openaksess.version>");
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                propertiesNode.append("<").append(entry.getKey()).append(">");
                propertiesNode.append(entry.getValue());
                propertiesNode.append("</").append(entry.getKey()).append(">\n");
            }
            propertiesNode.append("</properties>");
            projectParent = projectParent.replace("#{properties}", propertiesNode);

            File projectParentFile = getProjectParentFile();
            try(OutputStream outputStream = new FileOutputStream(projectParentFile)){
                IOUtils.write(projectParent, outputStream);
            }

            Artifact pom = getArtifact(projectParentFile);

            installer.install(projectParentFile, pom, localRepository);
        } catch (IOException | ArtifactInstallationException e) {
            throw new MojoFailureException("Failed to generate openaksess project parent", e);
        }
    }
}
