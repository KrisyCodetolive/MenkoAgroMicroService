package com.menkoagro.api.modules.customer.domain.repository;

import com.menkoagro.api.modules.customer.domain.entity.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Optional<Client> findById(UUID id);

    List<Client> findAll();

    List<Client> searchByNomOrTelephoneOrEmail(String query);

    boolean existsById(UUID id);

    Client save(Client client);

    void deleteById(UUID id);
}
