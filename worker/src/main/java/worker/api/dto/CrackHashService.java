package worker.api.dto;

import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.RequestPercentDto;

public interface CrackHashService {
   RequestPercentDto getPercentOfCompletion(String requestId);
   CrackHashWorkerResponse crackCode(ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest request);
}
