package worker.service;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.RequestPercentDto;
import worker.api.dto.CrackHashService;
import worker.domain.service.DomainCrackHashService;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CrackHashServiceImpl implements CrackHashService {
    private int allCombinationsNumber = 0;
    private int currentWordNumber = 1;
    private String curRequestId;

    @Override
    public RequestPercentDto getPercentOfCompletion(String requestId) {
        RequestPercentDto requestPercentDto = new RequestPercentDto();
        requestPercentDto.setPercentOfCompletion(
                ((Objects.equals(curRequestId, requestId)) ?
                DomainCrackHashService.getPercentOfCompletion(allCombinationsNumber, currentWordNumber) : 0));
        requestPercentDto.setRequestId(requestId);
        return requestPercentDto;
    }

    @Override
    public CrackHashWorkerResponse crackCode(CrackHashManagerRequest request) {
        log.info("progress task: {}", request);
        curRequestId = request.getRequestId();
        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(request.getAlphabet().getSymbols());
        List<String> answers = new ArrayList<>();

        int maxLength = request.getMaxLength();
        int partNumber = request.getPartNumber();
        int partCount = request.getPartCount();
        int alphabetSize = request.getAlphabet().getSymbols().size();

        for (int i = 1; i <= maxLength; i++) {
            allCombinationsNumber += (long) DomainCrackHashService.getCombinationsNumber(i, alphabetSize) / partCount;
        }

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

            log.info("percent = {} currentWordNumber = {}, allCombinationsNumber = {}",
                    getPercentOfCompletion(curRequestId), currentWordNumber, allCombinationsNumber);
        }
        log.info("end processing task : {}", request.getRequestId());
        return DomainCrackHashService.buildResponse(request.getRequestId(), partNumber, answers);
    }
}
