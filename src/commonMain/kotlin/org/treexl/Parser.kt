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
 *eita
 */

package org.treexl


class Parser(private val tokens: List<Token>) {

    private var current = 0

    fun parse(): Expression {
        return expression()
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun expression(): Expression {
        return or()
    }

    private fun isAtEnd(): Boolean {
        return peek().type === TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }
    private fun equality(): Expression {
        var expr = comparison()
        while (match(TokenType.DIFFERENT, TokenType.EQUAL, TokenType.IS, TokenType.IN, TokenType.LIKE, TokenType.BETWEEN)) {
            val operator = previous()

            val right = when (operator.type) {
                TokenType.IN -> {
                    if (match(TokenType.LEFT_PAREN)) {
                        ExprList(finishCall())
                    } else {
                        range()
                    }
                }
                TokenType.BETWEEN -> parseError(operator, "'like' operator not supported. Use 'in with ranges' instead ('<expr> in <start>..<end>').")
                else -> {
                    comparison()
                }
            }
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun range(): Expression {
        val expression = unary() //check for 'not'
        val range = consume(TokenType.RANGE, "Expected '(' or range  '<start> .. <end>' after 'in' expression.")
        return Binary(expression, range, unary())
    }

    private fun check(type: TokenType): Boolean {
        return if (isAtEnd()) false else peek().type === type
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        parseError(peek(), message)
    }

    private fun comparison(): Expression {
        var expr = unary()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun or(): Expression {
        var expr = and()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right = and()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun and(): Expression {
        var expr = equality()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expression {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }
        return parameter()
    }

    private fun parameter(): Expression {
        if (match(TokenType.PARAMETER)) {
            return Parameter(previous().lexeme)
        }
        return call()
    }

    private fun call(): Expression {
        val expression = identifier()

        if (expression is Identifier) {
            if (match(TokenType.LEFT_PAREN)) {
                val arguments = finishCall()
                val paren = consume(
                    TokenType.RIGHT_PAREN,
                    "Expect ')' after arguments."
                )
                return Call(expression, paren, arguments)
            }

        }
        return expression
    }

    private fun identifier(): Expression {
        if (match(TokenType.IDENTIFIER)) {
            return Identifier(previous().lexeme)
        }
        return primary()
    }

    private fun primary(): Expression {
        if (match(TokenType.FALSE)) return Literal(false)
        if (match(TokenType.TRUE)) return Literal(true)
        if (match(TokenType.NULL)) return Literal(null)
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Literal(previous().literal)
        }
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Grouping(expr)
        }

        parseError(peek(), "Expect expression.")
    }

    private fun finishCall(): List<Expression> {
        val arguments = mutableListOf<Expression>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }
        return arguments
    }

    private fun parseError(token: Token, message: String): Nothing {
        val msg = if (token.type === TokenType.EOF) {
            "Parse error (line: ${token.line}) EOF: $message"
        } else {
            "Parse error (line: ${token.line}) at '${token.lexeme}': $message"
        }
        throw ParseError(msg)
    }
}