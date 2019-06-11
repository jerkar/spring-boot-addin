import dev.jeka.core.api.depmanagement.*;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.java.project.JkJavaProject;
import dev.jeka.core.api.java.project.JkJavaProjectMaker;
import dev.jeka.core.api.system.JkException;
import dev.jeka.core.api.system.JkPrompt;
import dev.jeka.core.api.tooling.JkGitWrapper;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkConstants;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.PROVIDED;

/**
 * Jerkar build class (generated by Jerkar from existing pom).
 * 
 * @formatter:off
 */
class Build extends JkCommands {

    final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    public String ossrhUsername;

    public String ossrhPwd;

    @Override
    protected void setup() {
        JkJavaProject project = javaPlugin.getProject();
        JkGitWrapper git = JkGitWrapper.of(getBaseDir());
        project.setVersionedModule("dev.jeka.plugins:springboot", git.getVersionWithTagOrSnapshot());
        project.getCompileSpec().setSourceAndTargetVersion(JkJavaVersion.V8);
        project.addDependencies(JkDependencySet.of()
                .andFile(getBaseDir().resolve(JkConstants.JEKA_DIR).resolve("boot/dev.jeka.jeka-core.jar"), PROVIDED));
        project.setMavenPublicationInfo(mavenPublicationInfo());
        if (!project.getVersionedModule().getVersion().isSnapshot()) {
            javaPlugin.pack.javadoc = true;
            javaPlugin.pack.sources = true;
        }
    }

    protected JkMavenPublicationInfo mavenPublicationInfo() {
        return JkMavenPublicationInfo
                .of("Jerkar Add-in for Spring Boot",
                        "A Jerkar plugin for Spring boot application", "http://jerkar.github.io")
                .withScm("https://github.com/jerkar/spring-boot-addin.git")
                .andApache2License()
                .andGitHubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    @Override
    protected void setupAfterPluginActivations() {
        JkJavaProjectMaker maker = javaPlugin.getProject().getMaker();
        maker.setDependencyResolver(maker.getDependencyResolver().andRepos(
                JkRepo.ofMavenOssrhDownloadAndDeploySnapshot(ossrhUsername, ossrhPwd).toSet()));
        maker.getTasksForPublishing().setPublishRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUsername, ossrhPwd));
    }

    public void release() {
        JkGitWrapper git = JkGitWrapper.of(getBaseDir());
        System.out.println("Existing tags :");
        git.exec("tag");
        String newTag = JkPrompt.ask("Enter new tag : ");
        if (git.isDirty()) {
            throw new JkException("Git workspace is dirty. Cannot put tag.");
        }
        git.tagAndPush(newTag);
    }

    public static void main(String[] args) {
        JkPluginJava javaPlugin = JkInit.instanceOf(Build.class, args).javaPlugin;
        javaPlugin.clean().pack();
    }

}