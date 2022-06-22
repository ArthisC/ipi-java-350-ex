package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import org.apache.commons.lang.NullArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    //Tests getNombreAnneeAnciennete
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


    //Test augmenterSalaire
    @Test
    public void testAugmenterSalaire10Pourcent(){
        //given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);

        //when
        try {
            employe.augmenterSalaire(0.1);
        } catch (EmployeException e) {
            e.printStackTrace();
        }

        //then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1100.0);

    }

    @Test
    public void testAugmenterSalaireWithPourcentageNegatif(){
        //given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employe.augmenterSalaire(-0.2);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le pourcentage doit être compris entre 0 exclu et 1 inclu !");

    }

    @Test
    public void testAugmenterSalaireWithPourcentageEgale0(){
        //given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employe.augmenterSalaire(0.0);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le pourcentage doit être compris entre 0 exclu et 1 inclu !");

    }

    @Test
    public void testAugmenterSalaire150Pourcent(){
        //given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employe.augmenterSalaire(1.5);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le pourcentage doit être compris entre 0 exclu et 1 inclu !");

    }

    @Test
    public void testAugmenterSalaireWithSalaireNull(){
        //given
        Employe employe = new Employe();
        employe.setSalaire(null);

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employe.augmenterSalaire(0.1);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le salaire est null !");

    }


    //Test getNbRtt
    @ParameterizedTest
    @CsvSource({
            "2019,1.0,8",  //année non bissextile commençant un mardi
            "2021,1.0,11", //année non bissextile commençant un vendredi
            "2022,1.0,10", //année non bissextile commençant un samedi
            "2032,1.0,12", //année bissextile commençant un jeudi
            "2032,0.5,6",  //année bissextile commençant un jeudi avec mi-temps
            "2028,1.0,8",  //année bissextile commençant un samedi
            "2016,1.0,10"  //année bissextile commençant un vendredi
    })
    public void testGetNbRtt(
            Integer annee,
            Double temps_partiel,
            Integer nbRttAttendu
    ){
        //Given
        LocalDate date = LocalDate.of(annee,1,1);
        Employe employe = new Employe();
        employe.setTempsPartiel(temps_partiel);

        //When
        Integer nbRtt = employe.getNbRtt(date);

        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbRttAttendu);
    }
}
