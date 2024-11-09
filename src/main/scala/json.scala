package json

import scala.util.{Using, Try}
import scala.collection.immutable.ArraySeq

import java.io.{FileWriter, BufferedWriter}

import upickle.default.*
import program.{Program, Type, Rule, Metaformula}


def saveToJson(program: Program, path: String): Try[Unit] = Try {
    val json: String = write(toJsonProgram(program))
    Using(new BufferedWriter(new FileWriter(path)))(
        writer => writer.write(json)).get
}


sealed trait JsonType

object JsonType {
    case class Usual(values: Array[String], parents: Array[Int]) extends JsonType
    case class BuiltIn(name: String) extends JsonType

    private def toUJSON(t: JsonType): ujson.Value = t match {
        case BuiltIn(name) => ujson.Str(name)
        case Usual(values, parents) => ujson.Obj(
            "values"  -> ujson.Arr.from(values),
            "parents" -> ujson.Arr.from(parents)
        )
    }

    private def fromUJSON(json: ujson.Value): JsonType = {
        val res1 = Try {
            val atype = json.obj
            val values = atype("values").arr.iterator.map(_.str)
            val parents = atype("parents").arr.iterator.map(_.num.toInt)
            Usual(values.toArray, parents.toArray)
        }
        res1.orElse(Try(BuiltIn(json.str))).get
    }

    given ReadWriter[JsonType] = readwriter[ujson.Value]
        .bimap[JsonType](t => toUJSON(t), json => fromUJSON(json))
}

case class JsonMetaformula(template: String,
                           variables: ArraySeq[(String, Int)]) derives ReadWriter

case class JsonRule(left: JsonMetaformula, right: String) derives ReadWriter


case class JsonProgram(types: ArraySeq[JsonType],
                       rules: ArraySeq[JsonRule],
                       tasks: ArraySeq[String]) derives ReadWriter


def toJsonProgram(program: Program): JsonProgram = {
    val types = ArraySeq.from(program.rules.iterator    // get rules
        .flatMap(rule => rule.left.variables.map(_._2)) // invoke all types
        .toSet) // retain only unique types
    val typeMap: Map[Type, Int] = types.iterator.zipWithIndex.toMap
    val json_types = types.map(toJsonType(_, typeMap))
    val json_rules = program.rules.map(toJsonRule(_, typeMap))
    val json_tasks = program.tasks.map(_.toString)
    JsonProgram(json_types, json_rules, json_tasks)
}


def toJsonType(atype: Type, typeMap: Map[Type, Int]): JsonType = {
    program.BuiltInTypes.defined.find((_, t) => t == atype) match {
        case None => JsonType.Usual(atype.values.toArray,
            atype.parents.toArray.map(typeMap(_)))
        case Some((name, _)) => JsonType.BuiltIn(name)
    }
}


def toJsonRule(rule: Rule, typeMap: Map[Type, Int]): JsonRule = {
    val Metaformula(template, variables) = rule.left
    val json_variables = variables.map((name, t) => (name, typeMap(t)))
    val json_meta = JsonMetaformula(template.getRegex, json_variables)
    JsonRule(json_meta, rule.right)
}
