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

abstract class AbstractRewriter : Rewriter {

    @Suppress("MemberVisibilityCanBePrivate")
    protected inline fun Expression.rewriteChild(child1: Expression, block: (Expression) -> Expression): Expression {
        val newChild1 = child1.rewrite(this@AbstractRewriter)
        return if (newChild1 !== child1) {
            block(newChild1)
        } else {
            this
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected inline fun Expression.rewriteChild(child1: Expression, child2: Expression, block: (Expression, Expression) -> Expression): Expression {
        val newChild1 = child1.rewrite(this@AbstractRewriter)
        val newChild2 = child2.rewrite(this@AbstractRewriter)
        return if (newChild1 !== child1 || newChild2 !== child2) {
            block(newChild1, newChild2)
        } else {
            this
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected inline fun List<Expression>.rewrite(): List<Expression> {
        val list = this
        val rewriter = this@AbstractRewriter

        for (i in 0..list.lastIndex) {
            val expression = list[i]
            val newExpr = expression.rewrite(rewriter)
            if (newExpr !== expression) {
                val newList = list.toMutableList()
                newList[i] = newExpr
                for (j in i + 1..list.lastIndex) {
                    val expr2 = list[j]
                    val newExpr2 = expr2.rewrite(rewriter)
                    if (newExpr2 !== expr2) {
                        newList[j] = newExpr2
                    }
                }
                return newList
            }
        }

        return list
    }

    override fun rewrite(expression: Literal<*>): Expression {
        return expression
    }

    override fun rewrite(expression: Unary): Expression {
        return expression.rewriteChild(expression.right) {
            Unary(expression.operator, it)
        }
    }

    override fun rewrite(expression: Binary): Expression {
        return expression.rewriteChild(expression.left, expression.right) { left, right ->
            Binary(left, expression.operator, right)
        }
    }

    override fun rewrite(expression: Grouping): Expression {
        return expression.rewriteChild(expression.expression) {
            Grouping(it)
        }
    }

    override fun rewrite(expression: Identifier): Expression {
        return expression
    }

    override fun rewrite(expression: Parameter): Expression {
        return expression
    }

    override fun rewrite(expression: Call): Expression {
        val newid = expression.identifier.rewrite(this)
        val newlist = expression.arguments.rewrite()

        if (newid != expression.identifier || newlist !== expression.arguments) {
            return Call(newid as Identifier, expression.token, newlist)
        }

        return expression
    }

}