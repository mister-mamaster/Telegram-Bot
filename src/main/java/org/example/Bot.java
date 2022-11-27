package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    Boolean button1IsOn = false;
    Boolean button2IsOn = false;

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
            try {
                sendMsg(message, findText(message));
            } catch (TesseractException | IOException | TelegramApiException e) {
                e.printStackTrace();
            }
            button2IsOn = false;
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

    private String findText(Message message) throws TesseractException, IOException, TelegramApiException {
        PhotoSize photo = message.getPhoto().get(2);
        InputMediaPhoto input = new InputMediaPhoto(photo.getFileId());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(new URL("https://api.telegram.org/bot5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs/getFile?file_id=" + photo.getFileId()));
        node = node.get("result");
        String filePath = "https://api.telegram.org/file/bot5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs/" +
                node.get("file_path");
        filePath = filePath.replace("\"", "");
        System.out.println("Фото: [" + filePath + "]");

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng");
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(new URL("https://api.telegram.org/file/bot5648572959:AAGbFTYRNJ-HQEjnlLmtJAgdoAKsVlnP5qs/photos/file_3.jpg"));
        return tesseract.doOCR(image);
    }
}