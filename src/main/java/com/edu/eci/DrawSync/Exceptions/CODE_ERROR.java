package com.edu.eci.DrawSync.Exceptions;

/**
 * Enumeration representing possible error codes for user-related operations.
 * <ul>
 *   <li>{@link #USER_ALREADY_EXISTS} - Indicates that the user already exists in the system.</li>
 *   <li>{@link #BAD_PASSWORD} - Indicates that the provided password is invalid or does not meet requirements.</li>
 *   <li>{@link #BAD_EMAIL} - Indicates that the provided email is invalid or improperly formatted.</li>
 *   <li>{@link #USER_NOT_FOUND} - Indicates that the user could not be found in the system.</li>
 *   <li>{@link #USER_ALREADY_CONFIMED} - Indicates that the user has already been confirmed.</li>
 * </ul>
 */
public enum CODE_ERROR {
    USER_ALREADY_EXISTS,
    BAD_PASSWORD, 
    BAD_EMAIL, 
    USER_NOT_FOUND, 
    USER_ALREADY_CONFIMED, LIMIT_EXCEEDED
}
