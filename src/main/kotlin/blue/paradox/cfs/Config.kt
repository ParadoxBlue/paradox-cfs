package blue.paradox.cfs

import blue.paradox.cfs.core.lexer.Lexer
import blue.paradox.cfs.core.parser.Parser
import blue.paradox.cfs.core.writer.ConfigWriter
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@Suppress("UNCHECKED_CAST")
class Config(@PublishedApi internal val content: MutableMap<String, Any>) {
    constructor(file: File) : this(Parser.parse(Lexer.lex(file)))
    constructor() : this(HashMap())

    private fun getManual(key: String): Any? {
        val split = key.split(".")

        if (split.size == 1)
            return content[split[0]]

        var curMap: Map<String, Any>? = content

        for (i in 0 until split.size - 1) {
            curMap = curMap?.get(split[i]) as? Map<String, Any>
        }

        return curMap?.get(split[split.size - 1])
    }

    fun number(key: String): Number? {
        return getManual(key) as? Number
    }

    fun int(key: String): Int? {
        return number(key)?.toInt()
    }

    fun double(key: String): Double? {
        return number(key)?.toDouble()
    }

    fun float(key: String): Float? {
        return number(key)?.toFloat()
    }

    fun long(key: String): Long? {
        return number(key)?.toLong()
    }

    fun byte(key: String): Byte? {
        return number(key)?.toByte()
    }

    fun short(key: String): Short? {
        return number(key)?.toShort()
    }

    fun string(key: String): String? {
        return getManual(key) as? String
    }

    fun bool(key: String): Boolean? {
        return getManual(key) as? Boolean
    }

    fun <T> list(key: String): List<T>? {
        return getManual(key) as? List<T>
    }

    fun <T> map(key: String): MutableMap<String, T>? {
        return getManual(key) as? MutableMap<String, T>
    }

    fun section(key: String): Config? {
        return Config(map<Any>(key) ?: return null)
    }

    inline fun <reified T : Any> serialized(key: String): T? {
        return serialized(key, T::class)
    }

    fun <T : Any> serialized(key: String, clazz: KClass<T>): T? {
        val section = section(key) ?: return null
        val constructor = clazz.primaryConstructor ?: return null
        val arguments = mutableMapOf<KParameter, Any?>()

        for (parameter in constructor.parameters) {
            val name = parameter.name ?: return null

            arguments[parameter] = when (parameter.type.jvmErasure) {
                String::class -> section.string(name)
                Int::class -> section.int(name)
                Float::class -> section.float(name)
                Double::class -> section.double(name)
                Long::class -> section.long(name)
                Byte::class -> section.byte(name)
                Short::class -> section.short(name)
                Boolean::class -> section.bool(name)
                Number::class -> section.number(name)
                List::class -> section.list<Any>(name)
                Map::class -> section.map<Any>(name)
                else -> section.serialized(name, parameter.type.jvmErasure)
            }
        }

        return constructor.callBy(arguments)
    }

    private fun manualSet(key: String, value: Any) {
        if (value is Config)
            set(key, value.content)
        content[key] = value
    }

    inline operator fun <reified T : Any> set(key: String, value: T) {
        serialize(key, value, T::class)
    }

    @PublishedApi
    internal fun serialize(key: String, value: Any, clazz: KClass<*>) {
        when (value) {
            is String, is Boolean, is Number, is List<*>, is Map<*, *>, is Config -> manualSet(key, value)
            else -> {
                val section = Config()
                val properties = clazz.memberProperties
                for (property in properties) {
                    val getter = property.getter
                    section.serialize(property.name, getter.call(value) ?: continue, property.returnType.jvmErasure)
                }

                content[key] = section.content
            }
        }
    }

    fun write(file: File) {
        ConfigWriter.toFile(this, file)
    }

    override fun toString(): String {
        return content.toString()
    }

    infix fun String.to(value: Any) = serialize(this, value, value::class)

    companion object {
        operator fun invoke(body: Config.() -> Unit): Config {
            return Config().apply(body)
        }
    }

}