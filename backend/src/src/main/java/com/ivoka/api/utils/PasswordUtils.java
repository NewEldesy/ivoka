package com.ivoka.api.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtils {
    
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 10000;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    
    /**
     * Hache un mot de passe avec PBKDF2
     * @param password Le mot de passe en clair
     * @return Le hash du mot de passe avec le sel
     */
    public static String hashPassword(String password) {
        try {
            // Générer un sel aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hacher le mot de passe
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // Combiner le sel et le hash
            byte[] saltAndHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);
            
            // Encoder en Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }
    
    /**
     * Vérifie un mot de passe contre un hash
     * @param password Le mot de passe en clair
     * @param storedHash Le hash stocké
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Décoder le hash stocké
            byte[] saltAndHash = Base64.getDecoder().decode(storedHash);
            
            // Extraire le sel
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);
            
            // Extraire le hash
            byte[] originalHash = new byte[saltAndHash.length - SALT_LENGTH];
            System.arraycopy(saltAndHash, SALT_LENGTH, originalHash, 0, originalHash.length);
            
            // Hacher le mot de passe fourni avec le même sel
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] computedHash = factory.generateSecret(spec).getEncoded();
            
            // Comparer les hashes
            if (originalHash.length != computedHash.length) {
                return false;
            }
            
            for (int i = 0; i < originalHash.length; i++) {
                if (originalHash[i] != computedHash[i]) {
                    return false;
                }
            }
            
            return true;
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erreur lors de la vérification du mot de passe", e);
        }
    }
    
    /**
     * Génère un token de session aléatoire
     * @return Un token de session
     */
    public static String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[32];
        random.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
    
    /**
     * Valide la force d'un mot de passe
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est suffisamment fort, false sinon
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Vérifier la présence de différents types de caractères
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        
        // Au moins 3 types de caractères différents
        int charTypes = (hasUpperCase ? 1 : 0) + 
                       (hasLowerCase ? 1 : 0) + 
                       (hasDigit ? 1 : 0) + 
                       (hasSpecialChar ? 1 : 0);
        
        return charTypes >= 3;
    }
}