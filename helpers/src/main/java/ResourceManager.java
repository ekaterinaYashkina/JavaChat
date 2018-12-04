import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;


//Load jars and save the common resources for further usage
public class ResourceManager {

    private final static Logger logger = LogManager.getLogger("pluginsLog");
    private String directory = "plugins";

    public String getDirectory(){
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    private HashMap<String, CommandProducer> commands = new HashMap<>();

    public void append(CommandProducer cp, String name){
        commands.put(name, cp);
    }

    public CommandProducer caluculate(String command){
        return commands.get(command);
    }

    public void loadClass() throws ClassNotFoundException {
        File pluginsDir = new File(directory);
        if (pluginsDir.exists()) {
            for (File jar : pluginsDir.listFiles()) {
                try {
                    URL[] urls = new URL[]{jar.toURL()};
                    ClassLoader loader = new URLClassLoader(urls);

                    String className = jar.toString().split(".jar")[0];
                    System.out.println(className);
                    Class classLoad = loader.loadClass(className);
                    CommandProducer prod = (CommandProducer) classLoad.newInstance();
                    String opertaion = prod.getInvokationCommand();
                    System.out.println(opertaion);
                    append(prod, opertaion);
                    logger.info("Loaded jar: {}", className);

                } catch (MalformedURLException e) {
                    logger.error(e);
                } catch (IllegalAccessException e) {
                    logger.error(e);
                } catch (InstantiationException e) {
                    logger.error(e);
                } catch (NullPointerException e) {
                    logger.error(e);
                }
            }
        }
        else {
            System.out.println("Wrong path to plugins, no support for plugins will be provided.");
            logger.warn("Wrong path to plugins, no support for plugins will be provided.");
        }

    }
}
