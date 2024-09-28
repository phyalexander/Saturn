package building

import scala.io.Source
import scala.util.{Using, Try, Success, Failure}
import scala.util.{Left, Right}
import scala.annotation.tailrec

import java.io.File

import program.Line
import exceptions.{NotFoundError, FileNotFoundError}
import language_syntax.LanguageSyntax


def buildFromModules(mainFileName: String): Vector[Line] = {
    @tailrec
    def helper(files: Set[File], buffer: Set[File]): Set[File]= {
        if (files.isEmpty)
            return buffer
        val imports: Set[File] = files.flatMap(invokeImports(_))
        helper(imports -- buffer, buffer | imports)
    }

    val set = Set(File(mainFileName))
    helper(set, set).toVector.flatMap(readProgramFile(_))
}



def invokeImports(file: File): Iterator[File] = readProgramFile(file)
    .iterator.filter(_.startsWith("import")).map(parseImport(_))


def parseImport(line: Line): File = {
    val stripped = line.drop(6).strip
    val path = stripped.replace('.', '/') ++ LanguageSyntax.fileExtension

    // NOTE: std files are in special directory
    if (path.startsWith("std")) return File(utils.SATURN_HOME, path)

    def parent(file: File): Option[File] = Option(file.getParentFile)

    Iterator.iterate(parent(line.source))(of => of.flatMap(parent(_)))
        .takeWhile(_.isDefined)
        .map(f => File(f.get, path))
        .toVector.appended(File(path))
        .find(_.exists).getOrElse(throw NotFoundError(stripped, line))

}

/** Reads Saturn language source file.
 *  
 *  @param file A file with Saturn program code.
 *  @return List of `Line`s of Saturn program
 *  @throws exceptions.FileNotFoundError when the given file was not found
 *  @throws java.io.IOException when problems with filesystem.
 */
def readProgramFile(file: File): List[Line] = try {
    val result = Using(Source.fromFile(file)) { src =>
        src.getLines.zipWithIndex
        .map((line, i) => Line(line, i, file))
        .toList
    }
    result.get
} catch {
    case ex: java.io.FileNotFoundException => throw FileNotFoundError(file.toString)
}
