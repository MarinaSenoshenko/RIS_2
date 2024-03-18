package manager.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;


@Data
@AllArgsConstructor
public class RequestStatusDto {
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "result")
    private List<String> result;
}
