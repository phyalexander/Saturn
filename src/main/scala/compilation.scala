package compilation

import scala.util.matching.Regex
import scala.util.{Either, Left, Right}
import scala.collection.mutable
import scala.collection.immutable.ArraySeq
import scala.annotation.tailrec

import program.*
import exceptions.{SyntaxError, NotFoundError}
import language_attributes.LanguageAttributes
import utils.{clearBackSlash, trySplit}
import directives.DirectiveReader

def compileProgram(programText: Iterable[Line]): Program = {
    val lines = programText.iterator
        .filter(!_.startsWith("import"))
        .map(_.removeCommentary)
        .filterNot(_.content.equals(""))
        .filterNot(DirectiveReader.readLine(_)) // side effect: changes DirectiveReader
        .toVector


    val typeLines = lines.filter(_.startsWith("type"))
    val ruleLines = lines
        .filter(line => !line.startsWith("type") && !line.startsWith("task:"))
    val taskLines = lines.filter(_.startsWith("task:"))

    val types = parseTypes(typeLines)
    val rules = parseRules(ruleLines, types)
    val tasks = ArraySeq.from(taskLines.map(l => new Task(l.drop(5))))

    Program(rules, tasks)
}



case class MockType(values: Set[Value], parentNames: Set[TypeName], line: Line)


def parseTypes(typeLines: Iterable[Line]): Map[TypeName, Type] = {
    val hierarchy: Map[TypeName, MockType] = typeLines.map(parseTypeLine(_)).toMap
    val builtinTypes = BuiltInTypes.shouldBeAdded
    val allNames = hierarchy.keySet | builtinTypes.keySet

    for (mock <- hierarchy.values; name <- mock.parentNames; if !allNames(name))
        throw NotFoundError(name, mock.line)

    val unresolved = mutable.Queue.from(hierarchy.keys)
    val buffer = mutable.Map.empty[TypeName, Type]
    buffer ++= builtinTypes

    while (unresolved.nonEmpty) {
        val name = unresolved.dequeue
        val mock = hierarchy(name)
        val parentTypes: Set[Option[Type]] = mock.parentNames.map(buffer.get(_))
        if (parentTypes.exists(_.isEmpty))
            unresolved.enqueue(name)
        else
            buffer += (name -> Type(mock.values, parentTypes.map(_.get)))
    }

    buffer.toMap
}

// TODO moveBackSlash
def parseTypeLine(line: Line): (TypeName, MockType) = {
    val err = SyntaxError("Type annotation must contain one ' = '", line)
    val (namePart, description) = trySplit(line, " = ")(throw err)
    val parts = description.split(" : ")
    val values = if (parts(0) == LanguageAttributes.nothing) Set.empty else parts(0).split(" ").toSet
    val parents = parts.length match {
        case 2 => parts(1).split(" ").toSet
        case 1 => Set.empty
        case _ => throw SyntaxError("Only one ' : ' expected", line)
    }
    if (parents(LanguageAttributes.anythingTypeName))
        println(s"WARNING: inheriting from Any doesn't make sense\n ${line.info}\n")

    (namePart.drop(5), MockType(values, parents, line))
}


// For rules use syntax 'expression1 -> expression2'
// where expression1 and expression2 are any expressions with types and values
// Expression 1 can contain type declarations.
// Also separate type annotation's line is valid.
def parseRules(ruleLines: Iterable[Line], types: Map[TypeName, Type]): ArraySeq[Rule] = {
    @tailrec
    def helper(lines: Iterator[Line], rules: List[Rule]): List[Rule] = {
        if (!lines.hasNext)
            return rules
        val line = lines.next

        if (line.contains (" -> "))
            helper(lines, Rule.fromSingleLine(line, types) :: rules)
        else if (!lines.hasNext)
            throw NotFoundError("rule expression line", line)
        else
            helper(lines, Rule.fromTwoLines(line, lines.next, types) :: rules)
    }

    ArraySeq.from(helper(ruleLines.iterator, Nil))
}


// For tasks use syntax 'task: expression'
// where expression is any expression with values
@deprecated("unused")
def parseTasks(taskLines: Iterable[Line]): Iterable[Task] =
    taskLines.map(l => new Task(l.drop(6)))

