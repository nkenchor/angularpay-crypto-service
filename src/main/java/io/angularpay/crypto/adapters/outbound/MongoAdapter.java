package io.angularpay.crypto.adapters.outbound;

import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.RequestStatus;
import io.angularpay.crypto.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final CryptoRepository cryptoRepository;

    @Override
    public CryptoRequest createRequest(CryptoRequest request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return cryptoRepository.save(request);
    }

    @Override
    public CryptoRequest updateRequest(CryptoRequest request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return cryptoRepository.save(request);
    }

    @Override
    public Optional<CryptoRequest> findRequestByReference(String reference) {
        return cryptoRepository.findByReference(reference);
    }

    @Override
    public Page<CryptoRequest> listRequests(Pageable pageable) {
        return cryptoRepository.findAll(pageable);
    }

    @Override
    public Page<CryptoRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses) {
        return cryptoRepository.findByStatusIn(pageable, statuses);
    }

    @Override
    public Page<CryptoRequest> findByInvesteeUserReference(Pageable pageable, String userReference) {
        return cryptoRepository.findAByInvesteeUserReference(pageable, userReference);
    }

    @Override
    public long getCountByRequestStatus(RequestStatus status) {
        return cryptoRepository.countByStatus(status);
    }

    @Override
    public long getTotalCount() {
        return cryptoRepository.count();
    }
}
