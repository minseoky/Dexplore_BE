package info.dexplore.dexplore.handler;

import info.dexplore.dexplore.dto.response.ResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

/**
 * Valid 오류 전역처리 클래스
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, MultipartException.class})
    public ResponseEntity<ResponseDto> validationExceptionHandler(Exception e) {
        e.printStackTrace();
        return ResponseDto.validationFail();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDto> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        e.printStackTrace();
        return ResponseDto.databaseError();
    }
}
