package manager.domain.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document("request_status")
public class RequestStatus {
    @Id
    private String requestId;
    private Status status;
    private ArrayList<String> result;
    private Date updated;

    public enum Status {
        IN_PROGRESS,
        READY,
        ERROR
    }
}
