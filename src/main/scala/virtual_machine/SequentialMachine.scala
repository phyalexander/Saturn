package virtual_machine

import program.{Program, Task, Formula}

/** Sequential Saturn language virtual machine. Uses system of rules to execute
 *  program and get solutions for all tasks. Runs on a single thread.
 */
class SequentialMachine(private val program: Program) extends SaturnVirtualMachine {

    /** Tries to apply rules to all tasks in Saturn program.
     *
     *  For each task this method:
     *
    *
     *  @return pairs of tasks and solutions
     */
    override def executeProgram(): Iterator[(Formula, Formula)] = {
        for (task <- program.tasks.iterator) yield {
            val solution = task.clone
            while (runTask(solution) > 0) {}
            (task.toString, solution.toString)
        }
    }

    /** Does next stuff:
     *  1. Searches for a subformula in the given task that matches one of the
     *     rules of the program.
     *  2. If the subformula is found, modifies it with the corresponding
     *     rule; replaces it with the left part of the rule. Otherwise
     *     the solution for the current task is got      *
     *  @return pairs of tasks and solitions
     */
    def runTask(task: Task): Int = {
        var count = 0
        for (sub <- program.rules.map(_(task)); (form, i, j) <- sub) {
            task.replace(i, j, form)
            count += 1
        }
        count
    }
}
