package com.siupay.pci.gateway.controller

import com.siupay.base.result.GenericResult
import com.siupay.pci.gateway.dto.BindCardRequest
import com.siupay.pci.gateway.dto.BindCardResponse
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.gateway.service.BindCardService
import com.siupay.pci.gateway.service.TokenService
import com.siupay.pci.gateway.util.toJson
import com.siupay.pci.tokenservice.dto.CardTokenRequest
import com.siupay.pci.tokenservice.dto.CardTokenResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import com.siupay.pci.gateway.service.BindCardService.Companion.SESSION_ID_IS_OVERDUE

@RestController
@RequestMapping("/v1/card")
class CardTokenController(
    val tokenService: TokenService,
    val bindCardService: BindCardService
) {
    private val log = LoggerFactory.getLogger(com.siupay.pci.gateway.controller.CardTokenController::class.java)

    @RequestMapping(
        value = ["/getCardToken"],
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    //PA-2558: PCI security
    @ResponseBody
    fun getCardToken(@RequestBody request: com.siupay.pci.gateway.dto.BindCardRequest): GenericResult<com.siupay.pci.gateway.dto.BindCardResponse> {
        log.info(
            "#CardTokenController#getCardToken request requestId {}, sessionId {}",
            request.requestId,
            request.sessionId
        )
        val response: CardTokenResponse?
        var credential: String? = null
        try {
            val uid = bindCardService.validateSession(request.sessionId)
            response = tokenService.getCardToken(
                CardTokenRequest(
                    channelCardId = request.channelCardId,
                    requestId = request.requestId,
                    cardNumber = request.cardNumber,
                    cvv = request.cvv,
                    expiryYear = request.expiryYear,
                    expiryMonth = request.expiryMonth
                )
            )
            credential = uid?.let { bindCardService.sign(uid, response?.token) }
        } catch (e: Exception) {
            return if (e is AccessGatewayException && SESSION_ID_IS_OVERDUE.equals(e.message, ignoreCase = true)) {
                GenericResult.fail(e.code, e.message)
            } else {
                GenericResult.fail(e.message)
            }
        }
        log.info(
            "#CardTokenController#getCardToken response requestId {},with response {} credential {} ",
            request.requestId,
            response?.toJson(),
            credential
        )
        return GenericResult.success(
                com.siupay.pci.gateway.dto.BindCardResponse(
                        requestId = response?.requestId!!,
                        token = response.token,
                        first6Last4 = response.first6Last4,
                        expiryYear = response.expiryYear,
                        expiryMonth = response.expiryMonth,
                        bin = response.bin,
                        last4 = response.last4,
                        prefix6 = response.prefix6,
                        credential = credential
                )
        )
    }
}