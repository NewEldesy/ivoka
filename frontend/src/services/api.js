import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/ivoka-api/api';

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
    const response = await api.get(`/products?category=${category}`);
    return response.data;
  },

  create: async (productData) => {
    const response = await api.post('/products', productData);
    return response.data;
  },

  update: async (id, productData) => {
    const response = await api.put(`/products/${id}`, productData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/products/${id}`);
    return response.data;
  }
};

// Messages API
export const messagesAPI = {
  getAll: async () => {
    const response = await api.get('/messages');
    return response.data;
  },

  send: async (messageData) => {
    const response = await api.post('/messages', messageData);
    return response.data;
  },

  markAsRead: async (messageId) => {
    const response = await api.put(`/messages/${messageId}`, { read: true });
    return response.data;
  },

  delete: async (messageId) => {
    const response = await api.delete(`/messages/${messageId}`);
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

// Cart API
export const cartAPI = {
  getCart: async (sessionToken) => {
    try {
      const response = await api.get(`/cart?sessionToken=${sessionToken}`);
      return response.data;
    } catch (error) {
      // Si l'API n'existe pas, retourner un panier vide
      return { items: [], totalAmount: 0 };
    }
  },

  addToCart: async (productId, quantity, sessionToken) => {
    const response = await api.post('/cart/add', {
      productId,
      quantity,
      sessionToken
    });
    return response.data;
  },

  updateQuantity: async (cartItemId, quantity, sessionToken) => {
    const response = await api.put('/cart/update', {
      cartItemId,
      quantity,
      sessionToken
    });
    return response.data;
  },

  removeFromCart: async (cartItemId, sessionToken) => {
    const response = await api.delete(`/cart/${cartItemId}`, {
      data: { sessionToken }
    });
    return response.data;
  },

  clearCart: async (cartId, sessionToken) => {
    const response = await api.delete(`/cart`, {
      data: { cartId, sessionToken }
    });
    return response.data;
  }
};

// Orders API
export const ordersAPI = {
  getAll: async () => {
    const response = await api.get('/orders');
    return response.data;
  },

  getById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },

  getByUserId: async (userId) => {
    const response = await api.get(`/orders?userId=${userId}`);
    return response.data;
  },

  getByStatus: async (status) => {
    const response = await api.get(`/orders?status=${status}`);
    return response.data;
  },

  create: async (orderData) => {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  updateStatus: async (orderId, status) => {
    const response = await api.put(`/orders/${orderId}`, { status });
    return response.data;
  },

  update: async (orderId, orderData) => {
    const response = await api.put(`/orders/${orderId}`, orderData);
    return response.data;
  },

  cancel: async (orderId) => {
    const response = await api.delete(`/orders/${orderId}?action=cancel`);
    return response.data;
  },

  delete: async (orderId) => {
    const response = await api.delete(`/orders/${orderId}`);
    return response.data;
  }
};

// Auth API
export const authAPI = {
  register: async (userData) => {
    const response = await api.post('/auth', {
      action: 'register',
      ...userData
    });
    return response.data;
  },

  login: async (email, password) => {
    const response = await api.post('/auth', {
      action: 'login',
      email,
      password
    });
    return response.data;
  },

  logout: async (token) => {
    const response = await api.post('/auth', {
      action: 'logout',
      token
    });
    return response.data;
  }
};

export default api;