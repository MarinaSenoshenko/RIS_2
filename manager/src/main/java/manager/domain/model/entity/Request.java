package manager.domain.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "request")
public class Request {
    @Id
    private String id;
    private CrackHashManagerRequest request;
}
