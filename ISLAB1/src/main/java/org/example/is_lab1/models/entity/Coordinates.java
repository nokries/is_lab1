package org.example.is_lab1.models.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;


@Entity
public class Coordinates {
    @Id
    @SequenceGenerator(name = "coordinates_gen", sequenceName = "coordinates_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_gen")
    private Long id;

    @Max(value = 735, message = "Координата x не может быть больше 735")
    private int x; //Максимальное значение поля: 735
    private int y;

    public Coordinates(){}

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}

