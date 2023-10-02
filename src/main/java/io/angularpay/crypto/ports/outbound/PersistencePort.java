package io.angularpay.crypto.ports.outbound;

import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    CryptoRequest createRequest(CryptoRequest request);
    CryptoRequest updateRequest(CryptoRequest request);
    Optional<CryptoRequest> findRequestByReference(String reference);
    Page<CryptoRequest> listRequests(Pageable pageable);
    Page<CryptoRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses);
    Page<CryptoRequest> findByInvesteeUserReference(Pageable pageable, String userReference);
    long getCountByRequestStatus(RequestStatus status);
    long getTotalCount();
}
