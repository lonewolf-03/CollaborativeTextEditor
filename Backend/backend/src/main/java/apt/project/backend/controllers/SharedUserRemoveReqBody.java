package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class SharedUserRemoveReqBody {
    private String email;
    private Long docId;
}
