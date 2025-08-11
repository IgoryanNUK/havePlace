package vk.haveplace.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class EmptyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence c) {
        return c.toString();
    }
    @Override
    public boolean matches(CharSequence c, String s) {
        return c.toString().equals(s);
    }
}
