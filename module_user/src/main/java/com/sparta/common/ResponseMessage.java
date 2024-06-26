package com.sparta.common;

public interface ResponseMessage {
    String SUCCESS = "성공.";

    String VALIDATION_FAILED = "입력 데이터 유효성 검사 오류.";
    String DUPLICATE_ID = "중복 아이디.";

    String SIGN_IN_FAILED = "Login 정보 불일치.";

    String DATABASE_ERROR = "Database 오류.";

    String JWT_ERROR = "JWT 오류.";

    String LOGOUT_ERROR = "로그인 이용자가 아님.";
    String DO_NOT_EXIST_USER = "이용자 존재하지 않음.";
}
