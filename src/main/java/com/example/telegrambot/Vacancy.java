package com.example.telegrambot;

import java.util.List;

public class Vacancy {
    private final String name;
    private final long id;

    public Vacancy(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

}
