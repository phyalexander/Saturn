package template.jlib;



/**
 * Represents a match found in a string, containing information about the
 * start and end positions of the match, as well as any captured groups.
 *
 * <p>This class is immutable and final, meaning that once an instance is
 * created, its state cannot be changed.</p>
 *
 * <p>Instances of this class are typically created as a result of
 * searching for patterns in a text, such as with regular expressions.</p>
 *
 *
 */
public final class Match {

    /** The starting position of the match in the text. */
    final int start;

    /** The ending position of the match in the text. */
    final int end;

    /** An array of captured groups from the match. */
    final String[] groups;

    /**
     * Constructs a new Match instance with the specified start and end
     * positions and captured groups.
     *
     * @param start the starting position of the match
     * @param end the ending position of the match
     * @param groups an array of strings representing the captured groups
     */
    Match(int start, int end, String[] groups) {
        this.start = start;
        this.end = end;
        this.groups = groups;
    }

    /**
     * Returns the starting position of the match.
     *
     * @return the starting position of the match
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the ending position of the match.
     *
     * @return the ending position of the match
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns an array of strings representing the captured groups from
     * the match.
     *
     * @return an array of captured groups
     */
    public String[] getGroups() {
        return groups;
    }
}

