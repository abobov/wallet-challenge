package name.bobov.wallet.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.bobov.wallet.WalletConstants;
import org.springframework.stereotype.Component;

/**
 * HTTP filter for supporting idempotent requests.
 *
 * Filter use HTTP header {@link WalletConstants#IDEMPOTENCY_KEY_HEADER} value to get idempotency
 * key.
 *
 * Idempotency key is build based on header and request path.
 *
 * This implementation store keys in memory and not persist over restarts.
 */
@Component
public class IdempotentRequestFilter extends HttpFilter {

  private final Map<String, Integer> cache = new ConcurrentHashMap<>();

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                          FilterChain chain) throws IOException, ServletException {
    var idempotencyKey = extractIdempotencyKeyFrom(request);
    if (idempotencyKey == null) {
      chain.doFilter(request, response);
      return;
    }
    var cachedResult = cache.get(idempotencyKey);
    if (cachedResult == null) {
      chain.doFilter(request, response);
      cache.put(idempotencyKey, response.getStatus());
    } else {
      response.setStatus(cachedResult);
    }
  }

  private String extractIdempotencyKeyFrom(HttpServletRequest request) {
    String header = request.getHeader(WalletConstants.IDEMPOTENCY_KEY_HEADER);
    if (header == null) {
      return null;
    }
    return request.getRequestURI() + ":" + header;
  }
}
