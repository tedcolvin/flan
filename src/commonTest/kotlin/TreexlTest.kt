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

import org.treexl.rewriters.LiteralToParameterRewriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TreexlTest {

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

        assertScanAs("123", Token(TokenType.NUMBER, "123", literal = 123))

        assertScanAs("123.0", Token(TokenType.NUMBER, "123.0", literal = 123.0))

        assertScanAs(
            "a.b",
            Token(TokenType.IDENTIFIER, "a"),
            Token(TokenType.DOT, "."),
            Token(TokenType.IDENTIFIER, "b")
        )

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

    private val treexl = Treexl()

    @Test
    fun testLiterals() {
        val treexl = Treexl()
        assertEquals(Literal(1), treexl.parse("1"))
        assertEquals(Literal(1.0), treexl.parse("1.0"))

        assertEquals(Literal(null), treexl.parse("null"))

        assertEquals(Literal("string"), treexl.parse("'string'"))
        assertEquals(Literal(""), treexl.parse("''"))
        assertEquals(Literal("1"), treexl.parse("'1'"))

        assertEquals(Literal(true), treexl.parse("true"))
        assertEquals(Literal(false), treexl.parse("false"))
    }

    @Test
    fun testUnaries() {
        assertEquals(Unary(Token(TokenType.MINUS, "-"), Literal(1)), treexl.parse("-1"))
        assertEquals(Unary(Token(TokenType.MINUS, "-"), Literal(1.0)), treexl.parse("-1.0"))

        assertEquals(Unary(Token(TokenType.NOT, "not"), Literal(true)), treexl.parse("not true"))
        assertEquals(Unary(Token(TokenType.NOT, "not"), Literal(false)), treexl.parse("not false"))

        assertEquals(
            Unary(
                Token(TokenType.NOT, "not"),
                Unary(
                    Token(TokenType.NOT, "not"),
                    Literal(true)
                )
            ), treexl.parse("not not true")
        )

    }

    @Test
    fun testBinaryOps() {
        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.EQUAL, "="),
                Literal("1")
            ),
            treexl.parse("1 = '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.DIFFERENT, "<>"),
                Literal("1")
            ),
            treexl.parse("1 <> '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.GREATER, ">"),
                Literal("1")
            ),
            treexl.parse("1 > '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.GREATER_EQUAL, ">="),
                Literal("1")
            ),
            treexl.parse("1 >= '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.LESS, "<"),
                Literal("1")
            ),
            treexl.parse("1 < '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.LESS_EQUAL, "<="),
                Literal("1")
            ),
            treexl.parse("1 <= '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.AND, "and"),
                Literal("1")
            ),
            treexl.parse("1 and '1'")
        )

        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.OR, "or"),
                Literal("1")
            ),
            treexl.parse("1 or '1'")
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
            treexl.parse("1 or 2 and 3")
        )

    }

    @Test
    fun testLike() {
        assertEquals(
            Binary(
                Identifier("X"),
                Token(TokenType.LIKE, "like"),
                Literal("%x%")
            ),
            treexl.parse("X like '%x%'")
        )
    }

    @Test
    fun testIdentifiers() {
        assertEquals(
            Binary(
                Identifier("a.b.C"),
                Token(TokenType.EQUAL, "="),
                Identifier("x.Y")
            ),
            treexl.parse("a.b.C = x.Y")
        )
    }

    @Test
    fun testIn() {
        assertEquals(
            Binary(
                Identifier("A"),
                Token(TokenType.IN, "in"),
                ExprList(listOf(
                    Literal(1),
                    Literal(2),
                    Literal(3),
                ))
            ),
            treexl.parse("A in (1, 2, 3)")
        )

        try {
            treexl.parse("A in 1, 2, 3")
        } catch (e: ParseError) {
            assertEquals("Parse error (line: 1) at ',': Expected '(' or range  '<start> .. <end>' after 'in' expression.", e.message)
        }

        assertNotNull(treexl.parse("A in 1..3"))

        val actual = treexl.parse("A in (1) and 1 = 1")
        assertEquals(
            Binary(
                treexl.parse("A in (1)"),
                Token(TokenType.AND, "and"),
                treexl.parse("1 = 1"),
            ),
            actual
        )

    }

    @Test
    fun testParams() {
        assertEquals(
            Binary(
                Literal(1),
                Token(TokenType.EQUAL, "="),
                Literal("1")
            ),
            treexl.parse("1 = '1'")
        )
    }

    @Test
    fun testBetweenError() {
        try {
            treexl.parse("a between 5 and 7")
        } catch (e: ParseError) {
            assertEquals("Parse error (line: 1) at 'between': 'like' operator not supported. Use 'in with ranges' instead ('<expr> in <start>..<end>').", e.message)
        }
    }

    @Test
    fun testKeyworkCasing() {
        try {
            treexl.parse("(1 < 2 AND '1' = '1')")
        } catch (e: ParseError) {
            assertEquals("Parse error (1:10): Invalid keyword 'AND': keywords are case sensitive (lowercase only). Use 'and' instead.", e.message)
        }
    }

    @Test
    fun testParens() {
        assertEquals(
            Grouping(
                Binary(
                    Literal(1),
                    Token(TokenType.EQUAL, "="),
                    Literal("1")
                ),
            ),
            treexl.parse("(1 = '1')")
        )
    }

    @Test
    fun testErrorReporting() {
        try {
            treexl.parse("1 != 2")
        } catch (e: ParseError) {
            assertEquals("Parse error (1:3): Unexpected character '!'.", e.message)
        }

    }

    @Test
    fun testGrouping() {
        assertEquals(Grouping(Literal(1)), treexl.parse("(1)"))

        assertEquals(
            Binary(
                Grouping(
                    Binary(
                        Literal(1),
                        Token(TokenType.OR, "or"),
                        Literal(2)
                    )
                ),
                Token(TokenType.AND, "and"),
                Literal(3)
            ), treexl.parse("(1 or 2) and 3")
        )

    }

    @Test
    fun testParameters() {
        assertEquals(Parameter("A"), treexl.parse(":A"))
        assertEquals(Binary(Parameter("A"), Token(TokenType.EQUAL, "="), Identifier("A")), treexl.parse(":A = A"))
    }

    @Test
    fun testFunctions() {
        assertEquals(
            Call(
                Identifier("uppercase"),
                Token(TokenType.RIGHT_PAREN, ")"),
                listOf(Literal("x"))
            ),
            treexl.parse("uppercase('x')")
        )
    }

    @Test
    fun testRewrites() {

        val values = mutableListOf<Any?>()
        val rewriter = LiteralToParameterRewriter {
            values.add(it)
            "_${values.size}"
        }

        val treexl = Treexl()

        assertEquals(
            treexl.parse("A > :_1 and (B <> :_2 or C = :_3)"),
            treexl.parse("A > 1 and (B <> '2' or C = null)").rewrite(rewriter)
        )

        assertEquals(listOf<Any?>(1, "2", null), values)

    }

}