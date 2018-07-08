package blue.paradox.cfs.core.lexer

import java.io.File

internal object Lexer {

    fun lex(str: String): List<Token<*>> {
        if (str.isBlank()) return emptyList()

        val iterator = TextIterator(str)
        val tokens = mutableListOf<Token<*>>()

        iterator.whileNext {
            val next = next()

            if (next.isWhitespace())
                return@whileNext

            when (next) {
                ':' -> tokens += AssignmentSymbolToken(index)
                ',' -> tokens += SeparatorSymbolToken(index)
                '[' -> tokens += MapBeginSymbolToken(index)
                ']' -> tokens += MapEndSymbolToken(index)

                else -> {
                    if (next.isLetter())
                        tokens += lexIdentifier(iterator)
                    if (next.isDigit() || next == '-')
                        tokens += lexNumberLiteral(iterator)
                    if (next == '\"')
                        tokens += lexStringLiteral(iterator)
                    if (next == ' ')
                        tokens += lexIdentifier(iterator)
                }
            }
        }

        return tokens
    }

    fun lex(file: File): List<Token<*>> = lex(file.readText())

    fun lexIdentifier(iterator: TextIterator): Token<*> {

        val builder = StringBuilder(iterator.current().toString())

        while (iterator.hasNext()) {
            val next = iterator.peekNext() ?: break
            if ((!next.isLetterOrDigit() && next != ' '))
                break
            val actualNext = iterator.next()
            builder.append(actualNext)
        }

        val string = builder.toString()
        if (string == "true")
            return BooleanLiteralToken(true, iterator.index)
        if (string == "false")
            return BooleanLiteralToken(false, iterator.index)
        return IdentifierToken(string, iterator.index)
    }

    fun lexNumberLiteral(iterator: TextIterator): NumberLiteralToken {

        val builder = StringBuilder(iterator.current().toString())

        while (iterator.hasNext()) {

            val next = iterator.peekNext() ?: break
            if (next != '.' && !next.isDigit())
                break
            val actualNext = iterator.next()
            builder.append(actualNext)
        }

        return NumberLiteralToken(builder.toString().toBigDecimal(), iterator.index)
    }

    fun lexStringLiteral(iterator: TextIterator): StringLiteralToken {

        val builder = StringBuilder()

        while (iterator.hasNext()) {

            val next = iterator.next()
            if (next != '\"')
                builder.append(next)
            else
                break
        }

        return StringLiteralToken(builder.toString(), iterator.index)
    }
}