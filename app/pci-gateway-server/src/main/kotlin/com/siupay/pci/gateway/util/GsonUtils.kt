package com.siupay.pci.gateway.util

import com.google.gson.Gson

object GsonUtils {
    private val gson = Gson()
    @JvmStatic
    fun toJson(obj: Any?): String {
        return gson.toJson(obj)
    }

    @JvmStatic
    fun <T> toObject(json: String?, classOfT: Class<T>?): T {
        return gson.fromJson(json, classOfT)
    }
}