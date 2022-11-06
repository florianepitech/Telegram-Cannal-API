package net.telegram.channel;

import net.httpclient.wrapper.exception.HttpClientException;
import net.httpclient.wrapper.exception.HttpServerException;
import net.httpclient.wrapper.session.HttpClientSession;
import net.telegram.channel.enums.TelegramMessageType;
import net.telegram.channel.object.TelegramMessage;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramChannel {

    private String botId;
    private String cannalId;
    private HttpClientSession httpClientSession;
    private Boolean start = false;

    private final Object lock = new Object();
    private final ArrayList<TelegramMessage> messages = new ArrayList<>();

    public TelegramChannel() {
        String telegramChannelId = System.getProperty("telegramChannelId");
        String telegramBotId = System.getProperty("telegramBotId");
        if (telegramChannelId == null || telegramChannelId.isEmpty())
            throw new IllegalArgumentException("telegramChannelId property is not set");
        if (telegramBotId == null || telegramBotId.isEmpty())
            throw new IllegalArgumentException("telegramBotId property is not set");
        construct(telegramChannelId, telegramBotId);
    }

    public TelegramChannel(String botId, String cannalId) {
        construct(botId, cannalId);
    }

    private void construct(String botId, String cannalId) {
        this.botId = botId;
        this.cannalId = cannalId;
        // if (!cannalId.startsWith("@")) this.cannalId = "@" + this.cannalId;
        this.httpClientSession = new HttpClientSession();
    }

    public void start() {
        if (start) return;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if(messages.size() > 0) {
                        TelegramMessage telegramMessage = messages.get(0);
                        try {
                            httpClientSession.resetHttpClient();
                            switch (telegramMessage.getTelegramMessageType()) {
                                case MESSAGE -> sendMessage(telegramMessage);
                                case STICKER -> sendStickers(telegramMessage);
                            }
                            messages.remove(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, 4, TimeUnit.SECONDS);
        start = true;
    }

    public void addMessageToQueue(TelegramMessage telegramMessage) {
        messages.add(telegramMessage);
    }

    public void addMessageToQueue(String message) {
        messages.add(new TelegramMessage(TelegramMessageType.MESSAGE, message));
    }

    public void addMessageToQueue(String message, Object... args) {
        ParameterizedMessage pm = new ParameterizedMessage(message, args);
        addMessageToQueue(pm.getFormattedMessage());
    }

    /*
     *      PRIVATE FUNCTION
     */

    public void forceSendMessage(String message) throws HttpClientException, IOException, HttpServerException {
        TelegramMessage tm = new TelegramMessage(TelegramMessageType.MESSAGE, message);
        sendMessage(tm);
    }

    public void forceSendMessage(String message, Object... args) throws HttpClientException, IOException, HttpServerException {
        ParameterizedMessage pm = new ParameterizedMessage(message, args);
        forceSendMessage(pm.getFormattedMessage());
    }

    public void forceSendSticker(String sticker) throws HttpClientException, IOException, HttpServerException {
        TelegramMessage tm = new TelegramMessage(TelegramMessageType.STICKER, sticker);
        sendStickers(tm);
    }

    private void sendMessage(TelegramMessage telegramMessage) throws IOException, HttpClientException, HttpServerException {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, botId, cannalId, URLEncoder.encode(telegramMessage.getContent(), StandardCharsets.UTF_8));
        httpClientSession.sendGet(urlString);
    }


    private void sendStickers(TelegramMessage telegramMessage) throws IOException, HttpClientException, HttpServerException {
        String urlString = "https://api.telegram.org/bot%s/sendSticker?chat_id=%s&sticker=%s";
        urlString = String.format(urlString, botId, cannalId, URLEncoder.encode(telegramMessage.getContent(), StandardCharsets.UTF_8));
        httpClientSession.sendGet(urlString);
    }

}
