package com.example.demo.ForRCSCH.Fixtures;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
public class FixturePerson {

    private String name;
    private String surname;
    private Long age;
    private Long height;
    private Long weight;
    private Boolean isMan;

}
