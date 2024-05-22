package apt.project.backend.controllers;

import lombok.Data;

@Data
public class UpdatePermissionReqBody {
    private Long docId;
    private String email;
    private String newPermission;
};
