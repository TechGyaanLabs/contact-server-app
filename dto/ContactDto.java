package com.careerit.cbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Contact information")
public class ContactDto {
    
    @Schema(description = "Unique identifier of the contact", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Full name of the contact", example = "John Doe", required = true)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Schema(description = "Email address of the contact", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(description = "Mobile phone number (10 digits)", example = "1234567890", required = true)
    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    private String mobile;
    
    @Schema(description = "Date of birth", example = "1990-01-15", required = true)
    @NotNull(message = "Date of birth is required")
    private LocalDate dob;
    
    @Schema(description = "Soft delete flag", example = "false")
    private boolean deleted;
}
