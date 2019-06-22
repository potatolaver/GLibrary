package org.gg.library.utils

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.text.TextUtils
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import com.google.gson.Gson
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 用于方便的进行Bundle数据存取。
 * @author haoge on 2018/6/14
 */
class GsonBundle private constructor(private val bundle: Bundle) {

    /** 将map中的所有数据均存放至容器中*/
    fun put(map: Map<String, Any?>): GsonBundle {
        map.forEach { put(it.key, it.value) }
        return this
    }

    /** 直接一起存储不定数量的键值对数据到容器中*/
    fun put(vararg items: Pair<String, Any?>): GsonBundle {
        items.forEach { put(it.first, it.second) }
        return this
    }

    /**
     * 将指定[key]-[value]键值对数据存储到Bundle容器中
     *
     * **存储规则：**
     *
     * 1. 当[value]的数据类型支持直接被bundle进行存储时，直接进行存储
     * 2. 当[value]的数据类型不支持被bundle进行存储是，则将先将value转换为json后再进行存储
     */
    fun put(key: String, value: Any?): GsonBundle {
        if (TextUtils.isEmpty(key) || value == null) {
            return this
        }

        var store = true
        // 根据value的类型，选择合适的api进行存储
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is CharSequence -> bundle.putCharSequence(key, value)
            is String -> bundle.putString(key, value)
            is Float -> bundle.putFloat(key, value)
            is Double -> bundle.putDouble(key, value)
            is Char -> bundle.putChar(key, value)
            is Short -> bundle.putShort(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            is Parcelable -> bundle.putParcelable(key, value)
            is SparseArray<*> -> bundle.putSparseParcelableArray(key, value as SparseArray<out Parcelable>)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> bundle.putCharSequenceArray(key, value as Array<out CharSequence>)
                value.isArrayOf<String>() -> bundle.putStringArray(key, value as Array<out String>?)
                value.isArrayOf<Parcelable>() -> bundle.putParcelableArray(key, value as Array<out Parcelable>?)
                else -> store = false
            }
            is Size -> bundle.putSize(key, value)
            is SizeF -> bundle.putSizeF(key, value)
            is IntArray -> bundle.putIntArray(key, value)
            is LongArray -> bundle.putLongArray(key, value)
            is FloatArray -> bundle.putFloatArray(key, value)
            is DoubleArray -> bundle.putDoubleArray(key, value)
            is CharArray -> bundle.putCharArray(key, value)
            is ShortArray -> bundle.putShortArray(key, value)
            is BooleanArray -> bundle.putBooleanArray(key, value)
            is IBinder -> bundle.putBinder(key, value)
            is Serializable -> when (value) {
                is Collection<*>, is Map<*, *> -> store = false
                else -> bundle.putSerializable(key, value)
            }
            else -> store = false
        }

        if (store.not()) {
            bundle.putString(key, toJSON(value))
        }

        return this
    }

    /** 获取指定[key]对应的值，若获取失败，则返回默认值[defValue]*/
    inline fun <reified T> get(key: String, defValue: T): T {
        return get<T>(key) ?: defValue
    }

    /** 获取指定[key]对应的值，可为null*/
    inline fun <reified T> get(key: String): T? {
        val type = object : TypeGeneric<T>(T::class.java) {}.getType()
        return get(key, type) as T?
    }

    /** 获取指定[key]对应的值，类型为[clazz], 若获取失败，则返回默认值[defValue]*/
    fun <T> get(key: String, clazz: Class<T>, defValue: T): T {
        return get(key, clazz) ?: defValue
    }

    /** 获取指定[key]对应的值，类型为[clazz], 可为null*/
    fun <T> get(key: String, clazz: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return get(key, type = clazz) as T?
    }

    /** 获取指定[key]对应的值，类型为[type], 可为null*/
    fun get(key: String, type: Type): Any? {
        val rawType = getRawClass(type)
        var value = bundle.get(key) ?: return returnsValue(null, rawType)
        // 当取出数据类型与指定类型匹配时。直接返回
        if (rawType.isInstance(value)) {
            return value
        }

        if (value !is String) {
            // 对于数据类型不为String的，先行转换为json。
            value = toJSON(value)
        }

        if (value.isEmpty()) {
            // 过滤空数据
            returnsValue(null, rawType)
        }

        // 处理两种情况下的数据自动转换：
        @Suppress("IMPLICIT_CAST_TO_ANY")
        return when (rawType.canonicalName) {
            // String自动转换基本数据类型
            "byte", "java.lang.Byte" -> value.toByte()
            "short", "java.lang.Short" -> value.toShort()
            "int", "java.lang.Integer" -> value.toInt()
            "long", "java.lang.Long" -> value.toLong()
            "float", "java.lang.Float" -> value.toFloat()
            "double", "java.lang.Double" -> value.toDouble()
            "char", "java.lang.Character" -> value.toCharArray()[0]
            "boolean", "java.lang.Boolean" -> value.toBoolean()
            // 特殊处理StringBuilder、StringBuffer
            "java.lang.StringBuilder" -> StringBuilder(value)
            "java.lang.StringBuffer" -> StringBuffer(value)
            else -> parseJSON(value, type)
        }
    }

    private fun getRawClass(type: Type): Class<*> {
        return when (type) {
            is Class<*> -> type
            is ParameterizedType -> getRawClass(type.rawType)
            else -> throw RuntimeException("Only support of Class and ParameterizedType")
        }
    }

    // 兼容java环境使用，对返回数据进行二次处理。避免对基本数据类型返回null导致crash
    private fun returnsValue(value: Any?, type: Class<*>): Any? {
        if (value != null) return value

        return when (type.canonicalName) {
            "byte" -> 0.toByte()
            "short" -> 0.toShort()
            "int" -> 0
            "long" -> 0.toLong()
            "float" -> 0.toFloat()
            "double" -> 0.toDouble()
            "char" -> '0'
            "boolean" -> false
            else -> null
        }
    }

    private fun toJSON(value: Any): String {
        return Gson().toJson(value)
    }

    private fun parseJSON(json: String, type: Type): Any {
        return Gson().fromJson(json, type)
    }

    companion object {

        @JvmStatic
        fun create(source: Bundle? = null): GsonBundle {
            return GsonBundle(source ?: Bundle())
        }
    }
}

abstract class TypeGeneric<T>(private val raw: Class<*>) {
    fun getType(): Type {
        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return if (type is Class<*> || type is ParameterizedType) type else raw
    }
}