package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.utils.BookCreatureMapper;
import org.example.is_lab1.utils.MagicCityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpecialService {
    private static final String MORDOR = "Мордор";

    private final BookCreatureMapper mapper;
    private final MagicCityMapper cityMapper;
    private final InteractRepository repository;
    private final CityRepository cityRepository;
    private final WebSocketNotificationService notificationService;

    public SpecialService(BookCreatureMapper mapper, MagicCityMapper cityMapper, InteractRepository repository,
                          CityRepository cityRepository, WebSocketNotificationService notificationService){
        this.mapper = mapper;
        this.cityMapper = cityMapper;
        this.repository = repository;
        this.cityRepository = cityRepository;
        this.notificationService = notificationService;
    }


    @Transactional(readOnly = true)
    public BookCreatureDTO minCoordinates() {
        Optional<BookCreature> found = repository.findFirstByOrderByCoordinatesXAscCoordinatesYAsc();
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Объектов нет");
        }
        BookCreature creature = found.get();
        return mapper.toDto(creature);
    }

    @Transactional(readOnly = true)
    public List<BookCreatureDTO> nameMatch(String value) {
        List<BookCreatureDTO> result = new ArrayList<>();
        for (BookCreature creature : repository.findByNameContaining(value)) {
            result.add(mapper.toDto(creature));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<BookCreatureDTO> ringBelow(int value) {
        List<BookCreatureDTO> result = new ArrayList<>();
        for (BookCreature creature : repository.findByRingPowerLessThan(value)) {
            result.add(mapper.toDto(creature));
        }
        return result;
    }


    @Transactional
    public int takeRingsFromHobbits() {
        int changed = 0;
        for (BookCreature hobbit : repository.findByCreatureType(BookCreatureType.HOBBIT)) {
            if (hasRing(hobbit)) {
                hobbit.setRing(new Ring(Ring.NO_RING, 1, 1));
                BookCreature saved = repository.save(hobbit);
                notificationService.notifyUpdated(WebSocketNotificationService.CREATURE, saved.getId(), mapper.toDto(saved));
                changed++;
            }
        }
        return changed;
    }

    @Transactional
    public int moveInMordor() {
        MagicCity mordor = cityRepository.findFirstByName(MORDOR).orElse(null);
        if (mordor == null) {
            mordor = new MagicCity();
            mordor.setName(MORDOR);
            mordor.setArea(1.0);
            mordor.setPopulation(1);
            mordor.setPopulationDensity(1);
            mordor.setCapital(false);
            mordor.setEstablishmentDate(ZonedDateTime.now());
            mordor = cityRepository.save(mordor);
            notificationService.notifyCreated(WebSocketNotificationService.CITY, mordor.getId(), cityMapper.toDto(mordor));
        }

        int moved = 0;
        for (BookCreature hobbit : repository.findByCreatureType(BookCreatureType.HOBBIT)) {
            if (hasRing(hobbit)) {
                hobbit.setCreatureLocation(mordor);
                BookCreature saved = repository.save(hobbit);
                notificationService.notifyUpdated(WebSocketNotificationService.CREATURE, saved.getId(), mapper.toDto(saved));
                moved++;
            }
        }
        return moved;
    }

    private boolean hasRing(BookCreature creature) {
        return creature.getRing() != null && !Ring.NO_RING.equals(creature.getRing().getName());
    }

}