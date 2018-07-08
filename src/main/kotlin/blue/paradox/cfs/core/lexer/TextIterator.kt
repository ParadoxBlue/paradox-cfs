package blue.paradox.cfs.core.lexer

import blue.paradox.cfs.core.PeekingIterator

internal class TextIterator(private val text: String) : PeekingIterator<Char> {

    var index: Int = 0
        private set

    override fun hasNext(): Boolean {
        return index < text.length
    }

    override fun peekNext(): Char? {
        if (!hasNext())
            return null

        return text[index]
    }

    fun whileNext(body: TextIterator.() -> Unit) {
        while (hasNext())
            apply(body)
    }

    fun current(): Char {
        return text[index - 1]
    }

    override fun next(): Char {
        if (!hasNext())
            throw NoSuchElementException("There are no more characters in this string.")
        return text[index++]
    }
}