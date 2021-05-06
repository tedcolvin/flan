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

class TreexlOptions(
    val rewriters: List<Rewriter> = emptyList()
)

/**
 * Treexl - Filter Language
 *
 * Language based on SQL where subset used to filter collections.
 */
class Treexl(private val options: TreexlOptions = TreexlOptions()) {
    fun parse(strExpr: String): Expression {
        if (strExpr.isBlank()) {
            throw ParseError()
        }
        val tokens = Scanner(strExpr).scanTokens()
        val expression = Parser(tokens).parse()

        return options.rewriters.fold(expression) { expr, rewriteVisitor ->
            expr.rewrite(rewriteVisitor)
        }
    }
}

