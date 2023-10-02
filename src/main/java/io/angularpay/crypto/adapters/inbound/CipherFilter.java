package io.angularpay.crypto.adapters.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.adapters.outbound.CipherServiceAdapter;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.exceptions.ErrorResponse;
import io.angularpay.crypto.models.VerifySignatureResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.angularpay.crypto.common.Constants.ERROR_SOURCE;
import static io.angularpay.crypto.exceptions.ErrorCode.CIPHER_ERROR;

@Slf4j
@RequiredArgsConstructor
public class CipherFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CipherServiceAdapter cipherServiceAdapter;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        servletRequest = new WrappedHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (!request.getMethod().equalsIgnoreCase("POST") && !request.getMethod().equalsIgnoreCase("PUT")) {
            chain.doFilter(servletRequest, response);
            return;
        }

        String requestBody = getBody(request);
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream().collect(Collectors.toMap(h -> h, request::getHeader));

        log.info("verifying signature");
        VerifySignatureResponseModel signatureResponse = cipherServiceAdapter.verifySignature(requestBody, headers);

        if (signatureResponse.isValid()) {
            log.info("signature is valid");
            chain.doFilter(servletRequest, response);
        } else {
            log.info("signature is invalid");
            List<ErrorObject> errors = Collections.singletonList(ErrorObject.builder()
                    .code(CIPHER_ERROR)
                    .message(CIPHER_ERROR.getDefaultMessage())
                    .source(ERROR_SOURCE)
                    .build());

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorReference(UUID.randomUUID().toString())
                    .timestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                    .errors(errors)
                    .build();

            String responseString = objectMapper.writeValueAsString(errorResponse);
            ((HttpServletResponse) response).setStatus(400);
            response.setContentType("application/json");
            response.getOutputStream().write(responseString.getBytes());
        }
    }

    private static String getBody(ServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }
}
