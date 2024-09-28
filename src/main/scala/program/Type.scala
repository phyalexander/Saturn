package program

import scala.collection.mutable
import scala.annotation.targetName

import java.io.Serializable

import exceptions.{SyntaxError, NotFoundError}
import utils.{clearBackSlash, trySplit}



/** Type of a variable in Saturn rule.
 *
 *  Decribes which values can a variable of this type be equal to.
 *  All parent types' values are also values of this type (belong to it).
 *  Inheritance in this context is treated as a union operation over the sets of
 *  values, meaning a type includes all values of its parent types.
 *
 *  The type hierarchy is represented as a tree with links to parent types, which
 *  optimizes memory usage compared to a flat collection of types.
 *
 *  @note This class is serializable to support persistence
 *       through the [[Program.saveToFile]] method.
 *
 *  @contructor creates new type with a set of values and a set of parent types
 *  @param values set of values that belong to this type
 *  @param parents set of parent types
 */
@SerialVersionUID(108024218808227L)
case class Type(values: Set[Value], parents: Set[Type]) extends Serializable {

    /**
     *  Checks if a given value is part of this type.
     *
     *  A value is considered part of this type if it is explicitly listed in the * `values` set or if it belongs to any of the parent types.
     *
     *  @param value The value to check for membership in this type.
     *  @return `true` if the value is part of this type, `false` otherwise.
     */
    def contains(value: Value): Boolean = values(value) || parents.exists(_.contains(value))

    /** Creates a new type with no values but with two parent types: this type
     *  and another specified type.
     *
     *  This operation is akin to a union.
     *
     *  @param other The other type to be included as a parent.
     *  @return A new type that is a union of this type and the specified type.
     */
    @targetName("union")
    def |(other: Type): Type = Type(Set.empty, Set(this, other))
}


/** Type factory, providing utility methods and constants. */
object Type {

    /** Constructor of empty Saturn type */
    lazy val empty: Type = Type(Set.empty, Set.empty)
}
