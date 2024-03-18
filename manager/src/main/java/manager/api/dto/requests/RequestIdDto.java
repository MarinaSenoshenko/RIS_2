package manager.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class RequestIdDto {
    @JsonProperty(value = "requestId")
    @Nullable
    private String requestId;
}
