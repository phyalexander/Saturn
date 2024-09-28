package virtual_machine

import program.{Program, Formula}


// TODO: docs for main, running(this), building, compilation
// Main arguments
// parse checkers (test what happens?)
// todo repl
// todo scalacall


/** Saturn language virtual machine. Uses system of rules to execute
 *  program and get solutions for all tasks.
 *
 *  There are two versions of this machine: sequential and parallel ones.
 *  The first one is used for cases when there are not many rules
 *  and there is no sense to run more than one thread.
 *  The second one is used for programs with large amounts of rules,
 *  so we can make executing much faster by spliting the calculations
 *  on few independent branches.
 *
 *
 *  The only method of this trait is `executeProgram`.
 *
 *  @see [[virtual_machine.SequentialMachine]]
 *  @see [[virtual_machine.ParallelMachine]]
*/
trait SaturnVirtualMachine {

    /** Tries to apply rules to all tasks in Saturn program.
     *
     *  Concrete actions depend on child-class implementation, but generally
     *  does the next stuff for each task in the program.
     *
     *  1. Searches for a subformula in the task that matches one of the
     *     rules of the given program.
     *  2. If the subformula is found, modifies it with the corresponding
     *     rule; replaces it with the left part of the rule. Otherwise
     *     the solution for the current task is got and machine switches to the
     *     next task.
     *
     *  @return pairs of tasks and solutions
     */
    def executeProgram(): Iterator[(Formula, Formula)]
}


object SaturnVirtualMachine {

    /** Minimal number of tasks per thread in parallel mode. */
    private val minChunkSize = 15

    /** Chooses the optimal mode (sequential or parallel), creates
     *  new SaturnVirtualMachine, using the chosen mode and 
     *  executes program with this machine.
     *
     *  For more details read documentation for [[virtual_machine.SaturnVirtualMachine]]
     */
    def execute(program: Program): Iterator[(Formula, Formula)] = {
        val machine = if (program.rules.size > minChunkSize*2) {
            // let's run parallel to speed up
            val cores = Runtime.getRuntime.availableProcessors - 1 // 1 for MainThread
            val parts = (program.rules.size / minChunkSize).min(cores)
            ParallelMachine(program, parts)
        } else {
            SequentialMachine(program) // no sence to run parallel
        }

        machine.executeProgram()
    }
}


















//
