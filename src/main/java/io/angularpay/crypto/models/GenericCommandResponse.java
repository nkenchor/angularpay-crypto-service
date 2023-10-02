
package io.angularpay.crypto.models;

import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.commands.CryptoRequestSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GenericCommandResponse extends GenericReferenceResponse implements CryptoRequestSupplier {

    private final String requestReference;
    private final String itemReference;
    private final CryptoRequest cryptoRequest;
}
