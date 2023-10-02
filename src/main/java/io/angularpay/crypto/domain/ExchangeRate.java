
package io.angularpay.crypto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @NotEmpty
    private String date;

    @NotEmpty
    private String from;

    @NotEmpty
    private String rate;

    @NotEmpty
    private String to;

    @NotNull
    private ExchangeRateType type;
}
