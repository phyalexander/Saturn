package template.jlib;

import java.io.Serial;
import java.io.Serializable;

public class ContextsTemplate implements Template, Serializable {

    @Serial
    private static final long serialVersionUID = 108024218808227L;

    private final char[][] contexts;

    public ContextsTemplate(String[] contexts) {
        this.contexts = new char[contexts.length][];
        for (int i = 0; i < contexts.length; i++)
            this.contexts[i] = contexts[i].toCharArray();
    }

    public ContextsTemplate(char[][] contexts) {
        this.contexts = contexts;
    }

    @Override
    public Match tryMatch(CharSequence _expression) {
        var expression = _expression.toString().toCharArray();
        int i = 0; // index of an expression char

        final int start = Utils.substringIndex(expression, contexts[0], 0);
        if (start == -1)
            return null;

        final int groupsSize = contexts.length - 1;
        final String[] groups = new String[groupsSize];
        i = start + contexts[0].length;

        for (int k = 1; k < groupsSize && i != -1; k++) {
            int j = Utils.substringIndex(expression, contexts[k], i);
            if (j == -1)
                return null;
            groups[k-1] = _expression.subSequence(i, j).toString();
            i = j + contexts[k].length;
        }

        int end;
        final int last = groupsSize;
        if (contexts[last].length == 0) {
            end = expression.length;
        } else {
            end = Utils.substringIndex(expression, contexts[last], i);
            if (end == -1)
                return null;
        }

        groups[groupsSize - 1] = _expression.subSequence(i, end).toString();
        return new Match(start, end, groups);
    }

    @Override
    public Template copy() {
        return new ContextsTemplate(contexts);
    }

    @Override
    public String[] getContexts() {
        var contexts = new String[this.contexts.length];
        for (int i = 0; i < contexts.length; i++)
            contexts[i] = new String(this.contexts[i]);
        return contexts;
    }

    @Override
    public String getRegex() {
        return Utils.contextsToRegex(getContexts());
    }
}
