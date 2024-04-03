package worker.api.dto;

import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.PercentResponse;

public interface CrackHashService {
   PercentResponse getPercentOfCompletion(String requestId) throws InterruptedException;
   CrackHashWorkerResponse crackCode(ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest request);
}
