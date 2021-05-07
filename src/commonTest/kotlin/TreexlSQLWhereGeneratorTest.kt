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

    val generator = TreexlSQLWhereGenerator()

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

    private fun assertInputEqualsOutput(s: String) {
        assertEquals(s, generator.parse(s))
    }

}