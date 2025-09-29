package com.careerit.cbook.controller;

import com.careerit.cbook.dto.ContactDto;
import com.careerit.cbook.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contact Management", description = "APIs for managing contacts with CRUD operations, search, and batch processing")
public class ContactController {

    private final ContactService contactService;

    @Operation(
        summary = "Create a new contact",
        description = "Creates a new contact with the provided information. Mobile number must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Contact created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ContactDto.class),
                examples = @ExampleObject(
                    name = "Created Contact",
                    value = """
                        {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "name": "John Doe",
                          "email": "john.doe@example.com",
                          "mobile": "1234567890",
                          "dob": "1990-01-15",
                          "deleted": false
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "timestamp": "2024-01-01T10:00:00",
                          "status": 400,
                          "error": "Validation Failed",
                          "message": "Invalid input data",
                          "validationErrors": {
                            "mobile": "Mobile must be 10 digits",
                            "email": "Invalid email format"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Contact with this mobile number already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    value = """
                        {
                          "timestamp": "2024-01-01T10:00:00",
                          "status": 409,
                          "error": "Contact Already Exists",
                          "message": "Contact with mobile 1234567890 already exists"
                        }
                        """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<ContactDto> createContact(
        @Parameter(description = "Contact information to create", required = true)
        @Valid @RequestBody ContactDto contactDto) {
        log.info("Creating new contact with mobile: {}", contactDto.getMobile());
        ContactDto createdContact = contactService.createContact(contactDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    @Operation(
        summary = "Create multiple contacts in batch",
        description = "Creates multiple contacts in a single operation. All mobile numbers must be unique within the batch and in the database."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "All contacts created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ContactDto[].class),
                examples = @ExampleObject(
                    name = "Batch Created Contacts",
                    value = """
                        [
                          {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "name": "John Doe",
                            "email": "john.doe@example.com",
                            "mobile": "1234567890",
                            "dob": "1990-01-15",
                            "deleted": false
                          },
                          {
                            "id": "987fcdeb-51a2-43d1-b789-123456789abc",
                            "name": "Jane Smith",
                            "email": "jane.smith@example.com",
                            "mobile": "0987654321",
                            "dob": "1985-05-20",
                            "deleted": false
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "timestamp": "2024-01-01T10:00:00",
                          "status": 400,
                          "error": "Validation Failed",
                          "message": "Invalid input data",
                          "validationErrors": {
                            "0.mobile": "Mobile must be 10 digits",
                            "1.email": "Invalid email format"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Duplicate mobile numbers found in batch or existing in database",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    value = """
                        {
                          "timestamp": "2024-01-01T10:00:00",
                          "status": 409,
                          "error": "Contact Already Exists",
                          "message": "Mobile numbers already exist: 1234567890, 0987654321"
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/batch")
    public ResponseEntity<List<ContactDto>> createContacts(
        @Parameter(description = "List of contacts to create", required = true)
        @Valid @RequestBody List<ContactDto> contactDtos) {
        log.info("Creating {} contacts in batch", contactDtos.size());
        List<ContactDto> createdContacts = contactService.createContacts(contactDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContacts);
    }

    @Operation(
        summary = "Get contact by ID",
        description = "Retrieves a specific contact by its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contact found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ContactDto.class),
                examples = @ExampleObject(
                    name = "Found Contact",
                    value = """
                        {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "name": "John Doe",
                          "email": "john.doe@example.com",
                          "mobile": "1234567890",
                          "dob": "1990-01-15",
                          "deleted": false
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Contact not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found Error",
                    value = """
                        {
                          "timestamp": "2024-01-01T10:00:00",
                          "status": 404,
                          "error": "Contact Not Found",
                          "message": "Contact not found with id: 123e4567-e89b-12d3-a456-426614174000"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(
        @Parameter(description = "Unique identifier of the contact", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) {
        log.info("Retrieving contact with ID: {}", id);
        return contactService.getContactById(id)
                .map(contact -> ResponseEntity.ok(contact))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active contacts
     * GET /api/v1/contacts
     */
    @GetMapping
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        log.info("Retrieving all contacts");
        List<ContactDto> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    /**
     * Update an existing contact
     * PUT /api/v1/contacts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable UUID id, @Valid @RequestBody ContactDto contactDto) {
        log.info("Updating contact with ID: {}", id);
        ContactDto updatedContact = contactService.updateContact(id, contactDto);
        return ResponseEntity.ok(updatedContact);
    }

    /**
     * Soft delete a contact
     * DELETE /api/v1/contacts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable UUID id) {
        log.info("Deleting contact with ID: {}", id);
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Search contacts",
        description = "Searches contacts by name, email, or mobile number. The search is case-insensitive and supports partial matches."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ContactDto[].class),
                examples = @ExampleObject(
                    name = "Search Results",
                    value = """
                        [
                          {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "name": "John Doe",
                            "email": "john.doe@example.com",
                            "mobile": "1234567890",
                            "dob": "1990-01-15",
                            "deleted": false
                          },
                          {
                            "id": "987fcdeb-51a2-43d1-b789-123456789abc",
                            "name": "Johnny Smith",
                            "email": "johnny.smith@example.com",
                            "mobile": "0987654321",
                            "dob": "1985-05-20",
                            "deleted": false
                          }
                        ]
                        """
                )
            )
        )
    })
    @GetMapping("/search")
    public ResponseEntity<List<ContactDto>> searchContacts(
        @Parameter(description = "Search term to match against name, email, or mobile", required = true, example = "john")
        @RequestParam String q) {
        log.info("Searching contacts with term: {}", q);
        List<ContactDto> contacts = contactService.searchContacts(q);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Health check endpoint
     * GET /api/v1/contacts/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Contact Service is running");
    }
}
