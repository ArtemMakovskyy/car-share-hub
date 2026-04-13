package com.carsharehub.payment

import com.carsharehub.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/payments")
@SecurityRequirement(name = "Bearer Authentication")
class PaymentController(
    private val paymentService: PaymentService
) {

    @Operation(
        summary = "Get all user's payments",
        description = """
            If user is CUSTOMER he can get only own payments,
            if ADMIN or MANAGER can get any payments.
        """
    )
    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    fun getPaymentsById(
        pageable: org.springframework.data.domain.Pageable,
        @RequestParam(name = "user_id") userId: Long?,
        authentication: Authentication
    ): List<PaymentDto> {
        val user = authentication.principal as User
        val effectiveUserId = if (user.authorities.contains(SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            user.id!!
        } else {
            userId!!
        }
        return paymentService.findAllByRentalUserId(effectiveUserId, pageable)
    }

    @Operation(summary = "Create stripe payment session")
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    fun createPaymentSession(
        @RequestBody @Valid createPaymentSessionDto: CreatePaymentSessionDto
    ): PaymentDto {
        return paymentService.createPaymentSession(createPaymentSessionDto)
    }

    @Operation(summary = "Handle successful payment")
    @GetMapping("/success")
    fun handleSuccessfulPayment(
        @RequestParam(name = "session_id") sessionId: String
    ): String {
        return paymentService.handleSuccessfulPayment(sessionId)
    }

    @Operation(summary = "Process payment cancellation")
    @GetMapping("/cancel")
    fun processPaymentCancellation(
        @RequestParam(name = "session_id") sessionId: String
    ): String {
        return paymentService.processPaymentCancellation(sessionId)
    }
}
