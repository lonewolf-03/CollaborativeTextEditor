package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class GetPermissionsBody {
    private Long userId;
    private Long docId;
}
