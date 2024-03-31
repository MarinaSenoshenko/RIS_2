package worker.service;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.PercentResponse;
import worker.api.dto.CrackHashService;
import worker.domain.service.DomainCrackHashService;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CrackHashServiceImpl implements CrackHashService {
    private int allCombinationsNumber = 0;
    private int currentWordNumber = 0;
    private String curRequestId;

    @Override
    public PercentResponse getPercentOfCompletion(String requestId) {
        return DomainCrackHashService.buildResponse(requestId, curRequestId, allCombinationsNumber, currentWordNumber);
    }

    @Override
    public CrackHashWorkerResponse crackCode(CrackHashManagerRequest request) {
        log.info("progress task: {}", request);

        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(request.getAlphabet().getSymbols());
        List<String> answers = new ArrayList<>();

        int maxLength = request.getMaxLength();
        int partNumber = request.getPartNumber();
        int partCount = request.getPartCount();
        int alphabetSize = request.getAlphabet().getSymbols().size();

        allCombinationsNumber = 0;
        currentWordNumber = 0;
        for (int i = 1; i <= maxLength; i++) {
            allCombinationsNumber += (long) DomainCrackHashService.getCombinationsNumber(i, alphabetSize);
        }
        log.info("allCombinationsNumber: {}", allCombinationsNumber);

        curRequestId = request.getRequestId();
        for (int i = 1; i <= maxLength; i++) {
            long combinationsNumber = (long) DomainCrackHashService.getCombinationsNumber(i, alphabetSize);
            long partSize = combinationsNumber / partCount;
            long offset = combinationsNumber % partCount;

            long startIndex = partSize * partNumber + ((partNumber < offset) ? partNumber : offset);
            long stopIndex = startIndex + partSize - ((partNumber < offset) ? 0 : 1);

            for (ICombinatoricsVector<String> combination : CombinatoricsFactory
                    .createPermutationWithRepetitionGenerator(vector, i).generateObjectsRange(startIndex, stopIndex)) {
                currentWordNumber++;
                String currentWord = String.join("", combination.getVector());
                String hash = (new HexBinaryAdapter()).marshal(DomainCrackHashService.getMD5().digest(currentWord.getBytes()));

                if (request.getHash().equalsIgnoreCase(hash)) {
                    answers.add(currentWord);
                    log.info("added answer : {}", currentWord);
                    break;
                }
            }
        }
        log.info("currentWordNumber: {}", currentWordNumber);
        log.info("end processing task : {}", request.getRequestId());
        return DomainCrackHashService.buildResponse(request.getRequestId(), partNumber, answers);
    }
}
