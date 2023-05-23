package pl.wydarzeniawokolicy.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice(basePackageClasses = [RestErrorHandler::class])
class RestErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun exception(exception: Exception): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}