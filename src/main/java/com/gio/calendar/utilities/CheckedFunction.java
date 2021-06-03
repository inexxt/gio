package com.gio.calendar.utilities;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws MessagingException;
}

