package itcast.user.exception;

public class NicknameAlreadyExistsException extends RuntimeException {
	public NicknameAlreadyExistsException(String message) {
		super(message);
	}
}
