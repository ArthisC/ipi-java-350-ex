package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    public void delete(){
        employeRepository.deleteAll();
    }

    @Test
    public void testFindLastMatriculeWithoutEmploye(){
        //given

        //when
        String matricule = employeRepository.findLastMatricule();

        //then
        Assertions.assertThat(matricule).isNull();
    }
    @Test
    public void testFindLastMatriculeWith1Employe(){
        //given
        Employe employe = new Employe("Jhon","Doe","M111222", LocalDate.now(),1000.0,1,1.0);
        employeRepository.save(employe);

        //when
        String matricule = employeRepository.findLastMatricule();

        //then
        Assertions.assertThat(matricule).isEqualTo("111222");
    }

    @Test
    public void testFindLastMatriculeWith3Employe(){
        //given
        Employe employe = new Employe("Jhon","Doe","M111222", LocalDate.now(),1000.0,1,1.0);
        Employe employe1 = new Employe("Jhon","Doe","C654321", LocalDate.now(),1000.0,1,1.0);
        Employe employe2 = new Employe("Jhon","Doe","M246800", LocalDate.now(),1000.0,1,1.0);
        employeRepository.save(employe);
        employeRepository.save(employe1);
        employeRepository.save(employe2);

        //when
        String matricule = employeRepository.findLastMatricule();

        //then
        Assertions.assertThat(matricule).isEqualTo("654321");
    }
}
