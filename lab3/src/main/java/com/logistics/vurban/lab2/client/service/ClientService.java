package com.logistics.vurban.lab2.client.service;

import com.logistics.vurban.lab2.client.dto.ClientRequestDTO;
import com.logistics.vurban.lab2.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab2.client.model.Client;
import com.logistics.vurban.lab2.client.repository.ClientRepository;
import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientResponseDTO createClient(ClientRequestDTO request) {
        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        Client savedClient = clientRepository.save(client);
        return mapToResponse(savedClient);
    }

    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable).map(this::mapToResponse);
    }

    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return mapToResponse(client);
    }

    private ClientResponseDTO mapToResponse(Client client) {
        ClientResponseDTO response = new ClientResponseDTO();
        response.setId(client.getId());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setEmail(client.getEmail());
        response.setPhone(client.getPhone());
        return response;
    }

    public ClientResponseDTO updateClient(Long id, ClientRequestDTO request) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        existingClient.setFirstName(request.getFirstName());
        existingClient.setLastName(request.getLastName());
        existingClient.setEmail(request.getEmail());
        existingClient.setPhone(request.getPhone());

        Client updatedClient = clientRepository.save(existingClient);
        return mapToResponse(updatedClient);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}