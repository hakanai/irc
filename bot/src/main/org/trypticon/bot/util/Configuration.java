package org.trypticon.bot.util;

/**
 * Utility class to hold typed properties.
 *
 * No type-checking is done on these properties, except to throw IllegalArgumentException from those
 * methods which do type conversion.
 *
 * @author Trejkaz
 */
public class Configuration {
    private java.util.Properties properties = new java.util.Properties();

    /**
     * Sets a property as a string.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    /**
     * Gets a property as a string.
     *
     * @param name the property name.
     * @return the property value, as a string.
     */
    public String getString(String name) {
        return properties.getProperty(name);
    }

    /**
     * Gets a property as an integer.
     *
     * @param name the property name.
     * @return the property value, as an integer.
     * @throws NumberFormatException if the desired property is not parsable as an integer.
     */
    public int getInt(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }
}
