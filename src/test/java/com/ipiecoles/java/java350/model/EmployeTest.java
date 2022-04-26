package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void testGetNombreAnneeAncienneteNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    public void testGetNombreAnneeAncienneteAfterNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(10));

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isEqualTo(10);
    }

    @Test
    public void testGetNombreAnneeAncienneteBeforeNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    public void testGetNombreAnneeAncienneteNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    //test pour getPrimeAnuelle
    //tester matricule null, qui commence par "M", qui ne commence pas par "M", qui n'est pas valide
    // tester performance null ou valeur positive / négative / vaut 0
    // tester temps partiel null négatif, positif, vaut 0
    @ParameterizedTest
    @CsvSource({
            "'M123456',0,1,1.0,1700.0",
            "'M123456',1,1,1.0,1800.0",
            "'T123456',0,1,1.0,1000.0",
            ",0,1,1.0,1000.0",
            "'T123456',0,,1.0,1000.0",
            "'T123456',0,1,0.5,500.0",
            "'T123456',0,2,1.0,2300.0",
            "'T123456',1,2,1.0,2400.0",
            "'T123456',1,1,1.0,1100.0"
    })
    public void testGetPrimeAnuelle(
            String matricule,
            Integer nbAnneesAnciennete,
            Integer performance,
            Double tauxActivite,
            Double prime
    ){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 2500d, performance, tauxActivite);

        //When
        Double primeObtenue = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeObtenue).isEqualTo(prime);
    }
}
