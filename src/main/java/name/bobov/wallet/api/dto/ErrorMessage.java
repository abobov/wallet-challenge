package name.bobov.wallet.api.dto;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * DTO for presenting all application errors.
 *
 * Contains unique error code and details of error if any.
 */
@Value
@AllArgsConstructor
public class ErrorMessage {

  @NonNull
  String code;

  String detail;

  /**
   * Create new error message from exception.
   *
   * @param exception source exception
   */
  public ErrorMessage(@NonNull Throwable exception) {
    this(exception.getClass().getSimpleName(), exception.getMessage());
  }
}
