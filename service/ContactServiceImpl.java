package com.careerit.cbook.service;

import com.careerit.cbook.domain.Contact;
import com.careerit.cbook.dto.ContactDto;
import com.careerit.cbook.exception.ContactAlreadyExistsException;
import com.careerit.cbook.exception.ContactNotFoundException;
import com.careerit.cbook.repo.ContactRepository;
import com.careerit.cbook.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    // Create a new contact
    @Override
    public ContactDto createContact(ContactDto contactDto) {
        log.info("Creating new contact with mobile: {}", contactDto.getMobile());
        
        // Check if mobile already exists
        if (contactRepository.existsByMobile(contactDto.getMobile())) {
            log.warn("Contact creation failed - mobile {} already exists", contactDto.getMobile());
            throw new ContactAlreadyExistsException("mobile", contactDto.getMobile());
        }

        Contact contact = ObjectMapperUtil.convert(contactDto, Contact.class);
        contact.setDeleted(false); // Ensure new contacts are not deleted
        Contact savedContact = contactRepository.save(contact);
        
        log.info("Successfully created contact with ID: {} and mobile: {}", savedContact.getId(), savedContact.getMobile());
        return ObjectMapperUtil.convert(savedContact, ContactDto.class);
    }

    // Create multiple contacts in batch
    @Override
    public List<ContactDto> createContacts(List<ContactDto> contactDtos) {
        log.info("Creating {} contacts in batch", contactDtos.size());
        
        // Validate that all contacts have unique mobile numbers
        List<String> mobiles = contactDtos.stream()
                .map(ContactDto::getMobile)
                .toList();
        
        // Check for duplicates within the batch
        long uniqueMobiles = mobiles.stream().distinct().count();
        if (uniqueMobiles != mobiles.size()) {
            log.warn("Batch creation failed - duplicate mobile numbers found in the batch");
            throw new ContactAlreadyExistsException("Duplicate mobile numbers found in the batch");
        }
        
        // Check if any mobile numbers already exist in database
        List<String> existingMobiles = mobiles.stream()
                .filter(contactRepository::existsByMobile)
                .toList();
        
        if (!existingMobiles.isEmpty()) {
            log.warn("Batch creation failed - mobile numbers already exist: {}", existingMobiles);
            throw new ContactAlreadyExistsException("Mobile numbers already exist: " + String.join(", ", existingMobiles));
        }
        
        // Convert DTOs to entities
        List<Contact> contacts = contactDtos.stream()
                .map(dto -> {
                    Contact contact = ObjectMapperUtil.convert(dto, Contact.class);
                    contact.setDeleted(false);
                    return contact;
                })
                .toList();
        
        // Save all contacts
        List<Contact> savedContacts = contactRepository.saveAll(contacts);
        
        log.info("Successfully created {} contacts in batch", savedContacts.size());
        
        // Convert back to DTOs
        return savedContacts.stream()
                .map(contact -> ObjectMapperUtil.convert(contact, ContactDto.class))
                .toList();
    }

    // Get contact by ID
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> getContactById(UUID id) {
        log.debug("Retrieving contact by ID: {}", id);
        
        Optional<ContactDto> result = contactRepository.findById(id)
                .filter(contact -> !contact.isDeleted())
                .map(contact -> ObjectMapperUtil.convert(contact, ContactDto.class));
        
        if (result.isPresent()) {
            log.debug("Contact found with ID: {}", id);
        } else {
            log.debug("Contact not found or deleted with ID: {}", id);
        }
        
        return result;
    }

    // Get all non-deleted contacts
    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> getAllContacts() {
        log.debug("Retrieving all active contacts");
        
        List<ContactDto> contacts = contactRepository.findActiveContacts()
                .stream()
                .map(contact -> ObjectMapperUtil.convert(contact, ContactDto.class))
                .collect(Collectors.toList());
        
        log.info("Retrieved {} active contacts", contacts.size());
        return contacts;
    }

    // Update contact
    @Override
    public ContactDto updateContact(UUID id, ContactDto contactDto) {
        log.info("Updating contact with ID: {} and mobile: {}", id, contactDto.getMobile());
        
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contact not found with ID: {}", id);
                    return new ContactNotFoundException(id);
                });

        if (existingContact.isDeleted()) {
            log.warn("Attempted to update deleted contact with ID: {}", id);
            throw new ContactNotFoundException("Cannot update deleted contact with id: " + id);
        }

        // Check if mobile is being changed and if new mobile already exists
        if (!existingContact.getMobile().equals(contactDto.getMobile()) && 
            contactRepository.existsByMobile(contactDto.getMobile())) {
            log.warn("Contact update failed - mobile {} already exists", contactDto.getMobile());
            throw new ContactAlreadyExistsException("mobile", contactDto.getMobile());
        }
        
        // Update fields
        existingContact.setName(contactDto.getName());
        existingContact.setEmail(contactDto.getEmail());
        existingContact.setMobile(contactDto.getMobile());
        existingContact.setDob(contactDto.getDob());
        Contact updatedContact = contactRepository.save(existingContact);
        
        log.info("Successfully updated contact with ID: {}", id);
        return ObjectMapperUtil.convert(updatedContact, ContactDto.class);
    }

    // Soft delete contact
    @Override
    public void deleteContact(UUID id) {
        log.info("Soft deleting contact with ID: {}", id);
        
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contact not found with ID: {}", id);
                    return new ContactNotFoundException(id);
                });
        
        if (contact.isDeleted()) {
            log.warn("Attempted to delete already deleted contact with ID: {}", id);
            throw new ContactNotFoundException("Contact already deleted with id: " + id);
        }
        
        contact.setDeleted(true);
        contactRepository.save(contact);
        
        log.info("Successfully soft deleted contact with ID: {}", id);
    }
    
    // Search contacts by name, email, or mobile
    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> searchContacts(String searchTerm) {
        log.debug("Searching contacts with term: {}", searchTerm);
        
        List<ContactDto> results = contactRepository.searchContacts(searchTerm)
                .stream()
                .map(contact -> ObjectMapperUtil.convert(contact, ContactDto.class))
                .collect(Collectors.toList());
        
        log.info("Search completed for term '{}' - found {} contacts", searchTerm, results.size());
        return results;
    }

}
