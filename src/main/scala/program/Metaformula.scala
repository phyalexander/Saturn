package program

import scala.util.matching.Regex
import scala.collection.immutable.ArraySeq
import scala.annotation.tailrec
import java.io.Serializable
import exceptions.*
import template.Template


/** A metaformula in the Saturn language. Used for pattern matching within formulas.
 *
 *  Consists of a regular expression template and a sequence of variables
 *  with their associated types. It is used to match subformulas in a task and extract
 *  variable values that satisfy the pattern and type constraints.
 *
 *  @constructor Creates a new `Metaformula` with a specified regex template and variables.
 *  @param template The regular expression which represents the structure template of
 *                 this metaformula
 *  @param variables A Sequence of tuples, where each tuple consists of a variable name
 *                  and its corresponding type. These variables are placeholders within
 *                  the template that can capture specific values.
 *
 *  @note Serializable for [[Program.saveToFile]] usage.
 *
 */
@SerialVersionUID(108024218808227L)
case class Metaformula(template: Template, variables: ArraySeq[(VarName, Type)]) extends Serializable {

    /** Attempts to match the metaformula with a given formula.
     *
     *  This method tries to find the first match of the regegular expression
     *  template in the provided formula.
     *  If a match with structure template is found, it further checks if the
     *  captured values can be associated with the defined variables,
     *  ensuring type constraints are respected.
     *
     *  @param formula The formula against which the metaformula is matched.
     *  @return A tuple with a map of variable names to their matched values,
     *          and the start and end indices of the match within the formula, if matched.
     *          Returns `None` otherwise.
     */
    def matchWith(formula: CharSequence): Option[(Map[VarName, Value], Int, Int)] = {
        template.tryMatch(formula)
          .flatMap(m => matchWithVariables(m.getGroups.iterator).map(n2v => (n2v, m.getStart, m.getEnd)))
    }

    /** Matches captured values with the variables defined in the metaformula.
     *
     *  This private method iterates through the captured values and attempts to associate them
     *  with the variables in the metaformula, ensuring that each variable's type constraint is satisfied.
     *
     *  @param values An iterator over the captured values from a regex match.
     *  @return An `Option` containing a map of variable names to their matched values if all
     *         variables are successfully matched and type constraints are satisfied.
     *         Returns `None` otherwise.
     */
    private def matchWithVariables(values: Iterator[Value]): Option[Map[VarName, Value]] = {

        @tailrec
        def helper(iter: Iterator[((VarName, Type), Value)],
                   name2value: Map[VarName, Value]): Option[Map[VarName, Value]] = {
            if (!iter.hasNext) // all pairs matched
                return Some(name2value)
            val ((name, _type), value) = iter.next

            name2value.get(name) match {
                // the variable already occurred, so the value
                // must be the same for successful match
                case Some(addedValue) => if (value != addedValue) None
                    else helper(iter, name2value) // go to next pair
                // first time variable occurrence, so the value must match type
                case None => if (!_type.contains(value)) None
                    else helper(iter, name2value.updated(name, value)) // go to next pair
            }
        }

        helper(this.variables.iterator.zip(values), Map.empty)
    }
}
