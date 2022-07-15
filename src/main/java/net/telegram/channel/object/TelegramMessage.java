package net.telegram.channel.object;

import net.telegram.channel.enums.TelegramMessageType;

public class TelegramMessage {

    private TelegramMessageType telegramMessageType;
    private String content;

    public TelegramMessage(TelegramMessageType telegramMessageType, String content) {
        this.telegramMessageType = telegramMessageType;
        this.content = content;
    }

    public TelegramMessageType getTelegramMessageType() {
        return telegramMessageType;
    }

    public void setTelegramMessageType(TelegramMessageType telegramMessageType) {
        this.telegramMessageType = telegramMessageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
