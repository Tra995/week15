package pet.store.controller.error;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
    private enum LogStatus {
        MESSAGE_ONLY
    }

    @Data
    private class ExceptionMessage {
        private String message;
        private String statusReason;
        private int statusCode;
        private String timestamp;
        private String url;

    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ExceptionMessage handleNoSuchElementException(
            NoSuchElementException ex, WebRequest webRequest) {
        return buildExceptionMessage(ex, HttpStatus.NOT_FOUND, webRequest,
                LogStatus.MESSAGE_ONLY);
    }

    private ExceptionMessage buildExceptionMessage(Exception ex,
                                                   HttpStatus status, WebRequest webRequest, LogStatus logStatus) {
        String message = ex.toString();
        String statusReason = status.getReasonPhrase();
        int statusCode = status.value();
        String uri = null;
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

        if (webRequest instanceof ServletWebRequest swr) {
            uri = swr.getRequest().getRequestURI();
        }

        if (logStatus == LogStatus.MESSAGE_ONLY) {
            log.error("Exception: {}", ex.toString());
        } else {
            log.error("Exception: ", ex);
        }

        ExceptionMessage excMsg = new ExceptionMessage();

        excMsg.setMessage(message);
        excMsg.setStatusCode(statusCode);
        excMsg.setStatusReason(statusReason);
        excMsg.setTimestamp(timestamp);
        excMsg.setUrl(uri);

        return excMsg;
    }
}