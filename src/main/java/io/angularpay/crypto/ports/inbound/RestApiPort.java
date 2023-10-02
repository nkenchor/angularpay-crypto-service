package io.angularpay.crypto.ports.inbound;

import io.angularpay.crypto.domain.*;
import io.angularpay.crypto.models.*;

import java.util.List;
import java.util.Map;

public interface RestApiPort {
    GenericReferenceResponse createScheduledRequest(String schedule, CreateRequest request, Map<String, String> headers);
    GenericReferenceResponse create(CreateRequest request, Map<String, String> headers);
    void updateAmount(String requestReference, Amount amount, Map<String, String> headers);
    void updateExchangeRate(String requestReference, ExchangeRate exchangeRate, Map<String, String> headers);
    GenericReferenceResponse addInvestor(String requestReference, AddInvestorApiModel addInvestorApiModel, Map<String, String> headers);
    void removeInvestor(String requestReference, String investmentReference, Map<String, String> headers);
    void removeInvestorTTL(String requestReference, String investmentReference, Map<String, String> headers);
    void removeInvestorPlatform(String requestReference, String investmentReference, Map<String, String> headers);
    GenericReferenceResponse addBargain(String requestReference, AddBargainApiModel addBargainApiModel, Map<String, String> headers);
    void acceptBargain(String requestReference, String bargainReference, Map<String, String> headers);
    void rejectBargain(String requestReference, String bargainReference, Map<String, String> headers);
    void deleteBargain(String requestReference, String bargainReference, Map<String, String> headers);
    void updateInvestmentAmount(String requestReference, String investmentReference, UpdateInvestmentApiModel updateInvestmentApiModel, Map<String, String> headers);
    GenericReferenceResponse makePayment(String requestReference, String investmentReference, PaymentRequest paymentRequest, Map<String, String> headers);
    void updateRequestStatus(String requestReference, RequestStatusModel status, Map<String, String> headers);
    CryptoRequest getRequestByReference(String requestReference, Map<String, String> headers);
    List<CryptoRequest> getNewsfeedModel(int page, Map<String, String> headers);
    List<UserRequestModel> getUserRequests(int page, Map<String, String> headers);
    List<UserInvestmentModel> getUserInvestments(int page, Map<String, String> headers);
    List<CryptoRequest> getNewsfeedByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<CryptoRequest> getRequestListByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<CryptoRequest> getRequestList(int page, Map<String, String> headers);
    List<Statistics> getStatistics(Map<String, String> headers);
}
