package com.siupay.pci.gateway.exception

import com.siupay.common.api.exception.ErrorCode
import com.siupay.common.api.exception.PaymentException

class FormatException(e: Throwable) : PaymentException(ErrorCode.SERVER_ERROR, e.message.orEmpty())