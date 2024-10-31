package hey.io.heyscheduler.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 정보")
public class UserRequest {

    @Schema(description = "사용자 ID", defaultValue = "admin")
    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;

    @Schema(description = "비밀번호", defaultValue = "1", format = "password")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}