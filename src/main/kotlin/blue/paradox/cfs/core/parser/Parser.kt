package blue.paradox.cfs.core.parser

import blue.paradox.cfs.core.lexer.*

internal object Parser {

    fun parse(tokens: List<Token<*>>): MutableMap<String, Any> = parseMap(TokenIterator(tokens))

    fun parseAssignment(identifier: IdentifierToken, iterator: TokenIterator): Pair<String, Any> {

        val peeked = iterator.peekNext()
        if (peeked !is AssignmentSymbolToken)
            throw IllegalStateException("Illegal argument: $peeked")

        iterator.next()
        val value = parseValue(iterator) ?: throw IllegalStateException("Unable to parse value: (${peeked.index} to '${peeked.value}')")
        return identifier.value to value
    }

    fun parseValue(iterator: TokenIterator): Any? {
        val peeked = iterator.peekNext() ?: return null

        if (peeked is MapBeginSymbolToken)
            return parseMap(iterator)

        if (peeked !is LiteralToken)
            return null

        iterator.next()
        val nextPeeked = iterator.peekNext()

        if (nextPeeked is SeparatorSymbolToken) {
            iterator.next()
            val list = mutableListOf(peeked.value)
            val value = parseValue(iterator)

            if (value is List<*>)
                list.addAll(value)
            else
                list.add(value)
            return list
        }

        return peeked.value
    }

    fun parseMap(iterator: TokenIterator): MutableMap<String, Any> {

        val peekNext = iterator.peekNext()

        val hasMapBeginSymbol = peekNext is MapBeginSymbolToken
        val values = mutableMapOf<String, Any>()
        if (hasMapBeginSymbol)
            iterator.next()
        while (iterator.hasNext()) {
            if (iterator.peekNext() is MapEndSymbolToken)
                break
            val next = iterator.next()
            if (next is IdentifierToken)
                values += parseAssignment(next, iterator)
            else
                throw IllegalStateException("Invalid syntax, no identifier here: $next")
        }
        if (hasMapBeginSymbol) {
            val otherNext = iterator.next()

            if (otherNext !is MapEndSymbolToken)
                throw IllegalStateException("Unclosed map: $peekNext")
        }
        return values
    }
}