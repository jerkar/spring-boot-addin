import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkMavenPublicationInfo;
import org.jerkar.api.depmanagement.JkRepoSet;
import org.jerkar.api.depmanagement.JkVersion;
import org.jerkar.api.java.JkJavaVersion;
import org.jerkar.api.java.project.JkJavaProject;
import org.jerkar.api.java.project.JkJavaProjectMaker;
import org.jerkar.api.system.JkProcess;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.JkRun;
import org.jerkar.tool.builtins.java.JkPluginJava;

import static org.jerkar.api.depmanagement.JkJavaDepScopes.PROVIDED;


/**
 * Jerkar build class (generated by Jerkar from existing pom).
 * 
 * @formatter:off
 */
class Build extends JkRun {

    final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    public String ossrhUsername;

    public String ossrhPwd;

    @Override
    protected void setup() {
        JkJavaProject project = javaPlugin.getProject();
        project.setVersionedModule("org.jerkar.plugins:springboot", "2.0.0-SNAPSHOT");
        project.getCompileSpec().setSourceAndTargetVersion(JkJavaVersion.V8);
        project.addDependencies(JkDependencySet.of()
                .and("org.jerkar:core:0.7.0.RC1", PROVIDED));
        project.setMavenPublicationInfo(mavenPublicationInfo());
        if (!project.getVersionedModule().getVersion().isSnapshot()) {
            javaPlugin.pack.javadoc = true;
            javaPlugin.pack.sources = true;
            javaPlugin.publish.signArtifacts = true;
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
        maker.setDependencyResolver(maker.getDependencyResolver().withRepos(JkRepoSet.ofOssrhSnapshotAndRelease()));
        maker.getTasksForPublishing().setPublishRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUsername, ossrhPwd));
    }

    public void release() {
        JkVersion version = javaPlugin.getProject().getVersionedModule().getVersion();
        if (version.isSnapshot()) {
            throw new IllegalStateException("Cannot release a snapshot version");
        }
        javaPlugin.pack.javadoc = true;
        javaPlugin.pack.sources = true;
        javaPlugin.clean().pack();
        javaPlugin.publish();
        String tagName = version.toString();
        JkProcess git = JkProcess.of("git").withFailOnError(true);
        git.andParams("tag", "-a", tagName, "-m", "Release").runSync();
        git.andParams("push", "origin", tagName).runSync();
    }

    public static void main(String[] args) {
        JkPluginJava javaPlugin = JkInit.instanceOf(Build.class, args).javaPlugin;
        javaPlugin.clean().pack();
        javaPlugin.getProject().getMaker().getTasksForPublishing().publishLocal();
    }

}