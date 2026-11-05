package com.ivoka.api.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    // --- hashPassword / verifyPassword ---

    @Test
    void hashPassword_producesNonNullBase64String() {
        String hash = PasswordUtils.hashPassword("SecurePass1!");
        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }

    @Test
    void verifyPassword_returnsTrueForCorrectPassword() {
        String password = "SecurePass1!";
        String hash = PasswordUtils.hashPassword(password);
        assertTrue(PasswordUtils.verifyPassword(password, hash));
    }

    @Test
    void verifyPassword_returnsFalseForWrongPassword() {
        String hash = PasswordUtils.hashPassword("SecurePass1!");
        assertFalse(PasswordUtils.verifyPassword("WrongPass1!", hash));
    }

    @Test
    void hashPassword_producesDifferentHashEachCall() {
        String h1 = PasswordUtils.hashPassword("SecurePass1!");
        String h2 = PasswordUtils.hashPassword("SecurePass1!");
        // Sel aléatoire → deux hashes distincts pour le même mot de passe
        assertNotEquals(h1, h2);
    }

    // --- isPasswordStrong ---

    @ParameterizedTest
    @ValueSource(strings = {"Admin@2024", "User1234!", "Abc12345#", "Passw0rd!"})
    void isPasswordStrong_returnsTrueForStrongPasswords(String password) {
        assertTrue(PasswordUtils.isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_returnsFalseForShortPassword() {
        assertFalse(PasswordUtils.isPasswordStrong("Ab1!"));
    }

    @Test
    void isPasswordStrong_returnsFalseForOnlyLowercase() {
        assertFalse(PasswordUtils.isPasswordStrong("onlylowercase"));
    }

    @Test
    void isPasswordStrong_returnsFalseForNullPassword() {
        assertFalse(PasswordUtils.isPasswordStrong(null));
    }

    @Test
    void isPasswordStrong_returnsFalseForOnlyTwoCharTypes() {
        // Minuscules + chiffres seulement = 2 types → rejeté
        assertFalse(PasswordUtils.isPasswordStrong("password123"));
    }
}
