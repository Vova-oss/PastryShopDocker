package com.example.demo;

import com.example.demo.ForRCSCH.Fixtures.FixtureController;
import com.example.demo.ForRCSCH.Fixtures.FixturePerson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FixtureTests{

    List<FixturePerson> list;

    @Before
    public void setUpBeforeMainTest(){
        list = new ArrayList<>();

        int x = 50;

        for (int i = 0; i < x; i++)
            addOnePerson(list);

    }

    public void addOnePerson(List<FixturePerson> list){

        String[] names = new String[]{"Karl", "Ivan", "Vladimir", "Anna", "Kolya","Polina", "Vadim", "Ekaterina","Lena"};
        String[] surnames = new String[]{"Cheese", "Car", "Cat", "Salt", "Lock","Smith", "Iot", "Fish","Dog"};

        FixturePerson fixturePerson = new FixturePerson();

        fixturePerson.setAge((long) (Math.random()*100));
        fixturePerson.setHeight((long) (Math.random()*100));
        fixturePerson.setWeight((long) (Math.random()*1000));
        fixturePerson.setName(names[(int)(Math.random()*10 - 1)]);
        fixturePerson.setSurname(surnames[(int)(Math.random()*10-1)]);
        fixturePerson.setIsMan((int)(Math.random() * 2) == 1);

        list.add(fixturePerson);

    }

    @Test
    public void mainTest(){
        FixtureController fixtureController = new FixtureController();
        System.out.println(list.size());
        fixtureController.list = this.list;
        fixtureController.createGraph();
    }

}
