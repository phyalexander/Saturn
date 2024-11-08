package template.jlib;


import java.util.regex.Pattern;

import static template.jlib.Utils.contextsToRegex;
import static template.jlib.Utils.regexToContexts;

/**
 *
 */
public interface Template {

    /**
     * Nullable
     * @param expression
     * @return
     */
    Match tryMatch(final CharSequence expression);

    /**
     * @return
     */
    Template copy();

    /**
     * @return
     */
    String[] getContexts();

    /**
     * @return
     */
    String getRegex();

    enum TemplateType {
        REGEX,
        CONTEXTS
    }

    static Template fromContexts(TemplateType tt, final String[] contexts) {
        return switch (tt) {
            case REGEX -> new RegexTemplate(Pattern.compile(contextsToRegex(contexts)));
            case CONTEXTS -> new ContextsTemplate(contexts);
        };
    }

    static Template fromRegex(TemplateType tt, final String regex) {
        return switch (tt) {
            case REGEX -> new RegexTemplate(Pattern.compile(regex));
            case CONTEXTS -> new ContextsTemplate(regexToContexts(regex));
        };
    }

    static Template fromRegex(TemplateType tt, Pattern pattern) {
        return switch (tt) {
            case REGEX -> new RegexTemplate(pattern);
            case CONTEXTS -> new ContextsTemplate(regexToContexts(pattern.pattern()));
        };
    }


}
