package com.beyond.ordersystem.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResetPasswordReqDto {
    private String email;
    private String asIsPassword;
    private String toBePassword;
}
