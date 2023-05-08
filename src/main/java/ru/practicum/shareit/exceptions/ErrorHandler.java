package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

/**
 * Класс описывающий ErrorHandler для централизованной обработки ошибок.
 */

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistExceptionUserOrFilm(final UserAlreadyExistException e) {
        log.warn("Исключение UserAlreadyExistException {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidationIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationIdException(final ValidationIdException e) {
        log.warn("Исключение ValidationIdException {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Ошибка ValidationException {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse onThrowableException(final Throwable e) {
//        log.warn("Исключение Throwable {}", e.getMessage());
//        return new ErrorResponse("Непредвиденная ошибка");
//    }
}


//
//    @ExceptionHandler(FilmAlreadyExistException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleAlreadyExistExceptionUserOrFilm(final FilmAlreadyExistException e) {
//        log.warn("Исключение FilmAlreadyExistException {}", e.getMessage());
//        return new ErrorResponse(e.getMessage());
//    }
//
//    @ExceptionHandler(IncorrectParameterException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleValidationException(final IncorrectParameterException e) {
//        log.warn("Исключение IncorrectParameterException {}", e.getMessage());
//        return new ErrorResponse("Ошибка с параметром{}", e.getParameter());
//    }
//

//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<String> onConstraintValidationException(final ConstraintViolationException ex) {
//        log.warn("Исключение ConstraintViolationException {}", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//

