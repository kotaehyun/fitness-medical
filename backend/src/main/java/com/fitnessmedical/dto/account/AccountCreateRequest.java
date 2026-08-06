package com.fitnessmedical.dto.account;

import com.fitnessmedical.entity.AccountRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record AccountCreateRequest(


        @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 50, message = "로그인 아이디는 4자 이상 50자 이하여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "로그인 아이디는 영문, 숫자, 밑줄만 사용할 수 있습니다."
        )
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        String password,

        @NotBlank(message = "표시 이름은 필수입니다.")
        @Size(max = 30, message = "표시 이름은 30자 이하여야 합니다.")
        String displayName,

        @NotNull(message = "계정 역할은 필수입니다.")
        AccountRole role
) {

}
