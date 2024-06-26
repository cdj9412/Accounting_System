package com.sparta.common;

public interface ResponseCode {
    String SUCCESS = "SU";

    String VALIDATION_FAILED = "VF";
    String DUPLICATE_ID = "DI";

    String SIGN_IN_FAILED = "SF";

    String DATABASE_ERROR = "DBE";

    String JWT_ERROR = "JE";

    String LOGOUT_ERROR = "LE";
    String DO_NOT_EXIST_USER = "NEU";
}
