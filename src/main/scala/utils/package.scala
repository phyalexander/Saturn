package utils

import java.io.File
import java.io.FileNotFoundException


/** Removes backslash symbols from string begining
 *  @param str string that may start with '\' 
 *  @return new string without any '\' as first character
 */
def clearBackSlash(str: String): String =
    if (str.startsWith("\\")) str.drop(1) else str


/** Tries to split a string into two parts by the given separator.
 *
 *  @param string a string to split
 *  @param seprator substring by which the main string should be splitted
 *  @param default a pair of strings used, when splitting failed
 *  @return a pair of strings - parts of splitted one if successful, 
 *  result of evaluating default otherwise
 */
def trySplit(string: String, separator: String)(default: => (String, String)): (String, String) = {
    val parts = string.split(separator)
    if (parts.length == 2) (parts(0), parts(1)) else default
}


/** File, containing path to directory, where Saturn was installed */
final val SATURN_HOME: File = {
    val path = Option(System.getenv("HOME"))
        .orElse(Option(System.getProperty("user.home")))
        .getOrElse(throw FileNotFoundException("Can't find HOME directory"))
    File(path, ".Saturn")
}
