package manager.api.dto;

import manager.api.dto.requests.RequestStatusDto;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

public interface CrackHashService {
    String crackHash(String hash, int maxLength);
    RequestStatusDto getStatus(String requestId);
    void workerCallbackHandler(CrackHashWorkerResponse crackHashWorkerResponse);
}
