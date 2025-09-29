package com.careerit.cbook.service;

import com.careerit.cbook.dto.ContactDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Contact management operations
 */
public interface ContactService {
    
    /**
     * Create a new contact
     * @param contactDto Contact data to create
     * @return Created contact DTO
     */
    ContactDto createContact(ContactDto contactDto);
    
    /**
     * Create multiple contacts in batch
     * @param contactDtos List of contact data to create
     * @return List of created contact DTOs
     */
    List<ContactDto> createContacts(List<ContactDto> contactDtos);
    
    /**
     * Get contact by ID
     * @param id Contact ID
     * @return Optional containing contact DTO if found
     */
    Optional<ContactDto> getContactById(UUID id);
    
    /**
     * Get all active (non-deleted) contacts
     * @return List of contact DTOs
     */
    List<ContactDto> getAllContacts();
    
    /**
     * Update an existing contact
     * @param id Contact ID to update
     * @param contactDto Updated contact data
     * @return Updated contact DTO
     */
    ContactDto updateContact(UUID id, ContactDto contactDto);
    
    /**
     * Soft delete a contact (mark as deleted)
     * @param id Contact ID to delete
     */
    void deleteContact(UUID id);
    
    /**
     * Search contacts by name, email, or mobile
     * @param searchTerm Search term to match against name, email, or mobile
     * @return List of matching contact DTOs
     */
    List<ContactDto> searchContacts(String searchTerm);
}