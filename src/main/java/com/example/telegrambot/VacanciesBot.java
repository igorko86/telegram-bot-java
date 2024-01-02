package com.example.telegrambot;

import com.example.telegrambot.dto.VacancyDto;
import com.example.telegrambot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VacanciesBot extends TelegramLongPollingBot {
    @Autowired
    private VacancyService vacancyService;

    private final Map<Long, String> lastShowedVacancies = new HashMap<>();

    public VacanciesBot() {
        // put bot id here
        super("");
    }

    @Override
    public void onUpdateReceived(Update update) {
       if (update.getMessage() != null) {
           handleStart(update);
       }
       if (update.getCallbackQuery() != null) {
           String data = update.getCallbackQuery().getData();
           System.out.println(data);
           switch (data) {
               case "showJuniorVacancies":
                   showJuniorVacancies(update);
                   break;
               case "showMiddleVacancies":
                   showMiddleVacancies(update);
                   break;
               case "showSeniorVacancies":
                   showSeniorVacancies(update);
                   break;
               case "backToVacancies":
                   handleBackToVacanciesComand(update);
                   break;
               case "backToStartMenu":
                   handleBackToStartMenuCommand(update);
                   break;
           }

           if(data.startsWith("vacancyId=")) {
               String vacancyId = data.split("=")[1];
               showVacancyDescription(Integer.parseInt(vacancyId), update);
           }
       }

    }

    private void handleBackToStartMenuCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Select");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getStartMenu());

        send(sendMessage);
    }


    private void handleBackToVacanciesComand(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String lastShowed = lastShowedVacancies.get(chatId);

        if("junior".equals(lastShowed)) {
            showJuniorVacancies(update);
        } else if ("middle".equals(lastShowed)) {
            showMiddleVacancies(update);
        } else if ("senior".equals(lastShowed)) {
            showSeniorVacancies(update);
        }
    }

    private void showVacancyDescription(int id, Update update) {
        SendMessage sendMessage = new SendMessage();
        VacancyDto vacancy = vacancyService.get(id + "");
        sendMessage.setText(vacancy.getShortDescription());
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getBackToVacancyMenu());
        send(sendMessage);
    }

    private ReplyKeyboard getBackToVacancyMenu() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton backToVacancyButton = new InlineKeyboardButton();
        backToVacancyButton.setText("Back to Vacancies");
        backToVacancyButton.setCallbackData("backToVacancies");
        buttons.add(backToVacancyButton);

        InlineKeyboardButton backToMenuButton = new InlineKeyboardButton();
        backToMenuButton.setText("Back to Start Menu");
        backToMenuButton.setCallbackData("backToStartMenu");
        buttons.add(backToMenuButton);

        InlineKeyboardMarkup keyboardButtons = new InlineKeyboardMarkup();
        keyboardButtons.setKeyboard(List.of(buttons));

        return keyboardButtons;
    }

    private void handleStart(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Select something");
        sendMessage.setReplyMarkup(getStartMenu());

        send(sendMessage);
    }

    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void showJuniorVacancies(Update update) {
        lastShowedVacancies.put(update.getCallbackQuery().getMessage().getChatId(), "junior");
        showVacancies(update, getJuniorVacancy());
    }

    private void showMiddleVacancies(Update update) {
        lastShowedVacancies.put(update.getCallbackQuery().getMessage().getChatId(), "middle");
        showVacancies(update, getMiddleVacancy());
    }

    private void showSeniorVacancies(Update update) {
        lastShowedVacancies.put(update.getCallbackQuery().getMessage().getChatId(), "senior");
        showVacancies(update, getSeniorVacancy());
    }

    private void showVacancies(Update update, ReplyKeyboard content) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Pls choose vacancy");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(content);

        send(sendMessage);
    }

    private ReplyKeyboard getJuniorVacancy() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<VacancyDto> vacancyDtoList = vacancyService.getJuniorVacancies();
        System.out.println(vacancyDtoList);
        for (VacancyDto v: vacancyDtoList) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(v.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + v.getId());
            buttons.add(vacancyButton);
        }

        InlineKeyboardMarkup keyboardButtons = new InlineKeyboardMarkup();
        keyboardButtons.setKeyboard(List.of(buttons));

        return keyboardButtons;
    }

    private ReplyKeyboard getMiddleVacancy() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton myVacancy = new InlineKeyboardButton();
        myVacancy.setText("Middle Developer FE");
        myVacancy.setCallbackData("vacancyId=1");
        buttons.add(myVacancy);

        InlineKeyboardButton myVacancyBE = new InlineKeyboardButton();
        myVacancyBE.setText("Middle Developer BE");
        myVacancyBE.setCallbackData("vacancyId=2");
        buttons.add(myVacancyBE);

        InlineKeyboardMarkup keyboardButtons = new InlineKeyboardMarkup();
        keyboardButtons.setKeyboard(List.of(buttons));

        return keyboardButtons;
    }

    private ReplyKeyboard getSeniorVacancy() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton myVacancy = new InlineKeyboardButton();
        myVacancy.setText("Senior Developer FE");
        myVacancy.setCallbackData("vacancyId=1");
        buttons.add(myVacancy);

        InlineKeyboardButton myVacancyBE = new InlineKeyboardButton();
        myVacancyBE.setText("Senior Developer BE");
        myVacancyBE.setCallbackData("vacancyId=2");
        buttons.add(myVacancyBE);

        InlineKeyboardMarkup keyboardButtons = new InlineKeyboardMarkup();
        keyboardButtons.setKeyboard(List.of(buttons));

        return keyboardButtons;
    }


    private ReplyKeyboard getStartMenu() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton junior = new InlineKeyboardButton();
        junior.setText("Junior");
        junior.setCallbackData("showJuniorVacancies");
        buttons.add(junior);

        InlineKeyboardButton middle = new InlineKeyboardButton();
        middle.setText("Middle");
        middle.setCallbackData("showMiddleVacancies");
        buttons.add(middle);

        InlineKeyboardButton senior = new InlineKeyboardButton();
        senior.setText("Senior");
        senior.setCallbackData("showSeniorVacancies");
        buttons.add(senior);

        InlineKeyboardMarkup keyboardButtons = new InlineKeyboardMarkup();
        keyboardButtons.setKeyboard(List.of(buttons));

        return keyboardButtons;
    }

    @Override
    public String getBotUsername() {
        return "my first bot";
    }
}
