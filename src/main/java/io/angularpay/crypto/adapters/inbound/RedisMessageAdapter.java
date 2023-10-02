package io.angularpay.crypto.adapters.inbound;

import io.angularpay.crypto.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.crypto.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.crypto.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.crypto.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
