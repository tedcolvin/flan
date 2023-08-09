/*
 *
 * Treexl (Tree extensible expression language).
 * Copyright Ted Colvin (tedcolvin@outlook.com).
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the LICENSE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 */

package org.treexl

import org.treexl.sqlwhere.TreexlSQLWhereGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi
class TreexlSQLWhereGeneratorTest {

    private val generator = TreexlSQLWhereGenerator()

    @Test
    fun testSameExpression() {
        assertInputEqualsOutput("1")
        assertInputEqualsOutput("'1'")
        assertInputEqualsOutput("1.1")
        assertInputEqualsOutput("1.0".toDouble().toString())
        assertInputEqualsOutput("'1.0'")
        assertInputEqualsOutput("1 and 1")
        assertInputEqualsOutput("1 or 1")
        assertInputEqualsOutput("1 = 1")
        assertInputEqualsOutput("1 <> 1")
        assertInputEqualsOutput("1 > 1")
        assertInputEqualsOutput("1 < 1")
        assertInputEqualsOutput("1 >= 1")
        assertInputEqualsOutput("1 <= 1")
        assertInputEqualsOutput("1 > 1")
        assertInputEqualsOutput("not 1")
        assertInputEqualsOutput("-1")
        assertInputEqualsOutput("(1 or 2) and 3 or not 4")
        assertInputEqualsOutput("X")
        assertInputEqualsOutput("X or Y")
        assertInputEqualsOutput("X = 1 or Y = 2")
        assertInputEqualsOutput("X like '%x%'")
        assertInputEqualsOutput("X is null")
        assertInputEqualsOutput("X is not null")
        assertInputEqualsOutput("f(x, y, z) and B = 2")
        assertInputEqualsOutput("B = 2 and A in (1)")
        assertInputEqualsOutput("A in (1) and B = 2")
        assertInputEqualsOutput("a.B = 1 and a.C like 'a'")
        assertInputEqualsOutput("c.A in (1) and d.B = 2")
        assertInputEqualsOutput("c.d.A in (1) and e.f.g.B = 2")
    }

    @Test
    fun testAppend() {
        class IdentifierToUpperCase : AbstractRewriter() {
            override fun rewrite(expression: Identifier): Expression {
                return if (expression.name.any { it.toUpperCase() != it }) {
                    Identifier(expression.name.toUpperCase())
                } else {
                    expression
                }
            }
        }

        val treexl = Treexl(Treexl.Options(listOf(IdentifierToUpperCase())))

        val appendable = StringBuilder()
        generator.appendSQL(appendable, treexl.parse("a = B and c < D"))
        assertEquals("A = B and C < D", appendable.toString())
    }

    @Test
    fun testBetweenAndIn() {
        val treexl = Treexl(Treexl.Options(emptyList()))
        val appendable = StringBuilder()

        val betweenClause = "A in 1..10"
        generator.appendSQL(appendable, treexl.parse(betweenClause))
        assertEquals("A between 1 and 10", appendable.toString())

        appendable.setLength(0)
        val inClause = "A in (1,4,10,19)"
        generator.appendSQL(appendable, treexl.parse(inClause))
        assertEquals("A in (1,4,10,19)", appendable.toString())
    }

    private fun assertInputEqualsOutput(s: String) {
        assertEquals(s, generator.parse(s))
    }

}