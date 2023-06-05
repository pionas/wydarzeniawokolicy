package pl.wydarzeniawokolicy.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException


@ControllerAdvice(basePackageClasses = [WebErrorHandler::class])
class WebErrorHandler {

    val logger: Logger = LoggerFactory.getLogger(WebErrorHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun exception(exception: NotFoundException): ResponseEntity<String> {
        logger.error(exception.message, exception)
        return ResponseEntity(exception.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun exception(exception: AccessDeniedException): ResponseEntity<String> {
        logger.error(exception.message, exception)
        return ResponseEntity(exception.message, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(value = [ModelException::class, MethodArgumentNotValidException::class, HttpMessageNotReadableException::class])
    fun methodArgumentNotValidException(exception: Exception): ResponseEntity<String> {
        logger.error(exception.message, exception)
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun exception(exception: Exception): ResponseEntity<String> {
        logger.error(exception.message, exception)
        return ResponseEntity(exception.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}