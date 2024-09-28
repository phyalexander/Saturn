package program

import scala.collection.mutable

/** String name of a variable in Saturn rule. */
type VarName = String

/** String name of a type in Saturn. */
type TypeName = String

/** String value of a variable in Saturn rule while applying. */
type Value = String

/** String formula/expression in Saturn. */
type Formula = String

/** StringBuilder formula that is a Saturn task representation. */
type Task = mutable.StringBuilder


/** Service object that provides extra tools for work with regular expressions. */
object RegexSpecials {

    /** Special control symbols of regular expressions (RSS). */
    val symbols = Array("\\", "[", "]", "/", "^", "$", ".",
                        "|", "?", "*", "+", "(", ")", "{", "}")

    /** Escapes regular expressions' special symbols (RSS) in a string.
     *
     *  @param string a string that may contain RSS
     *  @return new string where each RSS is escaped with '\'
     */
    def escape(string: String): String =
        symbols.foldLeft(string)((s, c) => s.replace(c, "\\" + c))
}
