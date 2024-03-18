package manager.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
public class RequestCrackDto {
    @JsonProperty(value = "hash", required = true)
    private String hash;
    @JsonProperty(value = "maxLength", required = true)
    private int maxLength;
}
