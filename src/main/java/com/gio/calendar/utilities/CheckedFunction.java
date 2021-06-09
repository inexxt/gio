package com.gio.calendar.utilities;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

/**
 * Helper interface for functions that can throw MessagingException
 * @param <T> - input type
 * @param <R> - output type
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws MessagingException;
}

