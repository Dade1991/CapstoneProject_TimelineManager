package davidebraghi.CapstoneProject_TimelineManager.exceptions;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Errors_DTO.ErrorsDTO;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Errors_DTO.ErrorsWithListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

    // BAD_REQUEST // 400

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleBadRequestException(BadRequestException ex) {
        return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
    }

    // NOT_FOUND // 404

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorsDTO handleNotFoundException(NotFoundException ex) {
        return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
    }

    // UNAUTHORIZED // 401

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorsDTO handleUnauthorizedException(UnauthorizedException ex) {
        return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
    }

    // FORBIDDEN // 403

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorsDTO handleForbiddenException(ForbiddenException ex) {
        return new ErrorsDTO("We're sorry but you need the proper authorization in order to access.", LocalDateTime.now());
    }

    // VALIDATION // 400

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsWithListDTO handleValidationException(ValidationException ex) {
        return new ErrorsWithListDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrorsMessages());
    }

    // INTERNAL_SERVER_ERROR // 500

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(InternalServerErrorException.class)
    public ErrorsDTO handleInternalServerErrorException(InternalServerErrorException ex) {
        ex.printStackTrace();
        return new ErrorsDTO("A generic issue as occurred. Please, be patient, we will recover the website soon. Thank you.", LocalDateTime.now());
    }
}