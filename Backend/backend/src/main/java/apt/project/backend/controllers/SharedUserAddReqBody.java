package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;

@Setter
@Data
public class SharedUserAddReqBody {
    private String email;
    private Long docId;
    private String docName;
    private String permissions;
}
