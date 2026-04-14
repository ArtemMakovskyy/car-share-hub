package com.carsharehub.payment

import com.carsharehub.car.CarRepository
import com.carsharehub.exception.EntityNotFoundException
import com.carsharehub.rental.RentalDto
import com.carsharehub.rental.RentalMapper
import com.carsharehub.rental.RentalRepository
import com.carsharehub.strategy.PriceHandler
import com.carsharehub.strategy.PriceStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class StripePaymentServiceImpl(
    private val rentalRepository: RentalRepository,
    private val rentalMapper: RentalMapper,
    private val carRepository: CarRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentMapper: PaymentMapper,
    private val priceStrategy: PriceStrategy
) : PaymentService {

    @Value("\${api.stripe.sk.key:}")
    private lateinit var stripeKey: String

    override fun findAllByRentalUserId(userId: Long, pageable: Pageable): List<PaymentDto> {
        return paymentRepository.findAllByRentalUserId(pageable, userId)
            .content
            .map { paymentMapper.toDto(it) }
    }

    @Transactional
    override fun createPaymentSession(createPaymentSessionDto: CreatePaymentSessionDto): PaymentDto {
        val rental = rentalRepository.findById(createPaymentSessionDto.rentalId!!)
            .orElseThrow { EntityNotFoundException("Can't find rental by id ${createPaymentSessionDto.rentalId}") }

        val rentalDto = rentalMapper.toDto(rental)
        val car = carRepository.findById(rentalDto.carId!!)
            .orElseThrow { EntityNotFoundException("Can't find car by id ${rentalDto.carId}") }

        val paymentType = PaymentType.valueOf(createPaymentSessionDto.paymentType)
        val priceHandler = priceStrategy.get(paymentType)

        val dailyFee = car.dailyFee
        val fineMultiplier = BigDecimal("1.2")
        val smallChange = BigDecimal(100)

        val unitAmount = priceHandler.getTotalPrice(rentalDto, dailyFee, fineMultiplier, smallChange)

        val sessionId = "test_session_${System.currentTimeMillis()}"
        val sessionUrl = "https://checkout.stripe.com/test/$sessionId"
        val amountTotal = BigDecimal(unitAmount).divide(smallChange, 2, RoundingMode.DOWN)

        val payment = Payment(
            sessionId = sessionId,
            status = PaymentStatus.PENDING,
            type = paymentType,
            rental = rental,
            amountToPay = amountTotal,
            sessionUrl = sessionUrl
        )

        val savedPayment = paymentRepository.save(payment)
        return paymentMapper.toDto(savedPayment)
    }

    @Transactional
    override fun handleSuccessfulPayment(sessionId: String): String {
        val payment = paymentRepository.findBySessionId(sessionId)
            .orElseThrow { EntityNotFoundException("Can't find payment by session ID $sessionId") }
        payment.status = PaymentStatus.PAID
        paymentRepository.save(payment)
        return "The rent payment was successful"
    }

    override fun processPaymentCancellation(sessionId: String): String {
        return "The payment can be made later (but the session is available for only 24 hours). Session ID: $sessionId"
    }
}
