package itcast.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import itcast.ResponseTemplate;
import itcast.user.application.UserService;
import itcast.user.dto.request.ProfileCreateRequest;
import itcast.user.dto.response.ProfileCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/{id}")
	public ResponseTemplate<ProfileCreateResponse> createProfile(
		@PathVariable Long id,
		@RequestBody @Valid ProfileCreateRequest request
	) {
		ProfileCreateResponse response = userService.createProfile(request, id);
		return new ResponseTemplate<>(HttpStatus.OK, "회원 정보 작성이 완료되었습니다.", response);
	}
}
