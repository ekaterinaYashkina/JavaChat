import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class ResourceManager {

    private String directory = "out/artifacts/operations_jar";

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    private HashMap<String, CommandProducer> commands = new HashMap<>();

    public CommandProducer caluculate(String command){
        return commands.get(command);
    }

    public void loadClass() throws ClassNotFoundException {
        File pluginsDir = new File(directory);
        for (File jar : pluginsDir.listFiles()) {
            System.out.println(jar);
            try {
                URL[] urls = new URL[]{jar.toURL()};
                ClassLoader loader = new URLClassLoader(urls);

                String className = jar.getName().split(".jar")[0];
                System.out.println(className);
                Class classLoad = loader.loadClass(className);
                CommandProducer prod = (CommandProducer) classLoad.newInstance();
                String opertaion = prod.getInvokationCommand();
                commands.put(opertaion, prod);
                // Apparently its bad to use Class.newInstance, so we use
                // newClass.getConstructor() instead


            } catch (ClassNotFoundException e) {
                // There might be multiple JARs in the directory,
                // so keep looking
                continue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        throw new ClassNotFoundException("Class "
                + " wasn't found in directory " + directory);
    }
}
