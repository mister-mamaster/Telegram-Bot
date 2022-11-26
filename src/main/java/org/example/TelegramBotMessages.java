package org.example;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;
import java.text.DecimalFormat;

public class TelegramBotMessages {
    public String faceMessage(Face face) {
        StringBuilder faceMessage = new StringBuilder();
        DecimalFormat df = new DecimalFormat("###.##");
        faceMessage.append("Возраст: " + face.getAge().getMin() + " или ." +
                face.getAge().getMax() + "Пол: " + face.getGender().getGenderLabel());
        return faceMessage.toString();
    }
    public String faceNotFound() {
        return "Не можем найти никакого лица.\n" +
                "Пожалуйста, пришлите мне еще одну фотографию.";
    }
}