package com.siupay.pci.gateway.enums

import lombok.AllArgsConstructor
import lombok.Getter

@Getter
@AllArgsConstructor
enum class LimitLockKeyEnum {
    IP,
    USER
}