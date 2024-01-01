package com.example.telegrambot.service;

import com.example.telegrambot.dto.VacancyDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VacancyService {
    private VacanciesReaderService vacanciesReaderService = new VacanciesReaderService();
    private final Map<String, VacancyDto> vacancies = new HashMap<>();

    @PostConstruct
    public void init() {
        List<VacancyDto> list = vacanciesReaderService.getVacanciesFromFile("vacancies.csv");

        for (VacancyDto v: list) {
            vacancies.put(v.getId(), v);
        }
    }

    public List<VacancyDto> getJuniorVacancies() {
        return vacancies.values().stream().filter(v -> v.getTitle().toLowerCase().contains("junior")).toList();
    }

    public VacancyDto get(String id) {
        return vacancies.get(id);
    }
}
