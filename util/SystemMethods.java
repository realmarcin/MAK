package util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;


/**
 * User: marcin
 * Date: Jul 16, 2007
 * Time: 5:55:09 PM
 */
public class SystemMethods {

    /**
     *
     */
    public static void printCLASSPATH() {
        //Get the SystemMethods Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
        //Get the URLs
        URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i].getFile());
        }
    }

    /**
     *
     */
    public static void printLD_LIBRARY_PATH() {
        String s = System.getProperty("java.library.path");
        System.out.println(s);
    }

    /**
     *
     */
    public static void printAllProperties() {
        // Get all system properties
        Properties props = System.getProperties();
        // Enumerate all system properties
        for ( Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            // Get property name
            String propName = (String) e.nextElement();
            // Get property value
            String propValue = (String) props.get(propName);
        }
    }

}
