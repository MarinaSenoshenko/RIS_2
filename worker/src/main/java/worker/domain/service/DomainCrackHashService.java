package worker.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import ru.nsu.ccfit.schema.percent_of_completion_response.PercentResponse;


@Service
@Slf4j
public class DomainCrackHashService {

    public static double getPercentOfCompletion(int allCombinationsNumber, int currentWordNumber) {
        return (allCombinationsNumber == 0) ? 0 : 100.0 * currentWordNumber / allCombinationsNumber;
    }

    public static double getCombinationsNumber(int length, int alphabetSize) {
        return Math.pow(alphabetSize, length);
    }

    private static CrackHashWorkerResponse.Answers buildAnswers(List<String> answers) {
        CrackHashWorkerResponse.Answers newAnswers = new CrackHashWorkerResponse.Answers();
        newAnswers.getWords().addAll(answers);

        return newAnswers;
    }

    public static PercentResponse buildResponse(String requestId, String curRequestId,
                                                int allCombinationsNumber, int currentWordNumber, int partNumber) {
        PercentResponse percentResponse = new PercentResponse();
        percentResponse.setRequestId(requestId);
        percentResponse.setPartNumber(partNumber);
        percentResponse.setPercentOfCompletion(
                ((Objects.equals(curRequestId, requestId)) ?
                        getPercentOfCompletion(allCombinationsNumber, currentWordNumber) : 0)
        );

        return percentResponse;
    }

    public static CrackHashWorkerResponse buildResponse(String requestId, int partNumber, List<String> answers) {
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(requestId);
        response.setPartNumber(partNumber);
        response.setAnswers(buildAnswers(answers));

        return response;
    }

    public static MessageDigest getMD5() {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return md5;
    }
}

