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


class Flan {
    fun parse(strExpr: String): Expression {
        if (strExpr.isBlank()) {
            throw ParseError()
        }
        val tokens = Scanner(strExpr).scanTokens()
        return Parser(tokens).parse()
    }
}

