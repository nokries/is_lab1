package org.example.is_lab1.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Table(name = "magic_city")
public class MagicCity {
    @Id
    @SequenceGenerator(name = "city_gen", sequenceName = "magic_city_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_gen")
    private Long id;

    // Поле не может быть null, строка не может быть пустой
    @Column(nullable = false)
    @NotBlank(message = "Название города не может быть пустым")
    private String name;

    // Значение поля должно быть больше 0, поле не может быть null
    @Column(nullable = false)
    @NotNull(message = "Площадь города обязательна")
    @Positive(message = "Площадь города должна быть больше 0")
    private Double area;

    // Значение поля должно быть больше 0, поле не может быть null
    @Column(nullable = false)
    @NotNull(message = "Население города обязательно")
    @Positive(message = "Население города должно быть больше 0")
    private Integer population;

    // Если не задана - ставится текущее время при сохранении
    private ZonedDateTime establishmentDate;

    // Поле может быть null
    @Enumerated(EnumType.STRING)
    private BookCreatureType governor;

    @Column(nullable = false)
    private boolean capital;

    // Значение поля должно быть больше 0
    @Positive(message = "Плотность населения должна быть больше 0")
    private long populationDensity;


    public MagicCity(String name, Double area, Integer population, String governor, boolean capital, long populationDensity){
        this.area = area;
        this.capital = capital;
        this.governor = BookCreatureType.getType(governor);
        this.population = population;
        this.populationDensity = populationDensity;
        this.name = name;
    }

    public MagicCity(){}

    @PrePersist
    private void fillEstablishmentDate() {
        if (establishmentDate == null) {
            establishmentDate = ZonedDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getArea() {
        return area;
    }

    public Integer getPopulation() {
        return population;
    }

    public ZonedDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public BookCreatureType getGovernor() {
        return governor;
    }

    public boolean isCapital() {
        return capital;
    }

    public long getPopulationDensity() {
        return populationDensity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public void setEstablishmentDate(ZonedDateTime establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public void setGovernor(BookCreatureType governor) {
        this.governor = governor;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public void setPopulationDensity(long populationDensity) {
        this.populationDensity = populationDensity;
    }

    @Override
    public String toString() {
        return "MagicCity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", area=" + area +
                ", population=" + population +
                ", establishmentDate=" + establishmentDate +
                ", governor=" + governor +
                ", capital=" + capital +
                ", populationDensity=" + populationDensity +
                '}';
    }
}

