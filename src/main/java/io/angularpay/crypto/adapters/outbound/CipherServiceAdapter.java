package io.angularpay.crypto.adapters.outbound;

import io.angularpay.crypto.configurations.AngularPayConfiguration;
import io.angularpay.crypto.models.VerifySignatureResponseModel;
import io.angularpay.crypto.ports.outbound.CipherServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CipherServiceAdapter implements CipherServicePort {

    private final WebClient webClient;
    private final AngularPayConfiguration configuration;

    @Override
    public VerifySignatureResponseModel verifySignature(String requestBody, Map<String, String> headers) {
        URI cipherUrl = UriComponentsBuilder.fromUriString(configuration.getCipherUrl())
                .path("/cipher/entries/")
                .path(headers.get("x-angularpay-cipher-reference"))
                .path("/verify")
                .build().toUri();

        return webClient
                .post()
                .uri(cipherUrl.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-angularpay-username", headers.get("x-angularpay-username"))
                .header("x-angularpay-device-id", headers.get("x-angularpay-device-id"))
                .header("x-angularpay-user-reference", headers.get("x-angularpay-user-reference"))
                .header("x-angularpay-cipher-signature", headers.get("x-angularpay-cipher-signature"))
                .body(Mono.just(requestBody), String.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(VerifySignatureResponseModel.class);
                    } else {
                        return Mono.just(new VerifySignatureResponseModel(false));
                    }
                })
                .block();
    }
}
