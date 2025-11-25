import api from './api';

export const authAPI = {
  // Inscription
  register: async (userData) => {
    const response = await api.post('/auth', {
      action: 'register',
      ...userData
    });
    return response.data;
  },

  // Connexion
  login: async (credentials) => {
    const response = await api.post('/auth', {
      action: 'login',
      ...credentials
    });
    return response.data;
  },

  // Déconnexion
  logout: async (sessionToken) => {
    const response = await api.post('/auth', {
      action: 'logout',
      sessionToken
    });
    return response.data;
  },

  // Validation de session
  validateSession: async (sessionToken) => {
    const response = await api.get('/auth/validate', {
      headers: {
        'Authorization': `Bearer ${sessionToken}`
      }
    });
    return response.data;
  }
};