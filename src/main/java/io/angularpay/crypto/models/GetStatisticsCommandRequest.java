package io.angularpay.crypto.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class GetStatisticsCommandRequest extends AccessControl {

    GetStatisticsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
