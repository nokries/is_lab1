package org.example.is_lab1.models.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "book_creature")
public class BookCreature {
    @Id
    @SequenceGenerator(name = "book_creature_gen", sequenceName = "book_creature_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_creature_gen")
    private Long id;//Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(nullable = false)
    @NotBlank(message = "Имя не может быть пустым")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "coordinates_fk_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Координаты обязательны")
    private Coordinates coordinates; //Поле не может быть null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Positive(message = "Возраст должен быть больше 0")
    private Long age; //Значение поля должно быть больше 0, Поле может быть null

    @Enumerated(EnumType.STRING)
    private BookCreatureType creatureType; //Поле может быть null

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "city_fk_id", referencedColumnName = "id")
    private MagicCity creatureLocation; //Поле может быть null

    @Positive(message = "Уровень атаки должен быть больше 0")
    private long attackLevel; //Значение поля должно быть больше 0

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "ring_fk_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Кольцо обязательно")
    private Ring ring; //Поле не может быть null


    public BookCreature(){}

    public BookCreature(String name, Coordinates coordinates, Long age, BookCreatureType creatureType,
                 MagicCity creatureLocation, long attackLevel, Ring ring){
        this.name = name;
        this.age = age;
        this.attackLevel = attackLevel;
        this.coordinates = coordinates;
        this.creatureLocation = creatureLocation;
        this.creatureType = creatureType;
        this.ring = ring;
    }

    @PrePersist
    private void fillCreationDate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Long getAge() {
        return age;
    }

    public BookCreatureType getCreatureType() {
        return creatureType;
    }

    public MagicCity getCreatureLocation() {
        return creatureLocation;
    }

    public long getAttackLevel() {
        return attackLevel;
    }

    public Ring getRing() {
        return ring;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public void setCreatureType(BookCreatureType creatureType) {
        this.creatureType = creatureType;
    }

    public void setCreatureLocation(MagicCity creatureLocation) {
        this.creatureLocation = creatureLocation;
    }

    public void setAttackLevel(long attackLevel) {
        this.attackLevel = attackLevel;
    }

    public void setRing(Ring ring) {
        this.ring = ring;
    }

    @Override
    public String toString() {
        return "BookCreature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", age=" + age +
                ", creatureType=" + creatureType +
                ", creatureLocation=" + creatureLocation +
                ", attackLevel=" + attackLevel +
                ", ring=" + ring +
                '}';
    }
}

