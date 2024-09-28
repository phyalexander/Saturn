package exceptions

import program.Line

/** Class of errors occuried while attempt to read serialized Saturn
 *  program object from a file.
 *
 *  It may happen when reading file with no serialized program object
 *  or when it was modificated incorrectly.
 *
 *  @constructor creates new `BrokenSerializedProgramFileException`.
 *  @param filePath the path to the file which deserialization turned into error.
 */
class BrokenSerializedProgramFileError(filePath: String) extends Exception(
    s"Can't invoke compiled Saturn program from file $filePath")


/** A error for cases when file was not found.
 *
 *  Used instead of java.io.FileNotFoundException for better error message.
 *
 *  @param path The path to the file that was not found.
 */
class FileNotFoundError(path: String) extends Exception(s"Can't find file $path")



/** Base class for any errors, occurred uring attempt to compile a Saturn program.
 *
 *  @constructor creates a new compilation error with a string message, string name and link to line object
 *  @param mesage explanation message of this error
 *  @param category this error category
 *  @param line the line whose parsing resulted in an error
 */
abstract class CompilationError(message: String,
                                val category: String,
                                val line: Line) extends Exception(message) {

    /** Prints full mesage for this error */
    final def printMessage(): Unit = {
        // COMBAK: may be steal format from python/rust etc. Add explanations
        println("=" * 80)
        println(s"$category at line ${line.position} in ${line.source}:")
        println("\tLINE: " ++ line.content)
        for (l <- this.getMessage.linesIterator) println("\t| " ++ l)
        println("=" * 80 + "\n")
    }
}


/** A syntax error occurred during compilation.
 *
 *  @constructor creates a new syntax error from message and link to line object
 *  @param msg explanation message of this error
 *  @param line the line whose parsing resulted in an error
 */
class SyntaxError(msg: String, line: Line) extends CompilationError(msg, "Syntax Error", line)


/** A error for cases when an item was not found during compilation.
 *
 *  @constructor creates a new error from item name and link to line object
 *  @param msg explanation message of this error
 *  @param line the line whose parsing resulted in an error
 */
class NotFoundError(item: String, line: Line) extends CompilationError(
    s"Cannot find item `$item`", "Not Found Error", line) {


    /** Joines two NotFound errors into single error.
     *
     *  @todo not implemented
     *  @note Use to collect all errors about unfounded items in one error
     *  instead for throwing error for only one unfound item.
     *  @param other another NotFound error to join with
     *  @return new NotFound error made by joining this one and the other one
     */
    @deprecated("Not implemented yet")
    def join(other: NotFoundError): NotFoundError = ??? // TODO: IMPLEMENT
}

