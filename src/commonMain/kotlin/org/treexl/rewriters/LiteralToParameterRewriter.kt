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

package org.treexl.rewriters

import org.treexl.AbstractRewriter
import org.treexl.Expression
import org.treexl.Literal
import org.treexl.Parameter

/**
 * Replace literal values with parameters while collecting values to a list.
 */
class LiteralToParameterRewriter(private val literalCollector: (value: Any?) -> String) : AbstractRewriter() {

    /**
     * Contain collected values that were replaced by parameters.
     */
    val values = mutableListOf<Any?>()

    override fun rewrite(expression: Literal<*>): Expression {
        return Parameter(literalCollector(expression.value))
    }

}