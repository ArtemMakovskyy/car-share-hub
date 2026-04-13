package com.carsharehub.exception

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomGlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf("error" to ex.message.orEmpty()),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(DataDuplicationException::class)
    fun handleDataDuplicationException(ex: DataDuplicationException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf("error" to ex.message.orEmpty()),
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(RegistrationException::class)
    fun handleRegistrationException(ex: RegistrationException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf("error" to ex.message.orEmpty()),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf("error" to "Invalid credentials"),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(
            mapOf("error" to "Invalid JWT token"),
            HttpStatus.UNAUTHORIZED
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: org.springframework.http.HttpStatusCode,
        request: org.springframework.web.context.request.WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }
        return ResponseEntity(
            mapOf("errors" to errors.joinToString("; ")) as Any,
            HttpStatus.BAD_REQUEST
        )
    }
}
