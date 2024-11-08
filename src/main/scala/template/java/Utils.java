package template.java;

import java.util.ArrayList;

public final class Utils {

    private Utils() {}

    static String[] regexToContexts(String regex) {
        final var _regex = unescapeRegex(regex);
        final var indexes = new ArrayList<Integer>(10);
        final int iStep = regexVariableRepresentation.length();
        int i = 0;
        while (i != -1) {
            i = _regex.indexOf(regexVariableRepresentation, i);
            if (i == -1) break;
            indexes.add(i);
            i += iStep;
        }

        final int n = indexes.size();
        final var _indexes = new int[n];
        for (i = 0; i < n; i++) _indexes[i] = indexes.get(i);
        return createContexts(_regex, _indexes);
    }

    static String[] createContexts(String expression, int[] variablePositions) {
        String[] contexts = new String[variablePositions.length];
        contexts[0] = "";
        int i = 0;
        for (i = 0; i < variablePositions.length - 1; i++)
            contexts[i+1] = expression.substring(variablePositions[i] + 1, variablePositions[i+1]);
        contexts[i+1] = expression.substring(variablePositions[i]+1);
        return contexts;
    }

    static String contextsToRegex(String[] contexts) {
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

    private static String escapeRegex(String s) {
        // TODO: maybe make more efficient version
        return program.RegexSpecials.escape(s);
    }

    private static String unescapeRegex(String s) {
        // TODO: maybe make more efficient version
        return program.RegexSpecials.unescape(s);
    }

    static final String regexVariableRepresentation = "(.+?)";
}
