package blue.paradox.cfs.core.parser

import blue.paradox.cfs.core.PeekingIterator
import blue.paradox.cfs.core.lexer.Token

internal class TokenIterator(private val tokens: List<Token<*>>) : PeekingIterator<Token<*>> {

    var index = 0
        private set

    override fun hasNext(): Boolean {
        return index < tokens.size
    }

    override fun peekNext(): Token<*>? {
        if (!hasNext())
            return null
        return tokens[index]
    }

    override fun next(): Token<*> {
        if (!hasNext())
            throw NoSuchElementException("There are no more tokens in the token list.")
        return tokens[index++]
    }

}