package itcast.user.dto.request;
import itcast.domain.user.User;
import itcast.domain.user.enums.ArticleType;
import itcast.domain.user.enums.Interest;
import itcast.domain.user.enums.SendingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileCreateRequest(
	@NotBlank(message = "닉네임을 입력해주세요.")
	String nickname,
	@NotNull(message = "기사 타입을 선택해주세요.")
	ArticleType articleType,
	@NotNull(message = "관심분야를 선택해주세요.")
	Interest interest,
	@NotNull(message = "발송 타입을 선택해주세요.")
	SendingType sendingType,
	@NotBlank(message = "이메일을 입력해주세요.")
	String email
) {
	public User toEntity(User existingUser) {
		return User.builder()
			.id(existingUser.getId()) // 기존 ID 유지
			.kakaoEmail(existingUser.getKakaoEmail()) // 기존 카카오 이메일 유지
			.nickname(nickname)
			.articleType(articleType)
			.interest(interest)
			.sendingType(sendingType)
			.email(email)
			.build();
	}
}