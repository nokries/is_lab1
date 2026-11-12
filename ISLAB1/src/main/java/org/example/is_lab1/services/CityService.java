package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.utils.MagicCityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository repository;
    private final InteractRepository interactRepository;
    private final MagicCityMapper mapper;
    private final WebSocketNotificationService notificationService;


    @Transactional
    public MagicCityDTO create(MagicCityDTO city){
        MagicCity el = mapper.toEntity(city);
        repository.save(el);
        MagicCityDTO created = mapper.toDto(el);
        notificationService.notifyCreated(WebSocketNotificationService.CITY, el.getId(), created);
        return created;
    }

    @Transactional
    public MagicCityDTO modify(long id, MagicCityDTO city){
        Optional<MagicCity> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("City not found with id " + id);
        }
        MagicCity el = found.get();
        mapper.updateEntityFromDto(city, el);
        repository.save(el);
        MagicCityDTO updated = mapper.toDto(el);
        notificationService.notifyUpdated(WebSocketNotificationService.CITY, id, updated);
        return updated;
    }

    @Transactional
    public void delete(long id){
        Optional<MagicCity> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("City not found with id " + id);
        }
        MagicCity city = found.get();

        // Каскадное удаление: вместе с городом удаляются все существа, которые в нём живут
        for (BookCreature resident : interactRepository.findByCreatureLocation(city)) {
            interactRepository.delete(resident);
            notificationService.notifyDeleted(WebSocketNotificationService.CREATURE, resident.getId());
        }

        repository.delete(city);
        notificationService.notifyDeleted(WebSocketNotificationService.CITY, id);
    }

    public Page<MagicCityDTO> get(int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<MagicCity> citiesPage = repository.findAll(pageable);

        List<MagicCityDTO> dtoList = new ArrayList<>();
        for (MagicCity c : citiesPage.getContent()) {
            dtoList.add(mapper.toDto(c));
        }
        return new PageImpl<>(dtoList, pageable, citiesPage.getTotalElements());
    }

    public MagicCityDTO getById(long id){
        Optional<MagicCity> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("City not found with id " + id);
        }
        return mapper.toDto(found.get());
    }
}