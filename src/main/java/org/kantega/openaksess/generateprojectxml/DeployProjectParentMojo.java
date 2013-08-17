package org.kantega.openaksess.generateprojectxml;

import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "deploy-project-parent", requiresProject = true, defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true )
public class DeployProjectParentMojo extends AbstractProjectParentMojo {

    @Component
    private ArtifactDeployer artifactDeployer;

    @Parameter( property = "project.distributionManagementArtifactRepository", required = true, readonly = true )
    protected ArtifactRepository remoteRepository;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Deploying OpenAksess project parent");
        File projectParentFile = getProjectParentFile();

        try {
            artifactDeployer.deploy(projectParentFile, getArtifact(projectParentFile), remoteRepository, localRepository);
        } catch (ArtifactDeploymentException e) {
            throw new MojoFailureException("Error when deploying OpenAksess project parent", e);
        }
    }
}
