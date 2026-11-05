package com.ivoka.api.config;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crée les comptes admin et user par défaut au démarrage si ils n'existent pas.
 * Les credentials sont configurables via les variables d'environnement dans .env.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${init.admin.email:admin@ivoka.fr}")
    private String adminEmail;

    @Value("${init.admin.password:Admin@ivoka2024}")
    private String adminPassword;

    @Value("${init.admin.firstName:Admin}")
    private String adminFirstName;

    @Value("${init.admin.lastName:IVOKA}")
    private String adminLastName;

    @Value("${init.user.email:user@ivoka.fr}")
    private String userEmail;

    @Value("${init.user.password:User@ivoka2024}")
    private String userPassword;

    @Value("${init.user.firstName:Utilisateur}")
    private String userFirstName;

    @Value("${init.user.lastName:Test}")
    private String userLastName;

    @Override
    public void run(String... args) {
        UserAuthDAO dao = new UserAuthDAO();

        createAccountIfMissing(dao, adminEmail, adminPassword, adminFirstName, adminLastName, "admin");
        createAccountIfMissing(dao, userEmail, userPassword, userFirstName, userLastName, "customer");
    }

    private void createAccountIfMissing(UserAuthDAO dao, String email, String password,
                                         String firstName, String lastName, String role) {
        try {
            if (dao.userExists(email)) {
                logger.info("Compte {} ({}) déjà existant, ignoré.", role, email);
                return;
            }

            String hash = PasswordUtils.hashPassword(password);

            UserAuth user = new UserAuth();
            user.setEmail(email);
            user.setPasswordHash(hash);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setPhone("");
            user.setNewsletter(false);

            boolean created = dao.createUser(user);
            if (created) {
                logger.info("Compte {} créé : {}", role, email);
            } else {
                logger.warn("Échec de la création du compte {} : {}", role, email);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création du compte {} ({}) : {}", role, email, e.getMessage());
        }
    }
}
