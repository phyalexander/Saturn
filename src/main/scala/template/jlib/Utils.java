package template.jlib;

import java.util.ArrayList;

public final class Utils {

    private Utils() {}

    public static String[] regexToContexts(String regex) {
        final var expression = unescapeRegex(regex).toCharArray();

        final var contextsList = new ArrayList<String>();
        final var variable = regexVariableRepresentation.toCharArray();

        int pointer = 0;
        while (true) {
            final int varIndex = substringIndex(expression, variable, pointer);
            if (varIndex == -1) break;
            final var context = new char[varIndex - pointer];
            System.arraycopy(expression, pointer, context, 0, context.length);
            contextsList.add(new String(context));
            pointer = varIndex + variable.length;
        }

        // hate this moment...
        final var contexts = new String[contextsList.size()];
        for (int i = 0; i < contexts.length; i++)
            contexts[i] = contextsList.get(i);
        return contexts;
    }

    public static String[] charArraysToStrings(char[][] arrays) {
        final var strings = new String[arrays.length];
        for (int i = 0; i < arrays.length; i++)
            strings[i] = new String(arrays[i]);
        return strings;
    }

    // assumes all variables exist in expression
    public static char[][] createContexts(String expression, String[] variables) {
        if (variables.length == 0)
            return new char[][] {expression.toCharArray()};

        final var _expression = expression.toCharArray();
        final var contexts = new char[variables.length + 1][];

        int pointer = 0;
        for (int j = 0; j < variables.length; j++) {
            var variable = variables[j].toCharArray();
            final int varIndex = substringIndex(_expression, variable, pointer); // assumes never return -1
            contexts[j] = new char[varIndex - pointer];
            System.arraycopy(_expression, pointer, contexts[j], 0, varIndex - pointer);
            pointer = varIndex + variable.length;
        }

        final int j = contexts.length-1;
        contexts[j] = new char[_expression.length - pointer];
        System.arraycopy(_expression, pointer, contexts[j], 0, _expression.length - pointer);

        return contexts;
    }


    public static String contextsToRegex(String[] contexts) {
        if (contexts.length == 0) {
            throw new RuntimeException("Can't create RegexTemplate from empty array");
        } else if (contexts.length == 1) {
            return escapeRegex(contexts[0]);
        }

        final StringBuilder regex = new StringBuilder();
        for (int i = 0; i < contexts.length-1; i++)
            regex.append(escapeRegex(contexts[i])).append(regexVariableRepresentation);
        regex.append(escapeRegex(contexts[contexts.length - 1]));
        return regex.toString();
    }

    /**
     * Determines the index of the first occurrence of `target` within `source`.
     *
     * @note Inspired by C standard library function `strstr`, but specialized for
     * this class usage.
     *
     * @param source The string to search in
     * @param target The string to search for
     * @param start Index from which to start searching
     * @return index of the first occurrence of `target` within `source`, if found; -1 otherwise
     */
    public static int substringIndex(char[] source, char[] target, int start) {
        final int tlen = target.length;
        if (tlen == 0)
            return start;
        int i = start;
        int slen = source.length;
        while (slen >= tlen) {
            slen--;
            int j;
            for (j = 0; j < tlen && source[i + j] == target[j]; j++) {}
            if (j == tlen) return i;
            i++;
        }
        return -1;
    }

    private static String escapeRegex(String s) {
        // TODO: maybe make more efficient version
        return program.RegexSpecials.escape(s);
    }

    private static String unescapeRegex(String s) {
        // TODO: maybe make more efficient version
        return program.RegexSpecials.unescape(s);
    }

    public static final String regexVariableRepresentation = "(\\S+)";
}
