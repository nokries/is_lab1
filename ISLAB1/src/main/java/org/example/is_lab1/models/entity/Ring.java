package org.example.is_lab1.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
public class Ring {
    // Название "пустого" кольца-заглушки. Поле ring в BookCreature не может быть null,
    // поэтому когда у хоббита "забирают кольцо", ему выдаётся кольцо с этим названием.
    public static final String NO_RING = "Без кольца";

    @Id
    @SequenceGenerator(name = "ring_gen", sequenceName = "ring_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ring_gen")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Название кольца не может быть пустым")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Column(nullable = false)
    @NotNull(message = "Сила кольца обязательна")
    @Positive(message = "Сила кольца должна быть больше 0")
    private Integer power; //Значение поля должно быть больше 0, Поле не может быть null

    @Column(nullable = false)
    @NotNull(message = "Вес кольца обязателен")
    @Positive(message = "Вес кольца должен быть больше 0")
    private Integer weight; //Значение поля должно быть больше 0, Поле не может быть null

    public Ring(String name, Integer power, Integer weight){
        this.name = name;
        this.power = power;
        this.weight = weight;
    }

    public Ring(){}

    public Ring(Long id, String name, Integer power, Integer weight){
        this.id = id;
        this.name = name;
        this.power = power;
        this.weight = weight;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPower() {
        return power;
    }

    public Integer getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Ring{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", power=" + power +
                ", weight=" + weight +
                '}';
    }
}

