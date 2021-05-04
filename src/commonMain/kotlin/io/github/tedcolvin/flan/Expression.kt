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


sealed class Expression {

    abstract fun visit(visitor: Visitor)

}

data class Literal<T>(val value: T) : Expression() {
    override fun visit(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Grouping(val expression: Expression) : Expression() {
    override fun visit(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Identifier(val name: String) : Expression() {
    override fun visit(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression() {
    override fun visit(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Unary(val operator: Token, val right: Expression) : Expression() {
    override fun visit(visitor: Visitor) {
        visitor.visit(this)
    }
}

