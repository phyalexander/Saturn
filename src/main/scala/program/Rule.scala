package program

import scala.util.matching.Regex
import scala.util.{Either, Left, Right}
import scala.collection.mutable
import scala.collection.immutable.ArraySeq
import scala.annotation.tailrec

import java.io.Serializable

import exceptions.*
import template.Template
import utils.{clearBackSlash, trySplit}



/** A rule in the Saturn language.
 *
 * Describes the substitution of the right part of the rule in place of
 * the task's subformula (substring) that matches the left part of the rule.
 *
 * @constructor Creates a new `Rule` with a specified metaformula and substitution formula.
 * @param left The `Metaformula` used to match a subformula within a task.
 *             It defines the pattern that should be found in the task.
 * @param right The `Formula` to substitute in place of the matched subformula.
 *              It represents the transformation to apply when the pattern is matched.
 *
 * @note The `Rule` class is serializable, allowing it to be easily saved and loaded
 *       for use in different contexts or sessions.
 *
 * @see [[Metaformula]], [[Formula]], [[Task]]
 */
@SerialVersionUID(108024218808227L)
case class Rule(left: Metaformula, right: Formula) extends Serializable {

    /** Applies the rule to a given task.
     *
     * Attempts to match the `left` metaformula with any subformula in the task.
     * If a match is found, substitutes the captured values of variables `left`
     * metaformula instead of variables' names in the `right` formula.
     *
     * @param task The task to which the rule is applied.
     * @return A tuple of the substituted formula and the start and end indices
     *         of the matched subformula, or `None` if no match is found.
     */
    def apply(task: Task): Option[(Formula, Int, Int)] = left.matchWith(task)
        .map((name2value, i, j) => (substituteValues(right, name2value), i, j))


    /** Mutably applies the rule to a given task.
     *
     * Calls for [[Rule.apply]] and if it resulted in a substitution,
     * uses it to modify the matched subformula directly within the given task.
     *
     * @param task The task to which the rule is applied.
     * @return `true` if the rule was successfully applied and the task was modified,
     *         `false` otherwise.
     *
     * @see [[Formula]]
     */
    def applyMutable(task: Task): Boolean = this.apply(task)
        .map((formula, i, j) => task.replace(i, j, formula))
        .isDefined
}


/** Rule factory. Provides methods for parsing Saturn program lines
 * with Rule definitions.
 */
object Rule {

    /** Creates a new rule by parsing a single program line.
     *
     * This method takes a line of expression and a map of type names to types, and parses the line
     * to create a `Rule` object. The expression line must be of the form "left -> right", where
     * "left" defines variable names and their types, and "right" is the resulting expression.
     *
     * @param expressionLine The line of expression to parse, expected to be in the format "left -> right".
     * @param typeMap A map that associates type names with their corresponding `Type` objects.
     * @return A `Rule` object constructed from the parsed expression line.
     * @throws SyntaxError If the expression line does not contain exactly one ' -> ' separator.
     * @throws NotFoundError If a type name in the expression line is not found in the `typeMap`.
     *
     * @note The current implementation assumes that variable names and types in the "left" part
     *       are separated by a colon and a space (e.g., "varName: TypeName"). The method also
     *       expects that variable names in the "left" part are separated by spaces.
     *
     * @see [[Line]], [[Type]], [[Rule]]
     *
     * @todo Refactor this method to split it into shorter, more manageable functions.
     * @todo Consider implementing a method to handle backslashes more effectively.
     */
    def fromSingleLine(expressionLine: Line, typeMap: Map[TypeName, Type]): Rule = {
        // TODO: refactoring; split on short functions.
        // COMBAK: clearBackSlash
        val parts = expressionLine.split(" -> ")
        if (parts.length != 2)
            throw SyntaxError("Rule must contain one ' -> '", expressionLine)

        var name2type = raw"(\w+)\s?: (\w+)".r.findAllMatchIn(parts(0))
            .map(m => (m.group(1), m.group(2)))
            .map((vname, tname) => (vname, typeMap.getOrElse(
                tname, throw NotFoundError(tname, expressionLine))))
            .toMap

        // val protoleft = RegexSpecials.escape()
        // val left = name2type.keys.foldLeft(protoleft)(_.replace(_, raw"(\S+)")).r
        val template = Template(raw" : \w+".r.replaceAllIn(parts(0), ""), name2type.keys)

        val right = parts(1)
        val namesWithTypes = parts(0).split(" ")
            .filter(name2type.contains(_))
            .map(name => (name, name2type(name)))

        new Rule(Metaformula(template, ArraySeq.from(namesWithTypes)), right)
    }


