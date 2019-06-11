import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkImport;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import org.jerkar.plugins.springboot.JkPluginSpringboot;
import org.jerkar.plugins.springboot.JkSpringModules;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.TEST;


// Should be @JkImport("org.jerkar.plugins:spring-boot:2.x.x") to rely on a release version
@JkImport("../dev.jeka.plugins.spring-boot/jeka/output/classes")
class BuildSample extends JkCommands {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springbootPlugin = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springbootPlugin.springbootVersion = "2.0.3.RELEASE";
        javaPlugin.getProject().addDependencies(JkDependencySet.of()
                .and(JkSpringModules.Boot.STARTER_WEB)
                .and(JkSpringModules.Boot.STARTER_TEST, TEST)
        );
    }

    public static void main(String[] args) {
        JkInit.instanceOf(BuildSample.class, args).javaPlugin.clean().pack();
    }

}