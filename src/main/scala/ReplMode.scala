package repl_mode

import program.*
import exceptions.*
import language_syntax.LanguageSyntax

import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.io.StdIn.readLine

import java.io.File


class ReplMode {

    private val codeBase = new ReplMode.Stack[Line]()
    private var nonCompiledLinesNumber = 0
    private var program = Program(ArraySeq.empty[Rule], ArraySeq.empty[Task])
    private val importList = new mutable.ArrayBuffer[String]()

    def start(): Unit = {
        var continue = true
        while (continue) {
            print(">> ")
            val line = readLine()
            if (line.startsWith("task: "))
                runTaskLine(line)
            else if (line.startsWith("import"))
                importFile(line)
            else if (line == "exit")
                continue = false
            else if (line.startsWith("--") | line == "")
                {}
            else if (line.startsWith(":"))
                runReplCommand(line)
            else
                updateProgramWith(line)
        }
    }

    private def runTaskLine(taskLine: String): Unit = {
        val task = new mutable.StringBuilder(taskLine.drop(6))
        running.runTask(task, program)
        println(task.toString)
    }


    private def updateProgramWith(line: String): Unit = {
        codeBase.push(line)
        nonCompiledLinesNumber += 1
        if (predicate(line)) {
            try {
                program = compilation.compileProgram(codeBase)
                nonCompiledLinesNumber = 0
            } catch {
                case ex: CompilationError => ex.printMessage()
                case ex: FileNotFoundError => println(ex.getMessage)
                case ex: Exception => println(ex.getMessage)
            } finally {
                for (_ <- 1 to nonCompiledLinesNumber)
                    val _ = codeBase.pop()
                nonCompiledLinesNumber = 0
            }
        }
    }

    private def importFile(importLine: String): Unit = {
        val importName = importLine.drop(6).strip
        if (importList.contains(importName)) {
            println(s"Already imported: $importName")
            return
        }
        importList += importName
        val f = building.parseImport(importLine).toString
        building.buildFromModules(f)
            .filter(!_.content.startsWith("task:"))
            .foreach(codeBase.push(_))
        program = compilation.compileProgram(codeBase)
    }

    private def runReplCommand(cmd: String): Unit = cmd match {
        case ":del" => {
            val line = codeBase.pop()
            println(s"deleted ${line.info}")
            nonCompiledLinesNumber = 0.max(nonCompiledLinesNumber - 1)
        }
        case ":types" => codeBase.iterator.map(_.content)
            .filter(_.startsWith("type")).foreach((s: String) => println("\t" ++ s))
        case ":imports" => importList.foreach((s: String) => println("\t" ++ s))
        case ":help" => {
            println("================= HELP MENU =================")
            println("\t:del      deletes previous code line")
            println("\t:types    prints list of all defined types")
            println("\t:imports  prints list of all imports")
            println("\texit     exits this REPL mode")
        }
        case other => {
            println("This command is not supported in the current version")
            return

            if (other.startsWith(":unimport ")) {
                val importName = other.drop(10)
                val i = importList.indexOf(importName)
                if (i == -1) {
                    println(s"Is not imported: $importName")
                    return
                }
                importList.remove(i)
                val file = File(importName.replace('.', '/') ++ LanguageSyntax.fileExtension)
                codeBase.filterInPlace(_.source == file)
            }

        }
    }


    // TODO: rename
    private def predicate(l: String): Boolean = (l.startsWith("import")
        | l.startsWith("type") | l.contains("->") | l.startsWith("--"))

    private implicit def toLine(str: String): Line =
        Line(str, codeBase.size, new File("REPL"))

}

object ReplMode {

    def start(): Unit = {
        val r = new ReplMode()
        r.start()
    }

    private class Stack[A] extends mutable.Stack[A] {
        override def iterator(): Iterator[A] = reverseIterator
    }
}
