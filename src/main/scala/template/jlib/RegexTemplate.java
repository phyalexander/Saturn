package template.jlib;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 *
 */
public final class RegexTemplate implements Template, Serializable, Comparable<RegexTemplate> {

    @Serial
    private static final long serialVersionUID = 108024218808227L;

    private final Pattern pattern;

    public RegexTemplate(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Match tryMatch(CharSequence expression) {
        final var m = pattern.matcher(expression);
        if (m.find()) {
            final Match match = new Match(
                    (short) m.start(),
                    (short) m.end(),
                    new String[m.groupCount()]
            );
            for (int i = 0; i < match.groups.length; i++)
                match.groups[i] = m.group(i + 1);
            return match;
        } else {
            return null;
        }
    }

    @Override
    public Template copy() {
        return new RegexTemplate(Pattern.compile(pattern.pattern()));
    }

    @Override
    public String[] getContexts() {
        return Utils.regexToContexts(pattern.pattern());
    }

    @Override
    public String getRegex() {
        return pattern.pattern();
    }

    @Override
    public String toString() {
        return pattern.pattern();
    }

    @Override
    public int compareTo(RegexTemplate regexTemplate) {
        return this.pattern.toString()
                .compareTo(regexTemplate.pattern.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegexTemplate) {
            return this.pattern.toString()
                    .equals(((RegexTemplate) obj).pattern.toString());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return this.pattern.toString().hashCode();
    }
}
