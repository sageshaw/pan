package analysis.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtilities {

    public static String[] getPackageContent(String packageName) throws IOException {
        final String packageAsDirName = packageName.replace(".", "/");
        final List <String> list = new ArrayList <>();
        final Enumeration <URL> urls =
                Thread.currentThread().getContextClassLoader().getResources(packageAsDirName);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String file = url.getFile();
            switch (url.getProtocol()) {
                case "file":
                    File dir = new File(file);
                    for (File f : dir.listFiles()) {
                        list.add(packageAsDirName + "/" + f.getName());
                    }
                    break;
                case "jar":
                    int colon = file.indexOf(':');
                    int exclamation = file.indexOf('!');
                    String jarFileName = file.substring(colon + 1, exclamation);
                    JarFile jarFile = new JarFile(jarFileName);
                    Enumeration <JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry e = entries.nextElement();
                        String jarEntryName = e.getName();
                        if (!jarEntryName.endsWith("/") && jarEntryName.startsWith(packageAsDirName)) {
                            list.add(jarEntryName);
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Not sure what to do with this URL" + url);
            }
        }
        return list.toArray(new String[0]);
    }

    public static List <Class <?>> findAnnotatedClasses
            (String packageName, Class <? extends Annotation> annotationClass) throws Exception {
        List <Class <?>> ret = new ArrayList <>();
        String[] classes = getPackageContent(packageName);
        for (String clazz : classes) {
            clazz = clazz.replace('/', '.').substring(0, clazz.indexOf(".class"));
            Class <?> c = Class.forName(clazz);
            if (c.isAnnotationPresent(annotationClass)) ret.add(c);
        }

        return ret;
    }
}
