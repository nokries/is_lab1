package org.example.is_lab1.controllers;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.SuccessResponse;
import org.example.is_lab1.services.SpecialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/special")
public class SpecialController {
    private final SpecialService service;


    public SpecialController(SpecialService specialService){
        this.service = specialService;
    }

    @GetMapping("/minCoordinates")
    public ResponseEntity<BookCreatureDTO> getMinCoordinates(){
        return ResponseEntity.ok(service.minCoordinates());
    }

    @GetMapping("/nameMatcher/{value}")
    public ResponseEntity<List<BookCreatureDTO>> getListByName(@PathVariable String value){
        return ResponseEntity.ok(service.nameMatch(value));
    }

    @GetMapping("/ringBelow/{value}")
    public ResponseEntity<List<BookCreatureDTO>> getListByRing(@PathVariable int value){
        return ResponseEntity.ok(service.ringBelow(value));
    }

    @PostMapping("/takeRings")
    public ResponseEntity<SuccessResponse<Void>> takeRings(){
        int count = service.takeRingsFromHobbits();
        return ResponseEntity.ok(SuccessResponse.of("Rings taken from hobbits: " + count));
    }

    @PostMapping("/move")
    public ResponseEntity<SuccessResponse<Void>> moveToMordor(){
        int count = service.moveInMordor();
        return ResponseEntity.ok(SuccessResponse.of("Hobbits moved to Mordor: " + count));
    }

}
