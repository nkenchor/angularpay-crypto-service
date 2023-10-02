package io.angularpay.crypto.helpers;

import io.angularpay.crypto.domain.Bargain;
import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.RequestStatus;

import java.util.ArrayList;
import java.util.UUID;

import static io.angularpay.crypto.common.Constants.SERVICE_CODE;
import static io.angularpay.crypto.util.SequenceGenerator.generateRequestTag;

public class ObjectFactory {

    public static CryptoRequest cryptoRequestWithDefaults() {
        return CryptoRequest.builder()
                .reference(UUID.randomUUID().toString())
                .serviceCode(SERVICE_CODE)
                .status(RequestStatus.ACTIVE)
                .requestTag(generateRequestTag())
                .investors(new ArrayList<>())
                .bargain(Bargain.builder()
                        .offers(new ArrayList<>())
                        .build())
                .build();
    }
}