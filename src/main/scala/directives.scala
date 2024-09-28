package directives

import program.TypeName
import program.Line

/** Singleton for dealing with Saturn directives.
 *  
 *  **Explanation.**
 *  Directives - are special lines in Saturn standard library (std) files,
 *  used for doing some special actions protected from language user access.
 *  
 *  For example, on the one hand built-in types must be imported when
 *  corresponding std's files are imported, on the other hand they
 *  cannot be described by Saturn program syntax directly. The solution
 *  is to use directives that tell the compiler to add the required built-in
 *  types when it reads content of corresponding std' files. At the same
 *  time it will look like usual import for language user.
 *
 *  @note Provides inteface for mutable state in global scope.
 *  
 *  @see [[program.BuiltInTypes]]
 */
object DirectiveReader {

    /** Reads line.
     *
     *  Tries to invoke directive's command and argument from a line and 
     *  if successful, then does corresponding actions and returns true,
     *  does nothing and returns false otherwise.
     *
     *  @param line a line of Saturn code
     *  @returns true if directive from the line was applied, false otherwise
     *
     *  @note Updates inner state
     *
     *  @throws Exception if support for the read directive was not
     *  added for compilator code
     */
    def readLine(line: Line): Boolean = {
        if (!line.startsWith(callPart)) return false
        val Array(command, argument) = line.drop(callPart.length).split(" ")
        command match {
            case "builtintype" => builtInTypes += argument
            case _ => Exception("Not supported command") // for debug stage
        }
        true
    }

    /** Set of built-in types, that should be imported according to directives. */
    def importedBuiltInTypes: Set[TypeName] = this.builtInTypes


    /** Inner updatable buffer for built-in types, 
     *  that should be imported according to directives. */
    private var builtInTypes: Set[TypeName] = Set.empty


    /** Special begining of directive line to make sure there
     *  will be no collision with the left part of any rule.
     */
    private val callPart: String = "#directive_qJJWbUocQBa_"

}

