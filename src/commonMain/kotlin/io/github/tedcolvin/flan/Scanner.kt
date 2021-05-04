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

internal class Scanner(private val source: CharSequence) {
    private val tokens = arrayListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '=' -> addToken(TokenType.EQUAL)
            '-' -> addToken(TokenType.MINUS)

            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

            '<' -> {
                val token = when {
                    match('=') -> TokenType.LESS_EQUAL
                    match('>') -> TokenType.DIFFERENT
                    else -> TokenType.LESS
                }
                addToken(token)
            }

            '\'' -> string()

            '\t', ' ', '\r' -> {
                //noop
            }

            '\n' -> line++

            else -> {
                when {
                    isDigit(c) -> {
                        number()
                    }
                    isAlpha(c) -> {
                        identifier()
                    }
                    else -> {
                        parseError("Unexpected character.")
                    }
                }
            }
        }
    }

    private fun parseError(s: String) {
        println("Parse error ($line:$current): $s")
        throw ParseError()
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[current]
    }

    private fun string() {
        while (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            parseError("Unterminated string.")
            return
        }

        // The closing '.
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun number() {
        while (isDigit(peek())) {
            advance()
        }

        // Look for a fractional part.
        var fractional = false
        if (peek() == '.' && isDigit(peekNext())) {
            fractional = true
            // Consume the "."
            advance()
            while (isDigit(peek())) advance()
        }

        val s = source.substring(start, current)
        val literal = if (fractional) s.toDouble() else s.toInt()   //handle longs and bigdecimals

        addToken(TokenType.NUMBER, literal)
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        addToken(TokenType[text] ?: TokenType.IDENTIFIER)
    }

}


enum class TokenType(val keyword: String? = null) {

    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, COMMA, DOT,

    // One or two character tokens.
    EQUAL, DIFFERENT, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER,

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
