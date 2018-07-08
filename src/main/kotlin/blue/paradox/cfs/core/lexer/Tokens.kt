package blue.paradox.cfs.core.lexer

internal sealed class Token<T>(val value: T, val index: Int)
internal class IdentifierToken(value: String, index: Int) : Token<String>(value, index)

internal sealed class SymbolToken(value: Char, index: Int) : Token<Char>(value, index)
internal class AssignmentSymbolToken(index: Int) : SymbolToken(':', index)
internal class SeparatorSymbolToken(index: Int) : SymbolToken(',', index)
internal class MapBeginSymbolToken(index: Int) : SymbolToken('[', index)
internal class MapEndSymbolToken(index: Int) : SymbolToken(']', index)

internal sealed class LiteralToken<T>(value: T, index: Int) : Token<T>(value, index)
internal class StringLiteralToken(value: String, index: Int) : LiteralToken<String>(value, index)
internal class NumberLiteralToken(value: Number, index: Int) : LiteralToken<Number>(value, index)
internal class BooleanLiteralToken(value: Boolean, index: Int) : LiteralToken<Boolean>(value, index)