package virtual_machine

import scala.collection.mutable

import java.util.concurrent.CyclicBarrier

import utils.Slice
import program.{Program, Rule, Formula, Task}


class ParallelMachine(program: Program, executorNumber: Int) extends SaturnVirtualMachine {

    override def executeProgram(): Iterator[(Formula, Formula)] = {
        executors.foreach(_.start())
        executors.head.join() // wait till end of calculations
        program.tasks.iterator.map(_.toString).zip(solutions)
    }

    private var tasks = program.tasks.toList.map(_.clone)
    private val solutions = mutable.Queue.empty[Formula]

    private val barrier: CyclicBarrier = new CyclicBarrier(executorNumber, () => {
        var count = 0
        for (ex <- executors; (form, i, j) <- ex.substitutions) {
            currentTask.replace(i, j, form)
            count += 1
        }
        if (count == 0) {
            solutions.enqueue(currentTask.toString)
            tasks = tasks.tail
        }
    })

    private val executors: List[Executor] = Slice
        .makeSlices(program.rules, executorNumber).map(Executor(_)).toList

    private inline def currentTask: Task = tasks.head

    class Executor(val rules: Slice[Rule]) extends java.lang.Thread {
        val substitutions: mutable.ArrayBuffer[(Formula, Int, Int)] = new mutable.ArrayBuffer()

        override def run(): Unit = while (tasks.nonEmpty) {
            try {
                substitutions.clear()
                for (rule <- rules) rule(currentTask).foreach(substitutions += _)
                barrier.await()
            } catch {
                case e: Exception => {
                    println(e.getMessage)
                    System.exit(1)
                }
            }
        }
    }


    // /** Old code, may be will return to this in the future... */
    // private def runTaskParallel(task: Task, program: Program): Int = {
    //     // NOTE: benches showed that using Java Stream API is 5-10 times
    //     //       faster than scala.collection.parallel for this task
    //     var count = 0
    //     program.rules.asJavaParStream
    //         .map(_(task))
    //         .filter(_.isDefined).map(_.get)
    //         .sequential
    //         .forEach((formula, i, j) => {task.replace(i, j, formula); count += 1})
    //     count
    // }
}
