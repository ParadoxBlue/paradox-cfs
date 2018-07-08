package blue.paradox.cfs.core

internal interface PeekingIterator<T> : Iterator<T> {
    fun peekNext(): T?
}