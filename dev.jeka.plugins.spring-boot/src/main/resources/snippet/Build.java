import dev.jeka.core.tool.JkClass;
import dev.jeka.core.tool.JkDefClasspath;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInit;
import dev.jeka.plugins.springboot.JkPluginSpringboot;

@JkDefClasspath("dev.jeka:springboot-plugin:${version}")
class Build extends JkClass {

    private final JkPluginSpringboot springboot = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springboot.setSpringbootVersion("2.5.2");
        springboot.javaPlugin().getProject().simpleFacade()
            .setCompileDependencies(deps -> deps
                .and("org.springframework.boot:spring-boot-starter-web")
            )
            .setTestDependencies(deps -> deps
                .and("org.springframework.boot:spring-boot-starter-test")
                    .withLocalExclusions("org.junit.vintage:junit-vintage-engine")
            );
    }

    @JkDoc("Cleans, tests and creates bootable jar.")
    public void cleanPack() {
        clean(); springboot.javaPlugin().pack();
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}
