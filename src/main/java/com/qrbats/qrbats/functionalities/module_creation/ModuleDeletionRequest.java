package com.qrbats.qrbats.functionalities.module_creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDeletionRequest {
    private String moduleCode;
}
