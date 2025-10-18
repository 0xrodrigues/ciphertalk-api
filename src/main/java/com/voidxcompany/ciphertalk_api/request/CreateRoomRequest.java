package com.voidxcompany.ciphertalk_api.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    
    @NotBlank(message = "Room name is required")
    @Size(min = 3, max = 100, message = "Room name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Host username is required")
    private String hostUsername;
    
    @Min(value = 2, message = "Max users must be at least 2")
    @Max(value = 500, message = "Max users must not exceed 500")
    private Integer maxUsers;
    
    private String visibility; // PUBLIC or PRIVATE
    
    private List<String> tags;
}
