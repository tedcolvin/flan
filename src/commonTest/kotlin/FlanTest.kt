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

import kotlin.test.Test
import kotlin.test.assertEquals

class FlanTest {

    @Test
    fun testScannerOneToken() {
        assertScanAs("(", Token(TokenType.LEFT_PAREN, "("))
        assertScanAs(")", Token(TokenType.RIGHT_PAREN, ")"))
        assertScanAs(",", Token(TokenType.COMMA, ","))
        assertScanAs(".", Token(TokenType.DOT, "."))
        assertScanAs("=", Token(TokenType.EQUAL, "="))
        assertScanAs("<>", Token(TokenType.DIFFERENT, "<>"))
        assertScanAs(">", Token(TokenType.GREATER, ">"))
        assertScanAs(">=", Token(TokenType.GREATER_EQUAL, ">="))
        assertScanAs("<", Token(TokenType.LESS, "<"))
        assertScanAs("<=", Token(TokenType.LESS_EQUAL, "<="))

        assertScanAs("and", Token(TokenType.AND, "and"))
        assertScanAs("or", Token(TokenType.OR, "or"))

        assertScanAs("false", Token(TokenType.FALSE, "false"))
        assertScanAs("true", Token(TokenType.TRUE, "true"))
        assertScanAs("null", Token(TokenType.NULL, "null"))

        assertScanAs("FOO", Token(TokenType.IDENTIFIER, "FOO"))

        assertScanAs("'FOO'", Token(TokenType.STRING, "'FOO'", literal = "FOO"))

        assertScanAs("123", Token(TokenType.NUMBER, "123", literal = 123.0))

        assertScanAs("123.0", Token(TokenType.NUMBER, "123.0", literal = 123.0))

        assertScanAs(
            "()",
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.RIGHT_PAREN, ")")
        )

    }

    @Test
    fun testScannerSamples() {
        assertScanAs(
            "'abc' = ABC and not (a = b or c = d)",
            Token(TokenType.STRING, "'abc'", "abc"),
            Token(TokenType.EQUAL, "="),
            Token(TokenType.IDENTIFIER, "ABC"),
            Token(TokenType.AND, "and"),
            Token(TokenType.NOT, "not"),
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.IDENTIFIER, "a"),
            Token(TokenType.EQUAL, "="),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.OR, "or"),
            Token(TokenType.IDENTIFIER, "c"),
            Token(TokenType.EQUAL, "="),
            Token(TokenType.IDENTIFIER, "d"),
            Token(TokenType.RIGHT_PAREN, ")")
        )
    }

    private fun assertScanAs(expression: String, vararg expectedTokens: Token) {
        val scanner1 = Scanner(expression)
        val tokens = scanner1.scanTokens()
        assertEquals(TokenType.EOF, tokens.last().type)
        assertEquals(expectedTokens.toList(), tokens.subList(0, tokens.size - 1))
    }

    val flan = Flan()

    @Test
    fun testLiterals() {
        val flan = Flan()
        assertEquals(Literal(1), flan.parse("1"))
        assertEquals(Literal(1.0), flan.parse("1.0"))

        assertEquals(Literal(null), flan.parse("null"))

        assertEquals(Literal("string"), flan.parse("'string'"))
        assertEquals(Literal(""), flan.parse("''"))
        assertEquals(Literal("1"), flan.parse("'1'"))

        assertEquals(Literal(true), flan.parse("true"))
        assertEquals(Literal(false), flan.parse("false"))
    }

    @Test
    fun testUnaries() {
        assertEquals(Unary(Token(TokenType.MINUS, "-"), Literal(1)), flan.parse("-1"))
        assertEquals(Unary(Token(TokenType.MINUS, "-"), Literal(1.0)), flan.parse("-1.0"))

        assertEquals(Unary(Token(TokenType.NOT, "not"), Literal(true)), flan.parse("not true"))
        assertEquals(Unary(Token(TokenType.NOT, "not"), Literal(false)), flan.parse("not false"))

        assertEquals(
            Unary(
                Token(TokenType.NOT, "not"),
                Unary(
                    Token(TokenType.NOT, "not"),
                    Literal(true)
                )
            ), flan.parse("not not true"))

    }

    @Test
    fun testBinaryOps() {
        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.EQUAL, "="),
                Literal("1")
            ),
            flan.parse("1 = '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.DIFFERENT, "<>"),
                Literal("1")
            ),
            flan.parse("1 <> '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.GREATER, ">"),
                Literal("1")
            ),
            flan.parse("1 > '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.GREATER_EQUAL, ">="),
                Literal("1")
            ),
            flan.parse("1 >= '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.LESS, "<"),
                Literal("1")
            ),
            flan.parse("1 < '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.LESS_EQUAL, "<="),
                Literal("1")
            ),
            flan.parse("1 <= '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.AND, "and"),
                Literal("1")
            ),
            flan.parse("1 and '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.OR, "or"),
                Literal("1")
            ),
            flan.parse("1 or '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.OR, "or"),
                Binary(
                    Literal(2),
                    Token(TokenType.AND, "and"),
                    Literal(3)
                ),
            ),
            flan.parse("1 or 2 and 3")
        )

    }

    @Test
    fun testGrouping() {
        assertEquals(Grouping(Literal(1)), flan.parse("(1)"))

        assertEquals(Binary(
            Grouping(
                Binary(
                    Literal(1),
                    Token(TokenType.OR, "or"),
                    Literal(2)
                )
            ),
            Token(TokenType.AND, "and"),
            Literal(3)
        ), flan.parse("(1 or 2) and 3"))

    }

}
