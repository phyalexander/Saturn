package program

import scala.collection.StringOps
import java.io.File

import language_syntax.LanguageSyntax


/** A line in Saturn program file.
 *
 *  @param content text of the line
 *  @param position number of the line in the file, it was read from
 *  @param source file the line was read from
 */
case class Line(content: String, position: Int, source: File) {

    /** Full information about the line.
     *
     *  @return content, position and source file composed into a string
     *  @note used for error messages
     */
    def info: String = s"$content at line $position in $source"

    /** Removes commentary parts from the line.
     *
     *  @note Commentary sign is defined at [[language_syntax.LanguageSyntax]].
     *  @return new line with removed commentary
     */
    def removeCommentary: Line =
        copy(content = content.split(LanguageSyntax.commentBegining)(0))
}

/** Line implicit conversions. */
object Line {

    /** Implicit access to [[Line.content]]. Allows Line act line a String. */
    implicit def lineToString(line: Line): String = line.content

    /** Implicit access to [[Line.content]]. Allows Line act line a StringOps. */
    implicit def lineToStringOps(line: Line): StringOps = line.content
}
