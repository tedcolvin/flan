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

package io.github.tedcolvin.flan.sqlwhere

import io.github.tedcolvin.flan.Binary
import io.github.tedcolvin.flan.Flan
import io.github.tedcolvin.flan.Grouping
import io.github.tedcolvin.flan.Identifier
import io.github.tedcolvin.flan.Literal
import io.github.tedcolvin.flan.TokenType
import io.github.tedcolvin.flan.Unary
import io.github.tedcolvin.flan.Visitor

class FlanSQLWhereGenerator(val flan: Flan = Flan()) {

    fun parse(flanExpression: String): String {
        val stringBuilder = StringBuilder()
        val visitor = SQLVisitor(stringBuilder)
        val expr = flan.parse(flanExpression)
        expr.visit(visitor)
        return stringBuilder.toString()
    }

}

class SQLVisitor(private val appendable: Appendable) : Visitor {
    override fun visit(expression: Literal<*>) {
        when (val value = expression.value) {
            is Int -> appendable.append(value.toString())

            is Double -> appendable.append(value.toString())

            is String -> {
                appendable.append("'")
                appendable.append(value)
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

            else -> {
                error("Invalid unary operator: ${expression.operator}")
            }
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

}
