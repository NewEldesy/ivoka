import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/NotFound.css';

function NotFound() {
  return (
    <div className="not-found-container">
      <div className="not-found-content">
        <h1 className="error-code">404</h1>
        <h2 className="error-title">Page Non Trouvée</h2>
        <p className="error-message">
          Désolé, la page que vous recherchez n'existe pas.
        </p>
        
        <div className="not-found-actions">
          <Link to="/" className="btn btn-primary">
            Retour à l'accueil
          </Link>
          <Link to="/products" className="btn btn-secondary">
            Voir les produits
          </Link>
        </div>
      </div>
    </div>
  );
}

export default NotFound;