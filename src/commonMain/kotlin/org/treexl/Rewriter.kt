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

interface Rewriter {

    fun rewrite(expression: Literal<*>): Expression

    fun rewrite(expression: Unary): Expression

    fun rewrite(expression: Binary): Expression

    fun rewrite(expression: Grouping): Expression

    fun rewrite(expression: Identifier): Expression

    fun rewrite(expression: Parameter): Expression

    fun rewrite(expression: Call): Expression

    fun rewrite(expression: ExprList): Expression

}