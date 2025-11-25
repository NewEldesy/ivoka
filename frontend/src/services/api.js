import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Products API
export const productsAPI = {
  getAll: async () => {
    const response = await api.get('/products');
    return response.data;
  },
  
  getById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },
  
  getByCategory: async (category) => {
    const response = await api.get(`/products/category/${category}`);
    return response.data;
  }
};

// Messages API
export const messagesAPI = {
  send: async (messageData) => {
    const response = await api.post('/messages', messageData);
    return response.data;
  }
};

// Users API
export const usersAPI = {
  create: async (userData) => {
    const response = await api.post('/users', userData);
    return response.data;
  }
};

export default api;