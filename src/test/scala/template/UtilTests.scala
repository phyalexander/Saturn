package template

import jlib.Utils;

class UtilTests extends munit.FunSuite {

    private val RVR = Utils.regexVariableRepresentation

    ///////////// contextsToRegex /////////////

    test("contextsToRegex: basic") {
        val contexts = Array("", " + ", "")
        val expected = s"$RVR \\+ $RVR"

        val actual = Utils.contextsToRegex(contexts)

        assertEquals(actual, expected)
    }

    test("contextsToRegex: empty context case") {
        intercept[RuntimeException] {
            Utils.contextsToRegex(Array.empty[String])
        }
    }

    test("contextsToRegex: with no variables") {
        val contexts = Array("just context")
        val expected = "just context"

        val actual = Utils.contextsToRegex(contexts)

        assertEquals(actual, expected)
    }

    ///////////// createContexts /////////////

    test("createContexts: basic") {
        val expression = "My name is NAME. My age is AGE"
        val variablePositions = Array(
            expression.indexOf("NAME"), // 11
            expression.indexOf("AGE")   // 27
        )
        val expected = Array("My name is ", ". My age is ", "")

        val actual = Utils.createContexts(expression, variablePositions)

        assertEquals(actual, expected)
    }

    test("createContexts: from empty expression") {
        val actual = Utils.createContexts("", Array())
        assertEquals(actual, Array(""))
    }

    test("createContexts: from expression with no variables") {
        val expression = "Everything has its beauty, but not everyone sees it" // (c) Confucius
        val expected = Array(expression)

        val actual = Utils.createContexts(expression, Array())

        assertEquals(actual, expected)
    }

    ///

}
