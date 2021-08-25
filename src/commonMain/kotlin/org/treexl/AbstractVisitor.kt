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

abstract class AbstractVisitor : Visitor {
    override fun visit(expression: Binary) {
        expression.left.visit(this)
        expression.right.visit(this)
    }

    override fun visit(expression: Grouping) {
        expression.expression.visit(this)
    }

    override fun visit(expression: Call) {
        expression.arguments.forEach { it.visit(this) }
    }

    override fun visit(expression: ExprList) {
        expression.arguments.forEach { it.visit(this) }
    }

    override fun visit(expression: Unary) {
        expression.right.visit(this)
    }

    override fun visit(expression: Identifier) {}

    override fun visit(expression: Literal<*>) {}

    override fun visit(expression: Parameter) {}
}