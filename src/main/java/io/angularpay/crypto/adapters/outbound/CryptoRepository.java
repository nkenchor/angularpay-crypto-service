package io.angularpay.crypto.adapters.outbound;

import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends MongoRepository<CryptoRequest, String> {

    Optional<CryptoRequest> findByReference(String reference);
    Page<CryptoRequest> findAll(Pageable pageable);
    Page<CryptoRequest> findByStatusIn(Pageable pageable, List<RequestStatus> statuses);
    Page<CryptoRequest> findAByInvesteeUserReference(Pageable pageable, String userReference);
    long countByStatus(RequestStatus status);
}
