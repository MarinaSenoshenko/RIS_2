package worker.api.dto;

import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

public interface CrackHashService {
   CrackHashWorkerResponse crackCode(ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest request);
}
