package manager.api.dto;

import manager.api.dto.requests.RequestStatusDto;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.PercentResponse;

public interface CrackHashService {
    String crackHash(String hash, int maxLength);
    RequestStatusDto getStatus(String requestId) throws InterruptedException;
    void workerCallbackHandler(CrackHashWorkerResponse crackHashWorkerResponse);
    void workerCallbackHandler(PercentResponse requestPercentDto);
}
