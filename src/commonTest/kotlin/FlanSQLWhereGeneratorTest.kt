/*
 *
 * Flan (Filter language)
 * Copyright Ted Colvin (tedcolvin@outlook.com).
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the LICENSE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 */

package io.github.tedcolvin.flan

import io.github.tedcolvin.flan.sqlwhere.FlanSQLWhereGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class FlanSQLWhereGeneratorTest {

    val generator = FlanSQLWhereGenerator()

    @Test
    fun testSameExpression() {
        assertInputEqualsOutput("1")
        assertInputEqualsOutput("'1'")
        assertInputEqualsOutput("1.0")
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

    private fun assertInputEqualsOutput(s: String) {
        assertEquals(s, generator.parse(s))
    }

}