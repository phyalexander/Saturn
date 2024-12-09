package language_attributes

import java.io.{File, FileNotFoundException}

/** Provides Saturn attributes like comma-syntax and file extensions. */
object LanguageAttributes {

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

    /** File, containing path to directory, where Saturn was installed */
    final val SATURN_HOME: File = {
        val path = Option(System.getenv("HOME"))
          .orElse(Option(System.getProperty("user.home")))
          .getOrElse(throw FileNotFoundException("Can't find HOME directory"))
        File(path, ".Saturn")
    }

    val VERSION: String = "0.3.0"
}
