package io.angularpay.crypto.adapters.outbound;

import io.angularpay.crypto.configurations.AngularPayConfiguration;
import io.angularpay.crypto.models.SchedulerServiceRequest;
import io.angularpay.crypto.models.SchedulerServiceResponse;
import io.angularpay.crypto.ports.outbound.SchedulerServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulerServiceAdapter implements SchedulerServicePort {

    private final WebClient webClient;
    private final AngularPayConfiguration configuration;

    @Override
    public Optional<SchedulerServiceResponse> createScheduledRequest(SchedulerServiceRequest request, Map<String, String> headers) {
        URI schedulerUrl = UriComponentsBuilder.fromUriString(configuration.getSchedulerUrl())
                .path("/scheduler/schedules").build().toUri();

        SchedulerServiceResponse schedulerResponse = webClient
                .post()
                .uri(schedulerUrl.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-angularpay-username", headers.get("x-angularpay-username"))
                .header("x-angularpay-device-id", headers.get("x-angularpay-device-id"))
                .header("x-angularpay-user-reference", headers.get("x-angularpay-user-reference"))
                .header("x-angularpay-correlation-id", headers.get("x-angularpay-correlation-id"))
                .body(Mono.just(request), SchedulerServiceRequest.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(SchedulerServiceResponse.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .block();

        return Objects.nonNull(schedulerResponse)? Optional.of(schedulerResponse): Optional.empty();

    }
}
