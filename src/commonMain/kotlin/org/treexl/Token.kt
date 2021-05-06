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

class Token(val type: TokenType, lexeme: String, val literal: Any? = null, val line: Int = -1) {
    //optmization to reuse shared interned strings.
    val lexeme = type.keyword ?: lexeme

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(type)

        if (lexeme.isNotEmpty()) {
            sb.append(" [").append(lexeme).append("]")
        }

        if (type.isLiteral()) {
            sb.append(" ").append(literal)
        }

//        if (line != -1) {
//            sb.append(" (line: ").append(line).append(")")
//        }

        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false

        if (type != other.type) return false
        if (lexeme != other.lexeme) return false
        if (literal != other.literal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + lexeme.hashCode()
        result = 31 * result + (literal?.hashCode() ?: 0)
        return result
    }


}