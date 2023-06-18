package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

/**
 * Класс описывающий ErrorHandler для централизованной обработки ошибок.
 */

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ValidationIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationIdException(final ValidationIdException e) {
        log.warn("Исключение ValidationIdException {}", e.getMessage());
        return new ErrorResponse("ValidationIdException", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSqlException(final DataIntegrityViolationException e) {
        log.warn("Ошибка DataIntegrityViolationException {}", e.getMessage());
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("Ошибка ConstraintViolationException {}", e.getMessage());
        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            ItemIsNotAvailableForBookingException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchExceptionD(Exception e) {
        log.warn("Ошибка {}, описание: {}", e.getClass(), e.getMessage());
        String exceptionType;
        String errorMessage;

        if (e instanceof ItemIsNotAvailableForBookingException) {
            exceptionType = "ItemIsNotAvailableForBookingException";
        } else if (e instanceof MissingRequestHeaderException) {
            exceptionType = "MissingRequestHeaderException";
        } else if (e instanceof MissingServletRequestParameterException) {
            exceptionType = "MissingServletRequestParameterException";
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            exceptionType = "Unknown state: UNSUPPORTED_STATUS";
        } else {
            exceptionType = "Неизвестное исключение";
        }
        errorMessage = e.getMessage();
        return new ErrorResponse(exceptionType, errorMessage);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowableException(final Throwable e) {
        log.warn("Ошибка Throwable {}", e.getMessage());
        return new ErrorResponse("ValidationException", e.getMessage());
    }

}


