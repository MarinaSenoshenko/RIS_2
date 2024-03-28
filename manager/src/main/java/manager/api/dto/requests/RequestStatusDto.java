package manager.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class RequestStatusDto {
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "percentOfCompletion")
    private String percentOfCompletion;
    @JsonProperty(value = "result")
    private List<String> result;
}
