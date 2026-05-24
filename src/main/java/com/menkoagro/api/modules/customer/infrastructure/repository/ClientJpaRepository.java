package com.menkoagro.api.modules.customer.infrastructure.repository;

import com.menkoagro.api.modules.customer.domain.entity.Client;
import com.menkoagro.api.modules.customer.domain.repository.ClientRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<Client, UUID>, ClientRepository {

    @Query("""
            SELECT c FROM Client c
            WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%'))
               OR c.telephone LIKE CONCAT('%', :query, '%')
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY c.nom ASC
            """)
    List<Client> searchByNomOrTelephoneOrEmail(@Param("query") String query);
}
