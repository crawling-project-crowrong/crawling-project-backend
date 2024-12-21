package itcast.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {

    BLOG_NOT_FOUND("블로그가 유효하지 않습니다.", 1001L, HttpStatus.NOT_FOUND),
    NEWS_NOT_FOUND("뉴스가 유효하지 않습니다.", 2001L, HttpStatus.NOT_FOUND),
    //user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", 3001L, HttpStatus.NOT_FOUND),
    NICKNAME_ALREADY_EXISTS("이미 사용 중인 닉네임입니다.", 3002L, HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("이미 사용 중인 이메일입니다.", 3003L, HttpStatus.CONFLICT),
    PHONE_NUMBER_ALREADY_EXISTS("이미 등록된 전화번호입니다.", 3004L, HttpStatus.CONFLICT),
    INVALID_USER("접근할 수 없는 유저 입니다.", 3005L, HttpStatus.FORBIDDEN),
    //jwt
    JWT_TOKEN_CREATE_ERROR("JWT 토큰 생성에 실패했습니다.", 4001L, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN("유효하지 않거나 만료된 토큰입니다.", 4002L, HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("로그인이 필요한 기능입니다.", 4003L, HttpStatus.UNAUTHORIZED),
    //message
    MESSAGE_SENDING_FAILED("메시지 발송 실패", 4004L, HttpStatus.BAD_REQUEST),

    // Email
    EMAIL_SENDING_FAILED("메일 발송에 실패하였습니다.", 5001L, HttpStatus.BAD_REQUEST),

    // 뉴스 exception
    INVALID_NEWS_CONTENT("뉴스의 내용이 없습니다", 2002L, HttpStatus.BAD_REQUEST),
    INVALID_NEWS_DATE("출판 날짜 형식이 아닙니다", 2003L, HttpStatus.BAD_REQUEST),

    BLOG_CRAWLING_ERROR("블로그 크롤링에 실패했습니다", 2004L, HttpStatus.BAD_REQUEST),
    NEWS_CRAWLING_ERROR("뉴스 크롤링에 실패했습니다", 2004L, HttpStatus.BAD_REQUEST),
    BLOG_SELECT_ERROR("블로그 선택에 실패하였습니다.", 2004L, HttpStatus.BAD_REQUEST),
    NEWS_SELECT_ERROR("뉴스 선택에 실패하였습니다.", 2004L, HttpStatus.BAD_REQUEST),

    TODAY_NEWS_NOT_FOUND("뉴스 선택에 실패했습니다", 2005L, HttpStatus.NOT_FOUND),
    NOT_FOUND_EMAIL("이메일을 찾을 수 없습니다", 2006L, HttpStatus.NOT_FOUND),
    NOT_FOUND_SEND_DATA("발송 데이터를 찾을 수 없습니다", 2007L, HttpStatus.NOT_FOUND),
    INVALID_INTEREST_TYPE_ERROR("invalid타입이 맞지 않습니다", 2008L, HttpStatus.BAD_REQUEST),

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
