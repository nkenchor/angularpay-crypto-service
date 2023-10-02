package io.angularpay.crypto.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
