package language_syntax

/** Provides Saturn syntax elements line comma-symbols. */
object LanguageSyntax {

    /** Extension of Saturn program file. */
    val fileExtension: String = ".saturn"

    /** Name of Anything type, used in Saturn programs.
     *  
     *  @see [[program.BuiltInTypes.AnyType]]
     *  */
    val anythingTypeName: String = "Any"

    /** Commentary begining symbols. */
    val commentBegining: String = "--"

    /** Gag symbol for representing a lack of something that could be.*/
    val nothing: String = "_"
}
