package itcast.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {

    BLOG_NOT_FOUND("블로그가 유효하지 않습니다.", 1001L, HttpStatus.NOT_FOUND),
    NEWS_NOT_FOUND("뉴스가 유효하지 않습니다.", 2001L, HttpStatus.NOT_FOUND),

    // 뉴스 exception
    INVALID_NEWS_CONTENT("뉴스의 내용이 없습니다",2002L,HttpStatus.BAD_REQUEST),
    INVALID_NEWS_DATE("출판 날짜 형식이 아닙니다",2003L ,HttpStatus.BAD_REQUEST),
    CRAWLING_PARSE_ERROR("크롤링에 실패했습니다",2004L,HttpStatus.BAD_REQUEST),

    BAD_REQUEST("BAD_REQUEST", 9404L, HttpStatus.BAD_REQUEST),
    BAD_REQUEST_JSON_PARSE_ERROR("[BAD_REQUEST] JSON_PARSE_ERROR - 올바른 JSON 형식이 아님", 9405L, HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 9999L, HttpStatus.INTERNAL_SERVER_ERROR);

    public final String message;
    public final Long code;
    public final HttpStatus status;

    ErrorCodes(final String message, final Long code, final HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
