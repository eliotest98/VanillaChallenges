package io.eliotesta98.VanillaChallenges.Core;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Represents a standalone library that can be loaded. You can also set
 * a minimum and a maximum MC version where this library works, as well
 * as Java versions to restrict loading.
 */
public final class Library {

    /**
     * The groupID as per maven standards such as "org.mineacademy"
     */
    private final String groupId;

    /**
     * The artifactID as per maven standards such as "foundation"
     */
    private final String artifactId;

    /**
     * The version of the library such as "1.0.0"
     */
    private final String version;

    /**
     * The jar path from where we download the library JAR.
     */
    private final String jarPath;

    /*
     * Create a new library
     */
    private Library(String groupId, String artifactId, String version, String repositoryPath) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;

        this.jarPath = repositoryPath;
    }

    /**
     * Loads this library, returning true if loading was successful,
     * false if minimum/maximum Java or Minecraft version not met
     *
     * @return
     */
    public boolean load() {

        try {
            final File libraries = new File(Bukkit.getWorldContainer(), "libraries");
            final File file = new File(libraries, this.groupId.replace(".", "/") + "/" + this.artifactId.replace(".", "/") + "/" + this.version + "/" + this.artifactId + "-" + this.version + ".jar");

            // Download file from repository to our disk
            if (!file.exists()) {
                file.getParentFile().mkdirs();

                Bukkit.getLogger().info("Downloading library: " + this.getName());

                final URL url = new URL(this.jarPath);
                final URLConnection connection = url.openConnection();

                try (InputStream in = connection.getInputStream()) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Load the library into the plugin's class loader
            final URL url = file.toURI().toURL();
            final ClassLoader classLoader = Main.class.getClassLoader();
            final Method method = getDeclaredMethod(URLClassLoader.class, "addURL", URL.class);

            invoke(method, classLoader, url);

        } catch (final Throwable throwable) {
            throw new RuntimeException("Unable to load library " + this.getName() + ".", throwable);
        }

        return true;
    }

    /**
     * Get a declared class method
     *
     * @param clazz
     * @param methodName
     * @param args
     * @return
     */
    public static Method getDeclaredMethod(Class<?> clazz, final String methodName, Class<?>... args) throws Exception {
        final Class<?> originalClass = clazz;

        while (!clazz.equals(Object.class))
            try {
                final Method method = clazz.getDeclaredMethod(methodName, args);
                method.setAccessible(true);

                return method;

            } catch (final NoSuchMethodException ex) {
                clazz = clazz.getSuperclass();

            } catch (final Throwable t) {
                t.printStackTrace();
                throw new Exception("Error import library");
            }

        throw new Exception("Unable to find method " + methodName  + " in class " + originalClass + " and her subclasses");
    }


    /**
     * Invoke a non static method
     *
     * @param <T>
     * @param method
     * @param instance
     * @param params
     * @return
     */
    public static <T> T invoke(final Method method, final Object instance, final Object... params) throws Exception {

        try {
            return (T) method.invoke(instance, params);

        } catch (final ReflectiveOperationException ex) {
            ex.printStackTrace();
            throw new Exception("Could not invoke method " + method + " on instance " + instance);
        }
    }

    /**
     * Return the groupId:artifactId:version as string
     *
     * @return
     */
    public String getName() {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }

    /**
     * See {@link #getName()}
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getName();
    }

    // ------------------------------------------------------------------------------------------------------------
    // Static
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Create a new library from the Maven Central repository
     *
     * Use this if your favorite library is found at: https://mvnrepository.com/repos/central
     *
     * The path syntax is as follows: "groupId:artifactId:version" such as "org.jsoup:jsoup:1.14.3"
     *
     * @param path
     * @return
     */
    public static Library fromMavenRepo(String path) {
        final String[] split = path.split("\\:");

        return fromMavenRepo(split[0], split[1], split[2]);
    }

    /**
     * Create a new library from the Maven Central repository
     *
     * Use this if your favorite library is found at: https://mvnrepository.com/repos/central
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    public static Library fromMavenRepo(String groupId, String artifactId, String version) {
        final String jarPath = "https://repo1.maven.org/maven2/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";

        return new Library(groupId, artifactId, version, jarPath);
    }

    /**
     * Create a new library from your own custom repository path.
     *
     * The path must be fully qualified online URL to the JAR. In {@link #fromMavenRepo(String, String, String)}
     * we use "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.jar"
     * but in reality this could potentially be whatever such as yourdomain.com/yourlibrary.jar
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @param jarPath
     * @return
     */
    public static Library fromPath(String groupId, String artifactId, String version, String jarPath) {
        return new Library(groupId, artifactId, version, jarPath);
    }
}
