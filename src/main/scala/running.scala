package running

import scala.jdk.StreamConverters._
import scala.collection.immutable.ArraySeq

import program.{Program, Task, Rule}




def runProgram(program: Program): Iterable[(String, String)] = {
    for (task <- program.tasks) yield {
        val taskCopy = task.clone
        while (runTask(taskCopy, program) > 0) {} // NOTE: not using parallel now
        (task.toString, taskCopy.toString)
    }
}


// changes task
def runTask(task: Task, program: Program): Int = program.rules.iterator
    .map(_.applyMutable(task)).count(identity)





def runProcedures(task: String): String = {
    // TODO: add procedures, scala-calls

    // Найти процедурные вызовы вида "name(agrument)"
    // далее подвергнуть выполнению в порядке в котором встречаются
    // а если вложенные?? - нужен рекурсивный обход

    // Тут ещё нужно подумать над концептом
    ???
}

























//
