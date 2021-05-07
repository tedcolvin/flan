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


/**
 * Treexl (Tree extensible expression language) parser.
 *
 * Parses Treexl expressions into an "expression tree" (AST).

 */
class Treexl(private val options: Options = Options()) {
    class Options(
        val rewriters: List<Rewriter> = emptyList()
    )

    fun parse(strExpr: String): Expression {
        if (strExpr.isBlank()) {
            throw ParseError("Expression is blank (\"\").")
        }
        val tokens = Scanner(strExpr).scanTokens()
        val expression = Parser(tokens).parse()

        return options.rewriters.fold(expression) { expr, rewriteVisitor ->
            expr.rewrite(rewriteVisitor)
        }
    }
}

