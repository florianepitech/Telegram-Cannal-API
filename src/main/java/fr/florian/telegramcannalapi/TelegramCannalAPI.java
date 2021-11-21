package fr.florian.telegramcannalapi;

import fr.florian.telegramcannalapi.object.TelegramMessage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramCannalAPI {

    private String botId, cannalId;
    private Boolean start = false;

    private Object lock = new Object();
    private ArrayList<TelegramMessage> messages = new ArrayList<>();

    public TelegramCannalAPI(String botId, String cannalId) {
        this.botId = botId;
        this.cannalId = cannalId;
        if (!cannalId.startsWith("@")) this.cannalId = "@" + this.cannalId;
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

    /*
     *      PRIVATE FUNCTION
     */

    private void sendMessage(TelegramMessage telegramMessage) throws IOException {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, botId, cannalId, telegramMessage.getContent());
        send(urlString);
    }


    private void sendStickers(TelegramMessage telegramMessage) throws IOException {
        String urlString = "https://api.telegram.org/bot%s/sendSticker?chat_id=%s&sticker=%s";
        urlString = String.format(urlString, botId, cannalId, telegramMessage.getContent());
        send(urlString);
    }

    private void sendPicture(TelegramMessage telegramMessage) {

    }

    private void send(String request) throws IOException {
        URL url = new URL(request);
        URLConnection conn = url.openConnection();

        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        String response = sb.toString();
    }

}
