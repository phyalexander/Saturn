package template

import program.{Formula, RegexSpecials, VarName}
import jlib.{ContextsTemplate, Match, RegexTemplate, Utils, Template as JavaTemplate}

import scala.util.matching.Regex

/**
 * Wrapper for java package template classes.
 * Used as point of implementation version switching.
 *
 * @note Is used by [[program.Metaformula]]
 * */
@SerialVersionUID(108024218808227L)
final class Template private(private var inner: JavaTemplate) extends Serializable {

    def tryMatch(formula: CharSequence): Option[Match] = Option(inner.tryMatch(formula))

    override def equals(obj: Any): Boolean = inner.equals(obj)

    override def copy: Template = new Template(inner.copy())
}

object Template {

    private val templateType = JavaTemplate.TemplateType.REGEX

    def apply(expression: Formula, variableNames: IterableOnce[VarName]): Template = {
        templateType match {
            case JavaTemplate.TemplateType.REGEX => {
                val escaped = RegexSpecials.escape(expression)
                val regex = variableNames.iterator.foldLeft(escaped)(_.replace(_, RVR)).r
                new Template(new RegexTemplate(regex.pattern))
            }
            case JavaTemplate.TemplateType.CONTEXTS => {
                val indexes = variableNames.iterator.map(expression.indexOf).toArray
                val contexts = Utils.createContexts(expression, indexes)
                new Template(new ContextsTemplate(contexts))
            }
        }
    }

    // copy doctring from Utils.regexVariableRepresentation
    val RVR: String = Utils.regexVariableRepresentation
}