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

enum class TokenType(val keyword: String? = null) {

    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, COMMA, DOT,

    // One or two character tokens.
    EQUAL, DIFFERENT, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    IDENTIFIER,

    PARAMETER,

    STRING {
        override fun isLiteral() = true
    },

    NUMBER  {
        override fun isLiteral() = true
    },

    // Keywords.
    AND("and"),
    FALSE("false"),
    NULL("null"),
    OR("or"),
    TRUE("true"),
    NOT("not"),
    IS("is"),
    IN("in"),
    LIKE("like"),
    BETWEEN("between"),
    MINUS("minus"),

    EOF
    ;

    open fun isLiteral() = false

    companion object {
        operator fun get(text: String): TokenType? {
            return keywords[text]
        }

        private val keywords = values().mapNotNull { type ->
            type.keyword?.let { kw ->
                kw to type
            }
        }.toMap()
    }

}