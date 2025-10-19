package com.edu.eci.DrawSync.auth.model;

/**
 * Enumeration representing the various states a user account can be in within the DrawSync application.
 * This enum is used to track user authentication and account status throughout the user lifecycle.
 * 
 * <p>The possible user statuses are:</p>
 * <ul>
 *   <li>{@link #UNCONFIRMED} - User account has been created but email/phone verification is pending</li>
 *   <li>{@link #CONFIRMED} - User account has been verified and is active</li>
 *   <li>{@link #EXTERNAL_PROVIDER} - User authenticated through an external identity provider (e.g., Google, Facebook)</li>
 *   <li>{@link #RESET_REQUIRED} - User account requires a password reset before proceeding</li>
 *   <li>{@link #FORCE_CHANGE_PASSWORD} - User must change their password on next login</li>
 * </ul>
 * 
 * @author DrawSync Development Team
 * @version 1.0
 * @since 1.0
 */
public enum UserStatus {
    UNCONFIRMED,
    CONFIRMED,
    EXTERNAL_PROVIDER,
    RESET_REQUIRED,
    FORCE_CHANGE_PASSWORD

}
