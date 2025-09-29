package com.careerit.cbook.repo;

import com.careerit.cbook.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    
    @Query("SELECT c FROM Contact c WHERE c.deleted = false")
    List<Contact> findActiveContacts();
    
    @Query("SELECT c FROM Contact c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.mobile LIKE CONCAT('%', :searchTerm, '%')) AND " +
           "c.deleted = false")
    List<Contact> searchContacts(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.email = :email AND c.deleted = false")
    boolean existsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.mobile = :mobile AND c.deleted = false")
    boolean existsByMobile(@Param("mobile") String mobile);
}
