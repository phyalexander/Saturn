import program.Program
import exceptions.*
import virtual_machine.SaturnVirtualMachine.execute
import language_syntax.LanguageSyntax

object Main {

    def main(args: Array[String]): Unit = {
        if (args.length < 1) {
            println("Error: A command expected. Use 'help' for help.")
            return
        }

        args(0) match {
            case "run"     => runCommand(args)
            case "compile" => compileCommand(args)
            case "help"    => println(HELP_MESSAGE)
            case "version" => println("Saturn language tool ver. 1.0.0")
            case cmd       => println(s"Illegal command: $cmd")
        }
    }
}


def runCommand(args: Array[String]): Unit = {
    if (args.length < 2) {
        println("Error: No Saturn file was given. Use 'help' for help.")
        return
    }

    for (path <- args.drop(1)) {
        try {
            if (path.endsWith(LanguageSyntax.fileExtension)) {
                run(compile(path))
            } else if (path.endsWith(".ser")) {
                run(Program.readFromFile(path))
            } else {
                println(s"Unsupported file extension: $path")
            }
        } catch {
            case ex: CompilationError => ex.printMessage()
            case ex: FileNotFoundError => println(ex.getMessage)
            case ex: BrokenSerializedProgramFileError => println(ex.getMessage)
            case ex: Exception => println(ex.getMessage)
        }
    }
}

def compileCommand(args: Array[String]): Unit = {
    if (args.length < 2) {
        println("Error: No Saturn source file was given. Use 'help' for help.")
        return
    }

    val (filePaths, proc) = if (args(1) == "-json") {
        (args.drop(2), (prog: Program, name: String) => json.saveToJson(
            prog, name.replace(LanguageSyntax.fileExtension, ".json")))
    } else {
        (args.drop(1), (prog: Program, name: String) => prog.saveToFile(
            name.replace(LanguageSyntax.fileExtension, ".ser")))
    }

    for (path <- filePaths) {
        try {
            if (path.endsWith(LanguageSyntax.fileExtension)) {
                proc(compile(path), path)
            } else {
                println(s"Unsupported file extension: $path")
            }
        } catch {
            case ex: CompilationError => ex.printMessage()
            case ex: FileNotFoundError => println(ex.getMessage)
            case ex: Exception => println(ex.getMessage)
        }
    }
}


def run(program: Program): Unit = {
    for (((task, result), i) <- execute(program).zipWithIndex) {
        val prefix = "TASK " ++ i.toString ++ ": "
        println(prefix ++ task)
        println("RESULT:" ++ " " * (prefix.length - 7) ++ result ++ "\n")
    }
}


def compile(sourceFile: String): Program = {
    val fullProgramText = building.buildFromModules(sourceFile)
    compilation.compileProgram(fullProgramText)
}


val HELP_MESSAGE: String = """
Saturn language tool for managing source code.
	©phyalexander

Usage:

	saturn.sh <command> <options> [path to file]

The commands are:

	run                     run program from .saturn or .ser file
	compile                 compile program into .ser file
	compile     -json       compile program into .json file
	version                 print Saturn version
	help                    print this help message

Json files are needed for running by other implementations
of Saturn Virtual Machine.

"""
