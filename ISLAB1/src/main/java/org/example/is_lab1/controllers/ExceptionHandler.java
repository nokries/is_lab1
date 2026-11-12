package org.example.is_lab1.controllers;


import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.is_lab1.models.dto.ErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        if (message == null) {
            message = "Запрашиваемый объект не найден";
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Объект не найден",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = "Ошибка проверки данных";
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            FieldError first = fieldErrors.get(0);
            details = first.getField() + ": " + first.getDefaultMessage();
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка проверки данных",
                details,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Нарушение целостности данных",
                ex.getMostSpecificCause().getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleJpa(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка запроса к данным",
                ex.getMostSpecificCause().getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Передали {"id": N} несуществующего города/кольца - Spring превращает EntityNotFoundException в ObjectRetrievalFailureException
    @org.springframework.web.bind.annotation.ExceptionHandler(ObjectRetrievalFailureException.class)
    public ResponseEntity<ErrorResponse> handleRetrievalFailure(ObjectRetrievalFailureException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Объект не найден",
                "Связанный объект не найден: " + ex.getPersistentClassName() + " id=" + ex.getIdentifier(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Нарушение ограничений Bean Validation, найденное при проверке сущности (новое кольцо / город / существо)
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String details = "";
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            if (!details.isEmpty()) {
                details = details + "; ";
            }
            details = details + violation.getMessage();
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка проверки данных",
                details,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Некорректный JSON / неверный тип значения в теле запроса (например, число не помещается в int/long)
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String details = "Некорректное тело запроса: проверьте формат и типы значений";
        // Ищем в цепочке причин ошибку Jackson с путём до поля (ring.power, age и т.п.)
        Throwable t = ex.getCause();
        while (t != null && !(t instanceof JsonMappingException)) {
            t = t.getCause();
        }
        if (t instanceof JsonMappingException jme && !jme.getPath().isEmpty()) {
            String field = "";
            for (JsonMappingException.Reference ref : jme.getPath()) {
                if (!field.isEmpty()) {
                    field = field + ".";
                }
                if (ref.getFieldName() != null) {
                    field = field + ref.getFieldName();
                } else {
                    field = field + "[" + ref.getIndex() + "]";
                }
            }
            boolean outOfRange = jme.getCause() instanceof InputCoercionException;
            String value = "";
            if (jme instanceof InvalidFormatException ife) {
                value = " (значение: " + ife.getValue() + ")";
            }
            String range = rangeHint(jme.getPath().get(jme.getPath().size() - 1).getFieldName());

            String reason;
            if (outOfRange) {
                reason = "число слишком большое или слишком маленькое";
            } else {
                String hint;
                if (range != null) {
                    hint = ", ожидается число";
                } else {
                    hint = ", проверьте тип и формат";
                }
                reason = "недопустимое значение" + value + hint;
            }
            String allowed = "";
            if (range != null) {
                allowed = ". Допустимо: " + range;
            }
            details = "Поле '" + field + "': " + reason + allowed;
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Некорректный запрос",
                details,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Допустимый диапазон по имени поля (границы типов Java и ограничения задания)
    static String rangeHint(String field) {
        if (field == null) return null;
        if (field.equals("x")) {
            return "целое число от -2147483648 до 735";
        }
        if (field.equals("y")) {
            return "целое число от -2147483648 до 2147483647";
        }
        if (field.equals("age") || field.equals("attackLevel") || field.equals("populationDensity")) {
            return "целое число от 1 до 9223372036854775807";
        }
        if (field.equals("power") || field.equals("weight") || field.equals("population")) {
            return "целое число от 1 до 2147483647";
        }
        if (field.equals("area")) {
            return "число больше 0";
        }
        if (field.equals("id")) {
            return "целое число от 1 до 9223372036854775807";
        }
        return null;
    }

    // Параметр пути/запроса неверного типа или вне диапазона (например, "1231233123123" не помещается в int)
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Некорректный запрос",
                "Параметр '" + ex.getName() + "': значение '" + ex.getValue()
                        + "' не является корректным целым числом (допустимо от -2147483648 до 2147483647)",
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Неизвестный тип существа и т.п.
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Некорректный запрос",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Ошибки БД, которые Spring не отнёс к DataIntegrityViolation (так бывает с EclipseLink), и ошибки коммита
    @org.springframework.web.bind.annotation.ExceptionHandler({DataAccessException.class, TransactionSystemException.class})
    public ResponseEntity<ErrorResponse> handleDatabase(Exception ex, HttpServletRequest request) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String message = root.getMessage();
        if (message == null) {
            message = "Ошибка базы данных";
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Ошибка базы данных",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        String message = ex.getMessage();
        if (message == null) {
            message = "Произошла непредвиденная ошибка";
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Непредвиденная ошибка",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}