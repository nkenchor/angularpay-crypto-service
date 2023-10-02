package io.angularpay.crypto.domain.commands;

import io.angularpay.crypto.domain.CryptoRequest;

public interface CryptoRequestSupplier {
    CryptoRequest getCryptoRequest();
}
