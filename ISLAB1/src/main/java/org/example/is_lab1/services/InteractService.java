package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.repository.RingRepository;
import org.example.is_lab1.utils.BookCreatureMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InteractService {
    private final InteractRepository repository;
    private final BookCreatureMapper mapper;
    private final RingRepository ringRepository;
    private final CityRepository cityRepository;
    private final WebSocketNotificationService notificationService;


    public InteractService(InteractRepository repository, BookCreatureMapper mapper, RingRepository ringRepository, CityRepository cityRepository, WebSocketNotificationService notificationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.ringRepository = ringRepository;
        this.cityRepository = cityRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public BookCreatureDTO create(BookCreatureDTO creature){
        BookCreature el = mapper.toEntity(creature);
        repository.save(el);
        BookCreatureDTO created = mapper.toDto(el);
        notificationService.notifyCreated(WebSocketNotificationService.CREATURE, el.getId(), created);
        return created;
    }

    @Transactional
    public BookCreatureDTO modify(long id, BookCreatureDTO creature){
        Optional<BookCreature> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Creature not found with id " + id);
        }
        BookCreature existing = found.get();
        mapper.updateEntityFromDto(creature, existing);
        repository.save(existing);
        BookCreatureDTO updated = mapper.toDto(existing);
        notificationService.notifyUpdated(WebSocketNotificationService.CREATURE, id, updated);
        return updated;
    }

    @Transactional
    public void delete(long id){
        Optional<BookCreature> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Creature not found with id " + id);
        }
        BookCreature creature = found.get();

        MagicCity city = creature.getCreatureLocation();

        repository.delete(creature);

        // Проверяем, остались ли ещё жители этого города
        if (city != null) {
            boolean hasResidents = repository.existsByCreatureLocation(city);
            if (!hasResidents) {
                cityRepository.delete(city);
                notificationService.notifyDeleted(WebSocketNotificationService.CITY, city.getId());
            }
        }
        notificationService.notifyDeleted(WebSocketNotificationService.CREATURE, id);
    }

    // filterBy - по какой строковой колонке фильтруем (name / creatureType / city / ring), фильтр - только полное совпадение
    public Page<BookCreatureDTO> get(Pageable pageable, String filterBy, String filter){
        Page<BookCreature> page;
        if (filter != null && !filter.isEmpty()) {
            String column;
            if (filterBy == null) {
                column = "name";
            } else {
                column = filterBy;
            }
            switch (column) {
                case "creatureType" -> page = repository.findByCreatureType(BookCreatureType.getType(filter), pageable);
                case "city" -> page = repository.findByCreatureLocationName(filter, pageable);
                case "ring" -> page = repository.findByRingName(filter, pageable);
                default -> page = repository.findByName(filter, pageable);
            }
        } else {
            page = repository.findAll(pageable);
        }

        List<BookCreatureDTO> dtoList = new ArrayList<>();
        for (BookCreature c : page.getContent()) {
            dtoList.add(mapper.toDto(c));
        }
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    public BookCreatureDTO getById(long id){
        Optional<BookCreature> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Creature not found with id " + id);
        }
        return mapper.toDto(found.get());
    }
}