package io.angularpay.crypto.ports.inbound;

import io.angularpay.crypto.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
