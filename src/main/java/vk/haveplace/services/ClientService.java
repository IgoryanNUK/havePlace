package vk.haveplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.ClientRepository;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.services.mappers.ClientMapper;
import vk.haveplace.services.objects.requests.ClientRequest;

import java.util.Optional;

@Service
public class ClientService {
    public ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientEntity getEntityByRequest(ClientRequest clientRequest) {
        Optional<ClientEntity> opt = clientRepository.findByVkLink(clientRequest.getVkLink());
        ClientEntity entity;

        if (opt.isEmpty()) {
            entity = addNewClient(clientRequest);
        } else {
            entity = opt.get();
            if (!(entity.getPhone().equals(clientRequest.getPhone())
                && entity.getName().equals(clientRequest.getName())) &&
                clientRequest.getName() != null && clientRequest.getPhone() != null) {
                entity.setName(clientRequest.getName());
                entity.setPhone(clientRequest.getPhone());
                entity = clientRepository.saveAndFlush(entity);
            }
        }
        return entity;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientEntity getEntityByVkLink(String vkLink) {
        return clientRepository.findByVkLink(vkLink).orElseThrow(() -> null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientEntity addNewClient(ClientRequest request) {
        ClientEntity newClient = ClientMapper.getEntityFromRequest(request);

        return clientRepository.saveAndFlush(newClient);
    }
}
