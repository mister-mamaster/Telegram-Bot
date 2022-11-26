package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectFacesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    Boolean button1IsOn = false;
    Boolean button2IsOn = false;
    TelegramBotProperties botProperties = new TelegramBotProperties();
    TelegramBotMessages listMessages = new TelegramBotMessages();

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText() && !button1IsOn && !button2IsOn){
            switch (message.getText()){
                case "/spells":

                    try {
                        button1IsOn = true;
                        sendMsg(message, "Введите название заклинания\nРекомендуется использовать следующие:\nhellish rebuke\nantipathy sympathy\naura of vitality\naura of life");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "/quenta":
                    sendMsg(message, "*Оценка квенты*");
                    break;
                case "/photo":
                    button2IsOn = true;
                    sendMsg(message, "Отправьте фотографию и распознаваем возраст");
                    break;
            }
            return;
        }

        if(message != null && message.hasText() && button1IsOn){
            try {
                URLReader reader = new URLReader();
                sendMsg(message, reader.getData(message.getText()));
                button1IsOn = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if(message != null && button2IsOn){
            button2IsOn = false;
            if(analyzePhotos(message)) System.out.println("Успешно");
            else System.out.println("Ошибка");
        }
    }

    public String getBotUsername() {
        return "MyDnDSpells_Bot";
    }

    public String getBotToken() {
        return "5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs";
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("/spells"));
        keyboardFirstRow.add(new KeyboardButton("/quenta"));
        keyboardFirstRow.add(new KeyboardButton("/photo"));
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean analyzePhotos(Message message){
        boolean ret = false;
        try {
            PhotoSize photo = message.getPhoto().get(0);
            InputMediaPhoto input = new InputMediaPhoto(photo.getFileId());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(new URL("https://api.telegram.org/bot5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs/getFile?file_id=" + photo.getFileId()));
            node = node.get("result");
            String filePath = "https://api.telegram.org/file/bot5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs/" +
                    node.get("file_path");
            filePath = filePath.replace("\"", "");
            System.out.println("Фото: [" + filePath + "]");
            IamOptions options = new IamOptions.Builder()
                    .apiKey("123")
                    .build();
            VisualRecognition visualRecognition = new VisualRecognition("2018-03-19",
                    options);
            DetectFacesOptions detectFacesOptions = new DetectFacesOptions.Builder()
                    .url(filePath)
                    .build();
            DetectedFaces result = visualRecognition.detectFaces(detectFacesOptions).execute();
            System.out.println(result);
            if(result != null && result.getImages().get(0).getFaces().size()>0) {
                for(Face face : result.getImages().get(0).getFaces()) {
                    sendMsg(message, listMessages.faceMessage(face));
                }
            } else {
                sendMsg(message, listMessages.faceNotFound());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }
}