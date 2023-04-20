package com.irfansaf.safpass.util;

/**
 * Stirng utility class.
 *
 * @author Irfan Saf
 */
public final class StringUtils {
    private StringUtils() {
        // Utility Class
    }

    /**
     * This method ensures that the output String has only valid XML unicode
     * characters as specified by the XML 1.0 standard. For reference, please
     * see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty String if the input is
     * null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String stripNonValidXMLCharacters(final String in) {
        if (in == null || in.isEmpty()) {
            return in;
        }
        StringBuilder out = new StringBuilder();
        char current;
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            } else {
                out.append('?');
            }
        }
        return out.toString();
    }

    public static String stripString(String text) {
        return stripString(text, 80);
    }

    public static String stripString(String text, int length) {
        String result = text;
        if (text != null && text.length() > length) {
            result = text.substring(0, length) + "...";
        }
        return result;
    }
}
