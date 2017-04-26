package de.hpi.bpt.argos.util;

/**
 * This interface helps to set parameters in URIs.
 */
public interface RestUriUtil {
    /**
     * This method returns a parameter included with a a prefix or not.
     * @param parameterName - the parameter to update
     * @param includePrefix - if the prefix should be included
     * @return - the updated parameter
     */
    static String getParameter(String parameterName, boolean includePrefix) {
        String updatedParameterName = parameterName;
        if (includePrefix) {
            updatedParameterName = ":" + updatedParameterName;
        }
        return updatedParameterName;
    }
}
