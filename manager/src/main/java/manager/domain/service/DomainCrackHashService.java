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
    private static final int MILL_TO_SEC = 1000;
    public static final int MILL_TO_MIN = 60000;

    @PostConstruct
    private void init() {
        String alphabetString = "0123456789abcdefghijklmnopqrstuvwxyz";
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public static String getFormattedTime(long time) {
        int minutes = (int) (time / MILL_TO_MIN);
        int seconds = (int) ((time - minutes * MILL_TO_MIN) / MILL_TO_SEC);
        int milliseconds = (int)(time - minutes * MILL_TO_MIN - seconds * MILL_TO_SEC);

        return minutes + " min " + seconds + " sec " + milliseconds + " mill ";
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
                0.0,
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

    public static RequestStatusDto buildRequestStatusDto(RequestStatus requestStatus, Double percentOfCompletion) {
        String status = requestStatus.getStatus().toString();
        List<String> result = requestStatus.getResult();

        return new RequestStatusDto(status, percentOfCompletion + "%", (result.isEmpty()) ? null : result);
    }
}
