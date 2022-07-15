package net.telegram.channel.exception;

import org.apache.http.HttpResponse;

public class HttpClientException extends HttpException {

    public HttpClientException(HttpResponse httpResponse) {
        super(httpResponse);
    }
}