    /**
     * Creates a new rule from two program lines: a type declaration line and an expression line.
     *
     * This method parses a type declaration line and an expression line to create a `Rule` object.
     * The type declaration line specifies variable names and their types, while the expression line
     * defines the rule in the form "left -> right".
     *
     * @param typeLine The line specifying variable names and their types, expected to be in the format
     *                 "varName: TypeName, varName2: TypeName2, ...".
     * @param expressionLine The line of expression to parse, expected to be in the format "left -> right".
     * @param typeMap A map that associates type names with their corresponding `Type` objects.
     * @return A `Rule` object constructed from the parsed type and expression lines.
     * @throws SyntaxError If the type declaration line has invalid annotations or if a variable is declared multiple times.
     * @throws SyntaxError If the expression line does not contain exactly one ' -> ' separator.
     * @throws NotFoundError If a type name in the type declaration line is not found in the `typeMap`.
     *
     * @note The current implementation assumes that variable names and types in the type declaration line
     *       are separated by a colon and a space (e.g., "varName: TypeName") and that multiple declarations
     *       are separated by commas.
     *
     * @see [[Line]], [[Type]], [[Rule]]
     *
     * @todo Refactor this method to split it into shorter, more manageable functions.
     * @todo Consider implementing a method to handle backslashes more effectively.
     */
    def fromTwoLines(typeLine: Line, expressionLine: Line, typeMap: Map[TypeName, Type]): Rule = {
        // TODO: refactoring; split on short functions.
        // COMBAK: clearBackSlash
        var name2type: Map[VarName, Type] = Map.empty
        for (declaration <- typeLine.split(", ")) {
            val parts = declaration.split(": ")
            if (parts.length != 2)
                throw SyntaxError("Invalid type annotation, ' : ' expected", typeLine)

            if (name2type.contains(parts(0)))
                throw SyntaxError(s"Variable ${parts(0)} is declared multiply times", typeLine)

            val aType = parts(1).split(" ")
                .map(tname => typeMap.getOrElse(tname, throw NotFoundError(tname, expressionLine)))
                .reduce(_ | _)
            name2type += (parts(0).strip -> aType)
        }

        val (left, right) = trySplit(expressionLine, " -> ")(
            throw SyntaxError("Rule must contain ' -> '", expressionLine)
        )

        // val escaped = RegexSpecials.escape(left)
        // val regex = name2type.keys.foldLeft(escaped)(_.replace(_, raw"(\S+)")).r
        val template = Template(left, name2type.keys)

        val namesWithTypes = getSubstringsAppearences(name2type.keys, left)
            .map(name => (name, name2type(name)))

        new Rule(Metaformula(template, ArraySeq.from(namesWithTypes)), right)
    }
}



// TODO: write docstring
def getSubstringsAppearences(substrings: Iterable[String], string: String): List[String] = {
    var str2index: List[(Int, String)] = Nil
    var i = 0
    for (sub <- substrings) {
        i = 0
        var flag = true
        while (flag) {
            i = string.indexOf(sub, i)
            if (i == -1) flag = false
            else str2index = str2index.prepended((i, sub)); i += 1
        }
    }
    str2index.sortBy(_._1).map(_._2)
}


/** Substitutes variable names in a formula with their corresponding values.
 *
 *  This inline function takes a formula and a map of variable names to values,
 *  and replaces all occurrences of the variable names in the formula with the
 *  specified values.
 *
 *  @param formula The original formula in which variable names are to be substituted.
 *  @param name2value A `Map` where keys are variable names and values are the
 *                   corresponding values.
 *  @return new formula with all specified variable names replaced by their values.
 *
 *  @see [[Formula]], [[VarName]], [[Value]]
 */
inline def substituteValues(formula: Formula, name2value: Map[VarName, Value]): Formula = {
    name2value.foldLeft(formula)((f, entry) => f.replace(entry._1, entry._2))
}
