package com.booking_system.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AllocationServiceTest {

    @InjectMocks
    private AllocationService allocationService;

    @Test
    public void calculateSkillMatchTest_EmptyRequiredSkills(){
        List<String> requiredSkills = new ArrayList<>();
        List<String> associateSkills = List.of("skill1", "skill2", "skill3");
        double result = allocationService.calculateSkillMatch(requiredSkills, associateSkills);
        assertEquals(1.0, result);
    }

    @Test
    public void calculateSkillMatchTest_EmptyAssociateSkills(){
        List<String> associateSkills = new ArrayList<>();
        List<String> requiredSkills = List.of("skill1", "skill2", "skill3");
        double result = allocationService.calculateSkillMatch(requiredSkills, associateSkills);
        assertEquals(0.0, result);
    }

    @Test
    public void calculateSkillMatchTest_PerfectMatch(){
        List<String> skills = List.of("skill1", "skill2", "skill3");
        List<String> associateSkills = skills;
        List<String> requiredSkills = skills;
        double result = allocationService.calculateSkillMatch(requiredSkills, associateSkills);
        assertEquals(1.0, result);
    }

    @ParameterizedTest
    @CsvSource({
        "'haircut,coloring,styling', 'haircut,massage', 0.33",           // 1 out of 3 skills match
        "'haircut,coloring', 'haircut,coloring,styling,massage', 1.0",  // All required skills present
        "'haircut,coloring,styling', 'haircut,coloring', 0.67",         // 2 out of 3 skills match  
        "'massage,facial', 'haircut,coloring', 0.0",                    // No skills match
        "'haircut', 'haircut,coloring,styling', 1.0",                   // Single skill perfect match
        "'haircut,coloring,styling,massage', 'haircut,styling', 0.5",   // 2 out of 4 skills match
        "'programming,testing,debugging', 'programming,debugging,deployment', 0.67" // 2 out of 3 match
    })
    public void calculateSkillMatchTest_ImperfectMatch(String requiredSkillsStr, String associateSkillsStr, double expectedScore) {
        // Given: Parse CSV strings into Lists
        List<String> requiredSkills = Arrays.asList(requiredSkillsStr.split(","));
        List<String> associateSkills = Arrays.asList(associateSkillsStr.split(","));
        
        // When: Calculate skill match
        double result = allocationService.calculateSkillMatch(requiredSkills, associateSkills);
        
        // Then: Assert with tolerance for floating point precision
        assertEquals(expectedScore, result, 0.01, 
            String.format("Expected %.2f for required: %s, associate: %s", 
                         expectedScore, requiredSkills, associateSkills));
    }

}
