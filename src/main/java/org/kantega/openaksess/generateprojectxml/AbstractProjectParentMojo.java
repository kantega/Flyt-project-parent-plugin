package org.kantega.openaksess.generateprojectxml;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;

import java.io.File;

public abstract class AbstractProjectParentMojo extends AbstractMojo {
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    protected MavenProject project;

    @Parameter( property = "localRepository", required = true, readonly = true )
    protected ArtifactRepository localRepository;

    @Component
    protected ArtifactFactory artifactFactory;

    File getProjectParentFile(){
        return new File(System.getProperty("java.io.tmpdir"), "projectparentpom.xml");
    }

    Artifact getArtifact(File projectParentFile) {
        Artifact pom = artifactFactory.createBuildArtifact(project.getGroupId(), "openaksess-project-parent", project.getVersion(), "pom");
        ProjectArtifactMetadata metadata = new ProjectArtifactMetadata(pom, projectParentFile);

        pom.addMetadata(metadata);
        return pom;
    }
}
