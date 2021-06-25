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


sealed class Expression {

    abstract fun visit(visitor: Visitor)
    abstract fun rewrite(visitor: Rewriter): Expression

}

data class Literal<T>(val value: T) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)

    override fun toString(): String {
        return if (value is String) {
            "Literal(value=\"$value\")"
        } else {
            "Literal(value=$value)"
        }
    }

}

data class Grouping(val expression: Expression) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class Identifier(val name: String) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class Unary(val operator: Token, val right: Expression) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class Parameter(val name: String) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class Call(val identifier: Identifier, val token: Token, val arguments: List<Expression>) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}

data class ExprList(val arguments: List<Expression>) : Expression() {
    override fun visit(visitor: Visitor) = visitor.visit(this)
    override fun rewrite(visitor: Rewriter) = visitor.rewrite(this)
}
