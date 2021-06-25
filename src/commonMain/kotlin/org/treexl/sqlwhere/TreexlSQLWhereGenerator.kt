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

package org.treexl.sqlwhere

import org.treexl.Binary
import org.treexl.Call
import org.treexl.ExprList
import org.treexl.Expression
import org.treexl.Grouping
import org.treexl.Identifier
import org.treexl.Literal
import org.treexl.Parameter
import org.treexl.TokenType
import org.treexl.Treexl
import org.treexl.Unary
import org.treexl.Visitor


class TreexlSQLWhereGenerator(private val options: Options) {

    data class Options(
        var treexl: Treexl = Treexl(),
        var quoter: (Appendable) -> Unit = { it.append("''") }
    )

    companion object {
        private val defaultOptions = Options()
    }

    constructor() : this(defaultOptions)

    fun parse(treexlExpression: String): String {
        val treexl = options.treexl
        val expr = treexl.parse(treexlExpression)
        val stringBuilder = StringBuilder()
        appendSQL(stringBuilder, expr)
        return stringBuilder.toString()
    }

    fun appendSQL(appendable: Appendable, expression: Expression) {
        val visitor = SQLVisitor(appendable, options.quoter)
        expression.visit(visitor)
    }

}

class SQLVisitor(private val appendable: Appendable, private val quoter: (Appendable) -> Unit) : Visitor {
    override fun visit(expression: Literal<*>) {
        when (val value = expression.value) {
            is Int -> appendable.append(value.toString())

            is Double -> appendable.append(value.toString())

            is String -> {
                appendable.append("'")

                value.forEach {
                    if (it == '\'') {
                        quoter(appendable)
                    } else {
                        appendable.append(it)
                    }
                }

                appendable.append("'")
            }

            null -> appendable.append("null")

            else -> error("Literal of type ${value::class} is not supported.")
        }
    }

    override fun visit(expression: Unary) {
        when (expression.operator.type) {
            TokenType.MINUS -> {
                appendable.append("-")
            }

            TokenType.NOT -> {
                appendable.append("not ")
            }

            else -> error("Invalid unary operator: ${expression.operator}")
        }

        expression.right.visit(this)
    }

    override fun visit(expression: Binary) {
        expression.left.visit(this)
        appendable.append(" ")

        when (expression.operator.type) {
            TokenType.AND -> {
                appendable.append("and")
            }
            TokenType.OR -> {
                appendable.append("or")
            }
            TokenType.EQUAL -> {
                appendable.append("=")
            }
            TokenType.DIFFERENT -> {
                appendable.append("<>")
            }
            TokenType.LESS -> {
                appendable.append("<")
            }
            TokenType.LESS_EQUAL -> {
                appendable.append("<=")
            }
            TokenType.GREATER -> {
                appendable.append(">")
            }
            TokenType.GREATER_EQUAL -> {
                appendable.append(">=")
            }
            TokenType.IS -> {
                appendable.append("is")
            }
            TokenType.LIKE -> {
                appendable.append("like")
            }

            else -> error("Invalid binary operator: ${expression.operator}")
        }

        appendable.append(" ")
        expression.right.visit(this)

    }

    override fun visit(expression: Grouping) {
        appendable.append("(")
        expression.expression.visit(this)
        appendable.append(")")
    }

    override fun visit(expression: Identifier) {
        appendable.append(expression.name)
    }

    override fun visit(expression: Parameter) {
        appendable.append("\${")
        appendable.append(expression.name)
        appendable.append("}")
    }

    override fun visit(expression: Call) {
        appendable.append(expression.identifier.name)
        appendable.append("(")

        expression.arguments.forEachIndexed { i, arg ->
            if (i > 0) {
                appendable.append(", ")
            }
            arg.visit(this)
        }

        appendable.append(")")
    }

    override fun visit(expression: ExprList) {
        TODO("Not yet implemented")
    }
}

