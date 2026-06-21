// Configuration des URLs de l'application
const APP_CONFIG = {
  BASE_URL: 'http://127.0.0.1:8080/ivoka-api/',
  API_BASE: 'http://127.0.0.1:8080/ivoka-api/api/',
};

export const getAppUrl = (path) => {
  // Pour les routes React internes, retourner le chemin sans le /ivoka-api/
  // Le routing React gérera la navigation
  return `/${path}`;
};

export const getApiUrl = (endpoint) => {
  return `${APP_CONFIG.API_BASE}${endpoint}`;
};

export default APP_CONFIG;