package manager.domain.service;

import manager.api.dto.requests.RequestStatusDto;
import manager.domain.model.entity.Request;
import manager.domain.model.entity.RequestStatus.Status;
import manager.domain.model.entity.RequestStatus;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static manager.domain.model.entity.RequestStatus.Status.IN_PROGRESS;

@Service
public class DomainCrackHashService {
    private static final CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();

    @PostConstruct
    private void init() {
        String alphabetString = "0123456789abcdefghijklmnopqrstuvwxyz";
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public static String getFormattedTime(long time) {
        String resultTime = "";
        int seconds = (int) (time / 1000);
        int milliseconds = (int) (time - seconds * 1000);

        if (seconds > 0) {
            resultTime += seconds + " sec ";
        }

        return resultTime + milliseconds + " millis ";
    }

    public static RequestStatus changeStatus(Status status, RequestStatus requestStatus) {
        requestStatus.setStatus(status);
        return requestStatus;
    }

    public static RequestStatus getRequestStatus(String requestId) {
        return new RequestStatus(
                requestId,
                IN_PROGRESS,
                new ArrayList<>(),
                new Date(System.currentTimeMillis())
        );
    }

    public static Request getRequest(CrackHashManagerRequest crackHashManagerRequest) {
        return new Request(
                crackHashManagerRequest.getRequestId(),
                crackHashManagerRequest
        );
    }


    public static CrackHashManagerRequest buildCrackHashManagerRequest(String id, String hash, int maxLength,
                                                                       int partNumber, int partCount) {
        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(id);
        crackHashManagerRequest.setPartNumber(partNumber);
        crackHashManagerRequest.setPartCount(partCount);
        crackHashManagerRequest.setAlphabet(alphabet);

        return crackHashManagerRequest;
    }

    public static RequestStatusDto buildRequestStatusDto(RequestStatus requestStatus) {
        String status = requestStatus.getStatus().toString();
        List<String> result = requestStatus.getResult();
        if (result.isEmpty()) {
            result = null;
        }

        return new RequestStatusDto(status, result);
    }
}

