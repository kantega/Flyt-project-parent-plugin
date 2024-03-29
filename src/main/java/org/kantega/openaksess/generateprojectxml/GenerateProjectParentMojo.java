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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Mojo(name = "generate-project-parent", requiresProject = true, defaultPhase = LifecyclePhase.INSTALL, threadSafe = true )
public class GenerateProjectParentMojo extends AbstractProjectParentMojo {

    @Parameter(defaultValue = "2.5")
    private String mavenreleasepluginversion;

    @Parameter(defaultValue = "3.2.0")
    private String mavenjarpluginversion;

    @Parameter(defaultValue = "2.9")
    private String mavensurefirepluginversion;

    @Parameter(defaultValue = "3.8.1")
    private String mavencompilerpluginversion;

    @Parameter(defaultValue = "1.8")
    private String mavencompilerplugintarget;

    @Parameter(defaultValue = "3.2.1")
    private String mavensourcepluginversion;

    @Parameter(defaultValue = "2.8.2")
    private String mavendeploypluginversion;

    @Parameter(defaultValue = "3.3.1")
    private String mavenjavadocpluginversion;

    @Parameter(defaultValue = ".*\\.version")
    private String includePropertyPattern;

    @Component
    protected ArtifactInstaller installer;

    public void execute() throws MojoFailureException {
        Pattern pattern = Pattern.compile(includePropertyPattern);
        getLog().info("Generating project parent pom");
        try {
            String projectParent = IOUtils.toString(getClass().getResource("/pom.xml.template"), StandardCharsets.UTF_8);
            projectParent = projectParent.replace("#{openaksessversion}", project.getVersion());
            projectParent = projectParent.replace("#{mavenreleasepluginversion}", mavenreleasepluginversion);
            projectParent = projectParent.replace("#{mavensurefirepluginversion}", mavensurefirepluginversion);
            projectParent = projectParent.replace("#{mavencompilerpluginversion}", mavencompilerpluginversion);
            projectParent = projectParent.replace("#{mavensourcepluginversion}", mavensourcepluginversion);
            projectParent = projectParent.replace("#{mavendeploypluginversion}", mavendeploypluginversion);
            projectParent = projectParent.replace("#{mavencompilerpluginversion}", mavencompilerpluginversion);
            projectParent = projectParent.replace("#{mavenjavadocpluginversion}", mavenjavadocpluginversion);
            projectParent = projectParent.replace("#{mavenjarpluginversion}", mavenjarpluginversion);
            projectParent = projectParent.replaceAll("#\\{mavencompilerplugintarget\\}", mavencompilerplugintarget);
            Properties properties = project.getProperties();
            StringBuilder propertiesNode = new StringBuilder("<properties>");
            propertiesNode.append("<openaksess.version>");
            propertiesNode.append(project.getVersion());
            propertiesNode.append("</openaksess.version>");
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (pattern.matcher(key).matches()) {
                    propertiesNode.append("<").append(key).append(">");
                    propertiesNode.append(entry.getValue());
                    propertiesNode.append("</").append(key).append(">\n");
                }
            }
            propertiesNode.append("<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>" +
                                  "<maven.javadoc.failOnError>false</maven.javadoc.failOnError>");
            propertiesNode.append("</properties>");
            projectParent = projectParent.replace("#{properties}", propertiesNode);

            File projectParentFile = getProjectParentFile();
            try(OutputStream outputStream = new FileOutputStream(projectParentFile)){
                IOUtils.write(projectParent, outputStream, StandardCharsets.UTF_8);
            }

            Artifact pom = getArtifact(projectParentFile);

            installer.install(projectParentFile, pom, localRepository);
        } catch (IOException | ArtifactInstallationException e) {
            throw new MojoFailureException("Failed to generate openaksess project parent", e);
        }
    }
}
