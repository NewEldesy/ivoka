import React, { createContext, useState, useCallback, useEffect } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [sessionToken, setSessionToken] = useState(() => {
    // Récupérer le token depuis localStorage si disponible
    return localStorage.getItem('sessionToken') || 'user_' + Date.now();
  });

  // Sauvegarder le token et le panier dans localStorage
  useEffect(() => {
    localStorage.setItem('sessionToken', sessionToken);
  }, [sessionToken]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  // Charger le panier depuis localStorage au démarrage
  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        setCart(JSON.parse(savedCart));
      } catch (e) {
        console.error('Erreur lors du chargement du panier:', e);
      }
    }
  }, []);

  const addToCart = useCallback((product, quantity = 1) => {
    setCart(prevCart => {
      // Vérifier si le produit est déjà dans le panier
      const existingItem = prevCart.find(item => item.id === product.id);
      
      if (existingItem) {
        // Augmenter la quantité
        return prevCart.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        );
      } else {
        // Ajouter le nouveau produit
        return [...prevCart, { ...product, quantity }];
      }
    });
  }, []);

  const removeFromCart = useCallback((productId) => {
    setCart(prevCart => prevCart.filter(item => item.id !== productId));
  }, []);

  const updateQuantity = useCallback((productId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(productId);
      return;
    }
    setCart(prevCart =>
      prevCart.map(item =>
        item.id === productId ? { ...item, quantity } : item
      )
    );
  }, [removeFromCart]);

  const clearCart = useCallback(() => {
    setCart([]);
  }, []);

  const getCartTotal = useCallback(() => {
    return cart.reduce((total, item) => {
      const price = typeof item.price === 'string' ? parseFloat(item.price) : item.price;
      return total + (price * item.quantity);
    }, 0).toFixed(2);
  }, [cart]);

  const getCartItemsCount = useCallback(() => {
    return cart.reduce((count, item) => count + item.quantity, 0);
  }, [cart]);

  const value = {
    cart,
    sessionToken,
    setSessionToken,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getCartTotal,
    getCartItemsCount
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};
