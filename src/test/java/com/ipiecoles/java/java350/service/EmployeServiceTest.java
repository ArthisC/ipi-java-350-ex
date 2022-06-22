package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @Mock
    EmployeRepository employeRepository;

    @InjectMocks
    EmployeService employeService = new EmployeService();


    // Tests embaucheEmploye
    @Test
    public void testEmbaucheEmployeWithoutEmploye() throws EmployeException {
        //given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);

        //when
        employeService.embaucheEmploye("oui","non", Poste.TECHNICIEN, NiveauEtude.BAC,1.0);

        //then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeCaptor.capture());
        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
        Assertions.assertThat(employe.getNom()).isEqualTo("oui");
        Assertions.assertThat(employe.getPrenom()).isEqualTo("non");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1673.34);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1);
    }

    @Test
    public void testEmbaucheEmployeWithEmploye() throws EmployeException {
        //given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("11111");
        Mockito.when(employeRepository.findByMatricule("T11112")).thenReturn(null);


        //when
        employeService.embaucheEmploye("oui","non", Poste.TECHNICIEN, NiveauEtude.BAC,0.5);


        //then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeCaptor.capture());
        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T11112");
        Assertions.assertThat(employe.getNom()).isEqualTo("oui");
        Assertions.assertThat(employe.getPrenom()).isEqualTo("non");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(836.67);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(0.5);
    }

    @Test
    public void testEmbaucheEmployeWithTempsPartielNull() throws EmployeException {
        //given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        // dans le cas d'un employe (e) retourné lors d'un e = erepo.save(e) coté classe testée
        // il faut faire ceci pour pouvoir exécuter du code qui demanderais une méthode public liée sinon valeur null et plantage:
        // Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());


        //when
        employeService.embaucheEmploye("oui","non", Poste.TECHNICIEN, NiveauEtude.BAC,null);


        //then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeCaptor.capture());
        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
        Assertions.assertThat(employe.getNom()).isEqualTo("oui");
        Assertions.assertThat(employe.getPrenom()).isEqualTo("non");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1673.34);
        Assertions.assertThat(employe.getTempsPartiel()).isNull();
    }

    @Test
    public void testEmbaucheEmployeWithSameMatricule() throws EmployeException {
        //given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(new Employe());

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.embaucheEmploye("oui","non", Poste.TECHNICIEN, NiveauEtude.BAC,null);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EntityExistsException.class).hasMessage("L'employé de matricule T00001 existe déjà en BDD");
    }

    @Test
    public void testEmbaucheEmployeWithMaxMatriculeReach() throws EmployeException {
        //given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.embaucheEmploye("oui","non", Poste.TECHNICIEN, NiveauEtude.BAC,null);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Limite des 100000 matricules atteinte !");
    }


    // Tests calculPerformanceCommercial
    @Test
    public void testCalculPerformanceCommercialWithCaTraiteNegatif(){
        //given

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.calculPerformanceCommercial("C12345", -1L, 1L);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le chiffre d'affaire traité ne peut être négatif ou null !");
    }

    @Test
    public void testCalculPerformanceCommercialWithObjectifCaNegatif(){
        //given

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.calculPerformanceCommercial("C12345", 1L, -1L);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
    }

    @Test
    public void testCalculPerformanceCommercialWithMatriculeVautT(){
        //given

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.calculPerformanceCommercial("T12345", 1L, 1L);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le matricule ne peut être null et doit commencer par un C !");
    }

    @Test
    public void testCalculPerformanceCommercialWithMatriculeNotInBDD(){
        //given
        Mockito.when(employeRepository.findByMatricule("C12345")).thenReturn(null);

        //when
        Throwable t = Assertions.catchThrowable(() -> {
            employeService.calculPerformanceCommercial("C12345", 1L, 1L);
        });

        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class).hasMessage("Le matricule C12345 n'existe pas !");
    }
    @ParameterizedTest
    @CsvSource({
            "50,1",  //cas 1
            "90,8",  //cas 2
            "100,10", //cas 3
            "110,11", //cas 4
            "150,15"  //cas 5 + perf > perfMoyenne
    })
    public void testCalculPerformanceCommercial(
            Long caTraite,
            Integer perforfanceFinale
    ){
        //given
        Mockito.when(employeRepository.findByMatricule("C12345")).thenReturn(
                new Employe("A","B","C12345",LocalDate.now(),2000.0, 10,1.0
                        ));
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(12.0);

        //when
        try {
            employeService.calculPerformanceCommercial("C12345", caTraite, 100L);
        } catch (EmployeException e) {
            e.printStackTrace();
        }

        //then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeCaptor.capture());
        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getPerformance()).isEqualTo(perforfanceFinale);
    }
}
