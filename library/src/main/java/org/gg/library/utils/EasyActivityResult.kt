package org.gg.library.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.util.*

/**
 * 启动Activity并接收onActivityResult进行派发处理。提升activity result可维护性
 * @author haoge on 2018/6/1
 */
object EasyActivityResult {

    // 缓存容器, 临时保存进行启动的activity和与之对应的callback实例。用于在接收回传数据时
    private val container = mutableMapOf<Activity, MutableMap<Int, ResultCallback>>()
    private val codeGenerator = Random()// 用于进行requestCode自动生成的生成器
    private var lastTime = 0L

    /**
     * 使用 **context.startActivityForResult(intent, requestCode)** 进行页面启动。并绑定callback
     */
    @JvmStatic
    fun startActivity(context: Context, intent: Intent, callback: ResultCallback?) {
        startActivity(context, intent, callback, null)
    }

    /**
     * 使用 **context.startActivityForResult(intent, requestCode, options)** 进行页面启动。并绑定callback
     */
    @JvmStatic
    fun startActivity(context: Context, intent: Intent, callback: ResultCallback?, options: Bundle?) {
        val current = System.currentTimeMillis()
        val last = lastTime
        lastTime = current
        // 防暴击：两次启动间隔必须大于1秒。
        if (current - last < 1000) {
            return
        }

        if (context !is Activity || callback == null) {
            context.startActivity(intent)
        } else {
            // 自动生成有效的requestCode进行使用。
            val requestCode = codeGenerator.nextInt(0x0000FFFF)
            if (options == null) {
                context.startActivityForResult(intent, requestCode)
            } else {
                context.startActivityForResult(intent, requestCode, options)
            }

            // 保存回调缓存
            if (container.containsKey(context)) {
                container[context]?.put(requestCode, callback)
            } else {
                container[context] = mutableMapOf(Pair(requestCode, callback))
            }
        }
    }

    @JvmStatic
    fun dispatch(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (!container.containsKey(activity)) {
            return
        }

        // 从缓存中取出与此activity绑定的、与requestCode相匹配的回调。进行回调通知
        container[activity]?.remove(requestCode)?.onResult(resultCode, data)

        // 清理无用的缓存条目。避免内存泄漏
        releaseInvalidItems()
    }

    private fun releaseInvalidItems() {
        val keys = container.keys
        val iterator = keys.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.isFinishing || container[next]?.isEmpty() == true || next.isDestroyed) {
                iterator.remove()
            }
        }
    }
}

interface ResultCallback {
    fun onResult(code: Int, data: Intent?)
}