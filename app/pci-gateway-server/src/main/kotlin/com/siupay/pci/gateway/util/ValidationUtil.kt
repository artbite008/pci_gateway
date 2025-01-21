package com.siupay.pci.gateway.util

import java.util.*
import javax.validation.Validation
import javax.validation.groups.Default

object ValidationUtil {
    private val validator = Validation.buildDefaultValidatorFactory().validator
    fun <T> validateEntity(obj: T): ValidationResult {
        val result = ValidationResult()
        val set = validator.validate(obj, Default::class.java)
        if (set != null && set.size != 0) {
            result.hasErrors = true
            val errorMsg: MutableMap<String, String> = HashMap()
            for (cv in set) {
                errorMsg[cv.propertyPath.toString()] = cv.message
            }
            result.errorMsg = errorMsg
        }
        return result
    }
}