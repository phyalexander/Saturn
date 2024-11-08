package template

import program.Formula
import java.Template as JavaTemplate
import java.{ContextsTemplate, RegexTemplate, Match}

import scala.util.matching.Regex

/**
 * Wrapper for java package template classes.
 * Used as point of implementation version switching.
 *
 * @note Is used by [[program.Metaformula]]
 * */
final class Template private(private var inner: JavaTemplate) {

    def tryMatch(formula: Formula): Match = inner.tryMatch(formula)
}

object Template {

    def apply(regex: Regex): Template = new Template(new RegexTemplate(regex.pattern))

    def apply(contexts: Array[Formula]): Template = new Template(new ContextsTemplate(contexts))
}