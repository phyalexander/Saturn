package program

import language_attributes.LanguageAttributes

/** Built-in Saturn types factory. Provides built-in Saturn types' objects and
 *  tools for them.
 *
 *  Built-in types are the types, that cannot be described by enumeration of
 *  their values or parent types. The only way is to override
 *  the [[Type.contains]] standard implementation.
 */
object BuiltInTypes {

    /** Returns a map with keys as built-in types names
     *  and values as all defined built-in type objects
     */
    val defined: Map[TypeName, Type] = Map(
        LanguageAttributes.anythingTypeName -> AnyType,
        "Int"     -> StdInteger,
        "Natural" -> StdNatural
    )


    /** Returns a map with keys as built-in type names
     *  and values as built-in type objects that should be used while compilation.
     *
     *  @note Uses DirectiveReader to get list of names of imported
     *  from std built-in types. Any is allways included.
     */
    def shouldBeAdded: Map[TypeName, Type] = {
        val err = Exception("Not supported built-in type") // for debug stage
        directives.DirectiveReader.importedBuiltInTypes
            .map(tname => (tname, defined.getOrElse(tname, throw err)))
            .toMap.updated(LanguageAttributes.anythingTypeName, AnyType)
    }


    /** The Any Saturn type object.
     *
     *  This is a type that any value belongs to. It other words,
     *  if a variable declared with this type it can be equal to any value.
     */
    object AnyType extends Type(Set.empty, Set.empty) {
        override def contains(value: Value): Boolean = true
    }


    /** Integer-type object, representing Int type from
     *  standard Saturn library (std).
     *
     *  This is a type that contains all integer numbers.
     */
    object StdInteger extends Type(Set.empty, Set.empty) {
        override def contains(value: Value): Boolean = "-?\\d+".r.matches(value)
    }


    /** Natural-type object, representing Natural type from
     *  standard Saturn library (std).
     *
     *  This is a type that contains all natural (1, 2, 3, ...) numbers.
     */
    object StdNatural extends Type(Set.empty, Set.empty) {
        override def contains(value: Value): Boolean = "[1-9]+".r.matches(value)
    }
}
