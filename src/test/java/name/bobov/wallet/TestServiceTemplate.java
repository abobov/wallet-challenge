package name.bobov.wallet;

import java.util.List;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for testing API.
 */
class TestServiceTemplate {

  private final TestRestTemplate restTemplate;

  private final String serviceRoot;

  public TestServiceTemplate(TestRestTemplate restTemplate, int port) {
    this.restTemplate = restTemplate;
    serviceRoot = "http://localhost:" + port + "/";
  }

  public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType,
                                    Object... urlVariables) {
    return restTemplate.postForEntity(serviceRoot + url, request, responseType, urlVariables);
  }

  public <T> ResponseEntity<T> get(String url, Class<T> responseType, Object... urlVariables) {
    return restTemplate.getForEntity(url, responseType, urlVariables);
  }

  public <T> ResponseEntity<List<T>> getList(String url, Object... urlVariables) {
    var responseType = new ParameterizedTypeReference<List<T>>() {
    };
    return restTemplate.exchange(url, HttpMethod.GET, null, responseType, urlVariables);
  }
}
