package io.angularpay.crypto.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.angularpay.crypto.domain.CryptoRequest;
import io.angularpay.crypto.domain.Investor;
import io.angularpay.crypto.domain.Role;
import io.angularpay.crypto.exceptions.ErrorObject;
import io.angularpay.crypto.models.AccessControl;
import io.angularpay.crypto.models.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.angularpay.crypto.exceptions.ErrorCode.NO_PRINCIPAL;
import static io.angularpay.crypto.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Slf4j
public class Helper {

    public static <T> String writeAsStringOrDefault(ObjectMapper mapper, T source) {
        try {
            return mapper.writeValueAsString(source);
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while writing source parameter as string", exception);
            return "";
        }
    }

    public static AuthenticatedUser fromHeaders(Map<String, String> headers) {
        String clientIp = "";
        String xForwardedFor = headers.get("x-forwarded-for");
        if (StringUtils.hasText(xForwardedFor)) {
            clientIp = xForwardedFor.split(",")[0].trim();
        }
        List<Role> roles = new ArrayList<>();
        String rolesHeader = headers.get("x-angularpay-user-roles");
        if (StringUtils.hasText(rolesHeader)) {
            String[] rolesArr = rolesHeader.split(",");
            roles = Arrays.stream(rolesArr).map(x -> {
                try {
                    return Role.valueOf(x.trim());
                } catch (Exception exception) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return AuthenticatedUser.builder()
                .username(headers.get("x-angularpay-username"))
                .userReference(headers.get("x-angularpay-user-reference"))
                .deviceId(headers.get("x-angularpay-device-id"))
                .correlationId(headers.get("x-angularpay-correlation-id"))
                .clientIp(clientIp)
                .roles(roles)
                .build();
    }

    public static boolean hasPermittedRole(List<Role> requiredRoles, List<Role> authenticatedUserRoles) {
        if(CollectionUtils.isEmpty(requiredRoles)) return false;
        if(CollectionUtils.isEmpty(authenticatedUserRoles)) return false;
        return requiredRoles.stream().anyMatch(authenticatedUserRoles::contains);
    }

    public static HttpStatus resolveStatus(List<ErrorObject> errorObjects) {
        if (CollectionUtils.isEmpty(errorObjects)) {
            // ideally, this method shouldn't be called unless there are actual validation errors
            // this check should be performed by the caller
            // In this case the AbstractCommand already performs this check
            // Therefore, this block will never be executed
            return HttpStatus.OK;
        }
        return errorObjects.stream().anyMatch(x -> x.getCode() == NO_PRINCIPAL) ? HttpStatus.INTERNAL_SERVER_ERROR :
                errorObjects.stream().anyMatch(x -> x.getCode() == REQUEST_NOT_FOUND) ? HttpStatus.NOT_FOUND :
                        HttpStatus.BAD_REQUEST;
    }

    public static void addToMappedDiagnosticContext(String name, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(name, value);
        }
    }

    public static void addToMappedDiagnosticContextOrRandomUUID(String name, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(name, value);
        } else {
            MDC.put(name, UUID.randomUUID().toString());
        }
    }

    public static String maskUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return username;
        }
        String[] split = username.split("@");
        if (split.length > 1) {
            return split[0].charAt(0) + "*****" + split[0].charAt(split[0].length() - 1) + "@" + split[1];
        } else {
            return username.substring(0, 3) + "*****" + username.substring(username.length() - 3);
        }
    }

    public static String maskUserReference(String userReference) {
        if (!StringUtils.hasText(userReference)) {
            return userReference;
        }
        String[] split = userReference.split("@");
        if (split.length > 1) {
            return split[0].charAt(0) + "*****" + split[0].charAt(split[0].length() - 1) + "@" + split[1];
        } else {
            return userReference;
        }
    }

    public static <T extends AccessControl> String maskAuthenticatedUser(ObjectMapper mapper, T raw) {
        JsonNode node = mapper.convertValue(raw, JsonNode.class);
        JsonNode authenticatedUser = node.get("authenticatedUser");
        ((ObjectNode) authenticatedUser).put("username", maskUsername(raw.getAuthenticatedUser().getUsername()));
        ((ObjectNode) authenticatedUser).put("userReference", maskUserReference(raw.getAuthenticatedUser().getUserReference()));
        return node.toString();
    }

    public static List<String> getAllPartiesExceptActor(CryptoRequest request, String itemReference) {
        List<String> investors = request.getInvestors().stream()
                .filter(x -> !x.getReference().equalsIgnoreCase(itemReference))
                .map(Investor::getUserReference)
                .collect(Collectors.toList());

        List<String> audience = new ArrayList<>();
        audience.add(request.getInvestee().getUserReference());
        audience.addAll(investors);

        return audience;
    }

    public static List<String> getAllPartiesExceptInvestee(CryptoRequest request) {
        return request.getInvestors().stream()
                .map(Investor::getUserReference)
                .collect(Collectors.toList());
    }

    public static List<String> getAllParties(CryptoRequest request) {
        List<String> investors = request.getInvestors().stream()
                .map(Investor::getUserReference)
                .collect(Collectors.toList());

        List<String> audience = new ArrayList<>();
        audience.add(request.getInvestee().getUserReference());
        audience.addAll(investors);

        return audience;
    }

    public static String commaSeparated(List<Role> authorities) {
        if(CollectionUtils.isEmpty(authorities)) return "";
        return authorities.stream().map(Enum::name).collect(Collectors.joining(","));
    }
}
