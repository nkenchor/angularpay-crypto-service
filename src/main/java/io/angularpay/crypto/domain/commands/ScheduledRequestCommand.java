package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.configurations.AngularPayConfiguration;
import io.angularpay.crypto.domain.Role;
import io.angularpay.crypto.exceptions.CommandException;
import io.angularpay.crypto.exceptions.ErrorCode;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.models.ResourceReferenceResponse;
import io.angularpay.crypto.models.ScheduledRequestCommandRequest;
import io.angularpay.crypto.models.SchedulerServiceRequest;
import io.angularpay.crypto.models.SchedulerServiceResponse;
import io.angularpay.crypto.ports.outbound.SchedulerServicePort;
import io.angularpay.crypto.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

import static io.angularpay.crypto.common.Constants.ERROR_SOURCE;
import static io.angularpay.crypto.common.Constants.SERVICE_CODE;
import static io.angularpay.crypto.exceptions.ErrorCode.VALIDATION_ERROR;
import static io.angularpay.crypto.helpers.Helper.commaSeparated;

@Slf4j
@Service
public class ScheduledRequestCommand extends AbstractCommand<ScheduledRequestCommandRequest, ResourceReferenceResponse> {

    private final DefaultConstraintValidator validator;
    private final SchedulerServicePort schedulerServicePort;
    private final AngularPayConfiguration configuration;

    public ScheduledRequestCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            SchedulerServicePort schedulerServicePort,
            AngularPayConfiguration configuration) {
        super("ScheduledRequestCommand", mapper);
        this.validator = validator;
        this.schedulerServicePort = schedulerServicePort;
        this.configuration = configuration;
    }

    @Override
    protected String getResourceOwner(ScheduledRequestCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected ResourceReferenceResponse handle(ScheduledRequestCommandRequest request) {
        String payload;
        try {
            payload = mapper.writeValueAsString(request.getCreateRequest());
        } catch (JsonProcessingException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_JSON)
                    .message(ErrorCode.INVALID_JSON.getDefaultMessage())
                    .cause(exception)
                    .build();
        }

        SchedulerServiceRequest schedulerServiceRequest = SchedulerServiceRequest.builder()
                .description("scheduled " + SERVICE_CODE + " request for " + request.getAuthenticatedUser().getUserReference())
                .actionEndpoint(this.configuration.getSelfUrl() + "/pmt/requests")
                .payload(payload)
                .runAt(request.getRunAt())
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-angularpay-username", request.getAuthenticatedUser().getUsername());
        headers.put("x-angularpay-device-id", request.getAuthenticatedUser().getDeviceId());
        headers.put("x-angularpay-user-reference", request.getAuthenticatedUser().getUserReference());
        headers.put("x-angularpay-correlation-id", request.getAuthenticatedUser().getCorrelationId());
        headers.put("x-angularpay-user-roles", commaSeparated(request.getAuthenticatedUser().getRoles()));

        Optional<SchedulerServiceResponse> optional = schedulerServicePort.createScheduledRequest(schedulerServiceRequest, headers);
        if (optional.isEmpty()) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(ErrorCode.SCHEDULER_SERVICE_ERROR)
                    .message(ErrorCode.SCHEDULER_SERVICE_ERROR.getDefaultMessage())
                    .build();
        }
        return new ResourceReferenceResponse(optional.get().getReference());
    }

    @Override
    protected List<ErrorObject> validate(ScheduledRequestCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();
        try {
            Instant.parse(request.getRunAt());
        } catch (DateTimeParseException exception) {
            errors.add(ErrorObject.builder()
                    .code(VALIDATION_ERROR)
                    .message("run_at must be a valid date")
                    .source(ERROR_SOURCE)
                    .build());
        }
        errors.addAll(this.validator.validate(request));
        return errors;
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

}
