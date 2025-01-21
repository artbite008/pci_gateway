package com.siupay.base.result

import com.siupay.common.api.exception.ErrorCode

data class GenericResult<T>(
    val data: T? = null,
    val success: Boolean = true,
    val code: String = ErrorCode.BUSINESS_ERROR.code,
    val msg: String = ErrorCode.BUSINESS_ERROR.msg,
    val retry: Boolean = false
) {
    companion object {
        fun <T> success(data: T) = GenericResult<T>(data = data, success = true, code = "200", msg = "success")

        fun <T> fail(errorCode: ErrorCode, msg: String) = GenericResult<T>(code = errorCode.code, msg = msg)
        fun <T> fail(msg: String? = null) = if(msg == null) GenericResult() else GenericResult<T>(msg = msg)
    }
}