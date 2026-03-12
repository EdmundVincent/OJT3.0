package com.collaboportal.common.login.model;

public record LoginResult(
                boolean success,
                String code,
                String message,
                String userId,
                Byte role,
                String token) {

        public static LoginResult ok(
                        String id, Byte role) {
                return new LoginResult(true, "200", "Login success",
                                id, role, null);
        }

        public static LoginResult okWithRole(
                        String id, Byte role, String token) {
                return new LoginResult(true, "200", "Login success",
                                id, role, token);
        }

        public static LoginResult fail(String code, String msg) {
                return new LoginResult(false, code, msg,
                                null, null, null);
        }
}
