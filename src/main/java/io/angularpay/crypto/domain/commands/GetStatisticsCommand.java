package io.angularpay.crypto.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.crypto.adapters.outbound.MongoAdapter;
import io.angularpay.crypto.domain.RequestStatus;
import io.angularpay.crypto.domain.Role;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.models.GetStatisticsCommandRequest;
import io.angularpay.crypto.models.Statistics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GetStatisticsCommand extends AbstractCommand<GetStatisticsCommandRequest, List<Statistics>> {

    private final MongoAdapter mongoAdapter;

    public GetStatisticsCommand(ObjectMapper mapper, MongoAdapter mongoAdapter) {
        super("GetStatisticsCommand", mapper);
        this.mongoAdapter = mongoAdapter;
    }

    @Override
    protected String getResourceOwner(GetStatisticsCommandRequest request) {
        return ""; // TODO do this for non-user resources
    }

    @Override
    protected List<Statistics> handle(GetStatisticsCommandRequest request) {
        List<Statistics> statistics = new ArrayList<>();

        long total = this.mongoAdapter.getTotalCount();
        statistics.add(Statistics.builder()
                .name("Total")
                .value(String.valueOf(total))
                .build());

        long active = this.mongoAdapter.getCountByRequestStatus(RequestStatus.ACTIVE);
        statistics.add(Statistics.builder()
                .name("Active")
                .value(String.valueOf(active))
                .build());

        long inactive = this.mongoAdapter.getCountByRequestStatus(RequestStatus.INACTIVE);
        statistics.add(Statistics.builder()
                .name("Inactive")
                .value(String.valueOf(inactive))
                .build());

        long completed = this.mongoAdapter.getCountByRequestStatus(RequestStatus.COMPLETED);
        statistics.add(Statistics.builder()
                .name("Completed")
                .value(String.valueOf(completed))
                .build());

        long cancelled = this.mongoAdapter.getCountByRequestStatus(RequestStatus.CANCELLED);
        statistics.add(Statistics.builder()
                .name("Cancelled")
                .value(String.valueOf(cancelled))
                .build());

        return statistics;
    }

    @Override
    protected List<ErrorObject> validate(GetStatisticsCommandRequest request) {
        return Collections.emptyList();
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_PLATFORM_ADMIN, Role.ROLE_PLATFORM_USER);
    }
}
