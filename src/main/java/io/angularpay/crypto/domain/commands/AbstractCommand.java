package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.domain.Role;
import io.angularpay.crypto.exceptions.CommandException;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.exceptions.ValidationException;
import io.angularpay.crypto.models.AccessControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;

import static io.angularpay.crypto.exceptions.ErrorCode.*;
import static io.angularpay.crypto.helpers.Helper.*;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCommand<T extends AccessControl, R> {

    protected final String name;
    protected final ObjectMapper mapper;

    public R execute(T request) {
        try {
            log.info("received {} request {}", this.name, maskAuthenticatedUser(mapper, request));
            log.info("validating {} request...", this.name);

            List<ErrorObject> validationErrors = this.validate(request);

            if (!CollectionUtils.isEmpty(validationErrors)) {
                log.info("{} request validation failed!", this.name);
                log.info("validation errors: {}", writeAsStringOrDefault(mapper, validationErrors));
                ValidationException exception = new ValidationException(validationErrors);
                throw CommandException.builder()
                        .status(resolveStatus(validationErrors))
                        .errorCode(VALIDATION_ERROR)
                        .cause(exception)
                        .message(String.format("Validation failed for %s request", this.name))
                        .build();
            }

            boolean hasPermittedRole = hasPermittedRole(this.permittedRoles(), request.getAuthenticatedUser().getRoles());

            boolean isResourceOwner = false;
            String resourceOwner = this.getResourceOwner(request);
            if (!hasPermittedRole && StringUtils.hasText(resourceOwner) && StringUtils.hasText(request.getAuthenticatedUser().getUserReference())) {
                isResourceOwner = request.getAuthenticatedUser().getUserReference().equalsIgnoreCase(resourceOwner);
            }

            if (!hasPermittedRole && !isResourceOwner) {
                throw CommandException.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .errorCode(AUTHORIZATION_ERROR)
                        .message(String.format("Authorization failed for %s request", this.name))
                        .build();
            }

            R response = this.handle(request);
            log.info("{} request successfully processed", this.name);

            String responseText = writeAsStringOrDefault(mapper, response);

            if (this instanceof UpdatesPublisherCommand && response instanceof CryptoRequestSupplier) {
                log.info("publishing {} update to REDIS => message payload:  {}", this.name, responseText);
                Executors.newSingleThreadExecutor().submit(() -> {
                    ((UpdatesPublisherCommand)this).publishUpdates((CryptoRequestSupplier)response);
                });
            }
            if (this instanceof TTLPublisherCommand && response instanceof CryptoRequestSupplier) {
                log.info("publishing {} TTL to REDIS", this.name);
                Executors.newSingleThreadExecutor().submit(() -> {
                    ((TTLPublisherCommand)this).publishTTL((CryptoRequestSupplier)response);
                });
            }
            if (this instanceof UserNotificationsPublisherCommand && response instanceof CryptoRequestSupplier) {
                log.info("publishing {} User Notification to REDIS", this.name);
                Executors.newSingleThreadExecutor().submit(() -> {
                    ((UserNotificationsPublisherCommand)this).publishUserNotification((CryptoRequestSupplier)response);
                });
            }

            log.info("returning {} response {}", this.name, responseText);
            if (this instanceof ResourceReferenceCommand) {
                return ((ResourceReferenceCommand<R, R>) this).map(response);
            } else {
                return response;
            }
        } catch (Exception exception) {
            log.error("An error occurred while processing {} request", this.name, exception);
            if (exception instanceof CommandException) {
                throw ((CommandException) exception);
            } else {
                throw CommandException.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorCode(GENERIC_ERROR)
                        .cause(exception)
                        .message(String.format("An error occurred while processing %s request", this.name))
                        .build();
            }
        }
    }

    protected abstract String getResourceOwner(T request);

    protected abstract R handle(T request);

    protected abstract List<ErrorObject> validate(T request);

    protected abstract List<Role> permittedRoles();
}
