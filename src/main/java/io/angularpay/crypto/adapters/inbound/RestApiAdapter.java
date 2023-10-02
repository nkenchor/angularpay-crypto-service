package io.angularpay.crypto.adapters.inbound;

import io.angularpay.crypto.configurations.AngularPayConfiguration;
import io.angularpay.crypto.domain.*;
import io.angularpay.crypto.domain.commands.*;
import io.angularpay.crypto.models.*;
import io.angularpay.crypto.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.angularpay.crypto.domain.DeletedBy.*;
import static io.angularpay.crypto.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/crypto/requests")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final AngularPayConfiguration configuration;

    private final CreateRequestCommand createRequestCommand;
    private final UpdateAmountCommand updateAmountCommand;
    private final UpdateExchangeRateCommand updateExchangeRateCommand;
    private final AddInvestorCommand addInvestorCommand;
    private final RemoveInvestorCommand removeInvestorCommand;
    private final AddBargainCommand addBargainCommand;
    private final AcceptBargainCommand acceptBargainCommand;
    private final RejectBargainCommand rejectBargainCommand;
    private final DeleteBargainCommand deleteBargainCommand;
    private final UpdateInvestmentAmountCommand updateInvestmentAmountCommand;
    private final MakePaymentCommand makePaymentCommand;
    private final UpdateRequestStatusCommand updateRequestStatusCommand;
    private final GetRequestByReferenceCommand getRequestByReferenceCommand;
    private final GetNewsfeedCommand getNewsfeedCommand;
    private final GetUserRequestsCommand getUserRequestsCommand;
    private final GetUserInvestmentsCommand getUserInvestmentsCommand;
    private final GetNewsfeedByStatusCommand getNewsfeedByStatusCommand;
    private final GetRequestListByStatusCommand getRequestListByStatusCommand;
    private final GetRequestListCommand getRequestListCommand;
    private final ScheduledRequestCommand scheduledRequestCommand;
    private final GetStatisticsCommand getStatisticsCommand;

    @PostMapping("/schedule/{schedule}")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse createScheduledRequest(
            @PathVariable String schedule,
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        ScheduledRequestCommandRequest scheduledRequestCommandRequest = ScheduledRequestCommandRequest.builder()
                .runAt(schedule)
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return scheduledRequestCommand.execute(scheduledRequestCommandRequest);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse create(
            @RequestBody CreateRequest request,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        CreateRequestCommandRequest createRequestCommandRequest = CreateRequestCommandRequest.builder()
                .createRequest(request)
                .authenticatedUser(authenticatedUser)
                .build();
        return createRequestCommand.execute(createRequestCommandRequest);
    }

    @PutMapping("/{requestReference}/amount")
    @Override
    public void updateAmount(
            @PathVariable String requestReference,
            @RequestBody Amount amount,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateAmountCommandRequest updateAmountCommandRequest = UpdateAmountCommandRequest.builder()
                .requestReference(requestReference)
                .amount(amount)
                .authenticatedUser(authenticatedUser)
                .build();
        updateAmountCommand.execute(updateAmountCommandRequest);
    }

    @PutMapping("{requestReference}/exchange-rate")
    @Override
    public void updateExchangeRate(
            @PathVariable String requestReference,
            @RequestBody ExchangeRate exchangeRate,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateExchangeRateCommandRequest updateExchangeRateCommandRequest = UpdateExchangeRateCommandRequest.builder()
                .requestReference(requestReference)
                .exchangeRate(exchangeRate)
                .authenticatedUser(authenticatedUser)
                .build();
        updateExchangeRateCommand.execute(updateExchangeRateCommandRequest);
    }

    @PostMapping("/{requestReference}/investors")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse addInvestor(
            @PathVariable String requestReference,
            @RequestBody AddInvestorApiModel addInvestorApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AddInvestorCommandRequest addInvestorCommandRequest = AddInvestorCommandRequest.builder()
                .requestReference(requestReference)
                .addInvestorApiModel(addInvestorApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return addInvestorCommand.execute(addInvestorCommandRequest);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}")
    @Override
    public void removeInvestor(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, INVESTOR);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}/ttl")
    @Override
    public void removeInvestorTTL(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, TTL_SERVICE);
    }

    @DeleteMapping("/{requestReference}/investors/{investmentReference}/platform")
    @Override
    public void removeInvestorPlatform(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestHeader Map<String, String> headers) {
        removeInvestor(requestReference, investmentReference, headers, PLATFORM);
    }

    private void removeInvestor(
            String requestReference,
            String investmentReference,
            Map<String, String> headers,
            DeletedBy deletedBy) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RemoveInvestorCommandRequest removeInvestorCommandRequest = RemoveInvestorCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .deletedBy(deletedBy)
                .authenticatedUser(authenticatedUser)
                .build();
        removeInvestorCommand.execute(removeInvestorCommandRequest);
    }

    @PostMapping("{requestReference}/bargains")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse addBargain(
            @PathVariable String requestReference,
            @RequestBody AddBargainApiModel addBargainApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AddBargainCommandRequest addBargainCommandRequest = AddBargainCommandRequest.builder()
                .requestReference(requestReference)
                .addBargainApiModel(addBargainApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return addBargainCommand.execute(addBargainCommandRequest);
    }

    @PutMapping("{requestReference}/bargains/{bargainReference}")
    @Override
    public void acceptBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        AcceptBargainCommandRequest acceptBargainCommandRequest = AcceptBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        acceptBargainCommand.execute(acceptBargainCommandRequest);
    }

    @DeleteMapping("{requestReference}/bargains/{bargainReference}")
    @Override
    public void rejectBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RejectBargainCommandRequest rejectBargainCommandRequest = RejectBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        rejectBargainCommand.execute(rejectBargainCommandRequest);
    }

    @DeleteMapping("{requestReference}/bargains/{bargainReference}/delete")
    @Override
    public void deleteBargain(
            @PathVariable String requestReference,
            @PathVariable String bargainReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        DeleteBargainCommandRequest deleteBargainCommandRequest = DeleteBargainCommandRequest.builder()
                .requestReference(requestReference)
                .bargainReference(bargainReference)
                .authenticatedUser(authenticatedUser)
                .build();
        deleteBargainCommand.execute(deleteBargainCommandRequest);
    }

    @PutMapping("/{requestReference}/investors/{investmentReference}/amount")
    @Override
    public void updateInvestmentAmount(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestBody UpdateInvestmentApiModel updateInvestmentApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateInvestmentAmountCommandRequest updateInvestmentAmountCommandRequest = UpdateInvestmentAmountCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .updateInvestmentApiModel(updateInvestmentApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        updateInvestmentAmountCommand.execute(updateInvestmentAmountCommandRequest);
    }

    @PostMapping("{requestReference}/investors/{investmentReference}/payment")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse makePayment(
            @PathVariable String requestReference,
            @PathVariable String investmentReference,
            @RequestBody PaymentRequest paymentRequest,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        MakePaymentCommandRequest makePaymentCommandRequest = MakePaymentCommandRequest.builder()
                .requestReference(requestReference)
                .investmentReference(investmentReference)
                .paymentRequest(paymentRequest)
                .authenticatedUser(authenticatedUser)
                .build();
        return makePaymentCommand.execute(makePaymentCommandRequest);
    }

    @PutMapping("/{requestReference}/status")
    @Override
    public void updateRequestStatus(
            @PathVariable String requestReference,
            @RequestBody RequestStatusModel status,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        UpdateRequestStatusCommandRequest updateRequestStatusCommandRequest = UpdateRequestStatusCommandRequest.builder()
                .requestReference(requestReference)
                .status(status.getStatus())
                .authenticatedUser(authenticatedUser)
                .build();
        updateRequestStatusCommand.execute(updateRequestStatusCommandRequest);
    }

    @GetMapping("/{requestReference}")
    @Override
    public CryptoRequest getRequestByReference(
            @PathVariable String requestReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetRequestByReferenceCommandRequest getRequestByReferenceCommandRequest = GetRequestByReferenceCommandRequest.builder()
                .requestReference(requestReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return getRequestByReferenceCommand.execute(getRequestByReferenceCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}")
    @Override
    public List<CryptoRequest> getNewsfeedModel(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getNewsfeedCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/list/user-request/page/{page}")
    @Override
    public List<UserRequestModel> getUserRequests(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserRequestsCommandRequest getUserRequestsCommandRequest = GetUserRequestsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserRequestsCommand.execute(getUserRequestsCommandRequest);
    }

    @GetMapping("/list/user-investment/page/{page}")
    @Override
    public List<UserInvestmentModel> getUserInvestments(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetUserInvestmentsCommandRequest getUserInvestmentsCommandRequest = GetUserInvestmentsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getUserInvestmentsCommand.execute(getUserInvestmentsCommandRequest);
    }

    @GetMapping("/list/newsfeed/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<CryptoRequest> getNewsfeedByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getNewsfeedByStatusCommand.execute(genericGetByStatusCommandRequest);
    }

    @GetMapping("/list/page/{page}/filter/statuses/{statuses}")
    @ResponseBody
    @Override
    public List<CryptoRequest> getRequestListByStatus(
            @PathVariable int page,
            @PathVariable List<RequestStatus> statuses,
            Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetByStatusCommandRequest genericGetByStatusCommandRequest = GenericGetByStatusCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .statuses(statuses)
                .build();
        return getRequestListByStatusCommand.execute(genericGetByStatusCommandRequest);
    }

    @GetMapping("/list/page/{page}")
    @ResponseBody
    @Override
    public List<CryptoRequest> getRequestList(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericGetRequestListCommandRequest genericGetRequestListCommandRequest = GenericGetRequestListCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .build();
        return getRequestListCommand.execute(genericGetRequestListCommandRequest);
    }

    @GetMapping("/statistics")
    @ResponseBody
    @Override
    public List<Statistics> getStatistics(@RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetStatisticsCommandRequest getStatisticsCommandRequest = GetStatisticsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return getStatisticsCommand.execute(getStatisticsCommandRequest);
    }
}
