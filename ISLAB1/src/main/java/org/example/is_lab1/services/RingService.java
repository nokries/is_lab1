package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.repository.RingRepository;
import org.example.is_lab1.utils.RingMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RingService {
    private final RingRepository repository;
    private final InteractRepository interactRepository;
    private final RingMapper mapper;
    private final WebSocketNotificationService notificationService;



    private Ring findRingOrThrow(long id) {
        Optional<Ring> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Ring not found with id " + id);
        }
        return found.get();
    }


    @Transactional
    public RingDTO create(RingDTO ring){
        Ring el = mapper.toEntity(ring);
        repository.save(el);
        RingDTO created = mapper.toDto(el);
        notificationService.notifyCreated(WebSocketNotificationService.RING, el.getId(), created);
        return created;
    }

    @Transactional
    public RingDTO modify(long id, RingDTO ring){
        Ring el = findRingOrThrow(id);
        mapper.updateEntityFromDto(ring, el);
        repository.save(el);
        RingDTO updated = mapper.toDto(el);
        notificationService.notifyUpdated(WebSocketNotificationService.RING, id, updated);
        return updated;
    }

    @Transactional
    public void delete(long id){
        Ring ring = findRingOrThrow(id);


        Optional<BookCreature> ownerBox = interactRepository.findByRing(ring);
        if (ownerBox.isPresent()) {
            BookCreature owner = ownerBox.get();
            interactRepository.delete(owner);
            notificationService.notifyDeleted(WebSocketNotificationService.CREATURE, owner.getId());
        } else {
            repository.delete(ring);
        }
        notificationService.notifyDeleted(WebSocketNotificationService.RING, id);
    }

    public Page<RingDTO> get(int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Ring> ringsPage = repository.findRingsWithoutOwner(Ring.NO_RING, pageable);

        List<RingDTO> dtoList = new ArrayList<>();
        for (Ring r : ringsPage.getContent()) {
            dtoList.add(mapper.toDto(r));
        }
        return new PageImpl<>(dtoList, pageable, ringsPage.getTotalElements());
    }

    public RingDTO getById(long id){
        Ring ring = findRingOrThrow(id);
        return mapper.toDto(ring);
    }

}