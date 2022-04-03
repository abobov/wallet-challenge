package name.bobov.wallet.config;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import name.bobov.wallet.api.dto.ErrorMessage;
import name.bobov.wallet.exceptions.WalletException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This handler process {@link WalletException} and validation exceptions, wrap them into {@link
 * ErrorMessage} class.
 *
 * HTTP status of response is got from {@link ResponseStatus} annotation of exception.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String INVALID_DATA_CODE = "InvalidData";

  @ExceptionHandler(WalletException.class)
  protected ResponseEntity<ErrorMessage> handleWalletException(WalletException exception) {
    return ResponseEntity.status(findResponseStatusOf(exception))
      .body(new ErrorMessage(exception));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers,
                                                                HttpStatus status,
                                                                WebRequest request) {
    ErrorMessage invalidData = createErrorMessageFrom(ex);
    return ResponseEntity.badRequest()
      .body(invalidData);
  }

  private HttpStatus findResponseStatusOf(WalletException exception) {
    var responseStatus = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
    if (responseStatus == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return responseStatus.value();
  }

  private ErrorMessage createErrorMessageFrom(MethodArgumentNotValidException exception) {
    String errorDetails = exception.getBindingResult()
      .getAllErrors()
      .stream()
      .filter(FieldError.class::isInstance)
      .map(FieldError.class::cast)
      .map(DefaultMessageSourceResolvable::getDefaultMessage)
      .collect(Collectors.joining("; "));
    return new ErrorMessage(INVALID_DATA_CODE, errorDetails);
  }

}
