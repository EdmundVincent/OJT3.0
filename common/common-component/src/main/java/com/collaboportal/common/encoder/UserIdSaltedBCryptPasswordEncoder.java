package com.collaboportal.common.encoder;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserIdSaltedBCryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate;

    public UserIdSaltedBCryptPasswordEncoder(int strength) {
        this.delegate = new BCryptPasswordEncoder(strength, new SecureRandom());
    }

    @Override
    public String encode(CharSequence rawPassword) {
        throw new UnsupportedOperationException("PepperとしてUserIdが必要です");
    }

    public String encode(CharSequence rawPassword, String userId) {
        return delegate.encode(rawPassword + "@" + userId);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        throw new UnsupportedOperationException("PepperとしてUserIdが必要です");

    }

    public boolean matches(CharSequence rawPassword, String userId, String encodedPassword) {
        return delegate.matches(rawPassword + "@" + userId, encodedPassword);
    }

}
