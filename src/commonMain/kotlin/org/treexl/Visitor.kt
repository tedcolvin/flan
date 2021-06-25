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

interface Visitor {

    fun visit(expression: Literal<*>)

    fun visit(expression: Unary)

    fun visit(expression: Binary)

    fun visit(expression: Grouping)

    fun visit(expression: Identifier)

    fun visit(expression: Parameter)

    fun visit(expression: Call)

    fun visit(expression: ExprList)

}


