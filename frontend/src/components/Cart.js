import React, { useState, useEffect } from 'react';
import { cartAPI } from '../services/api';

const Cart = ({ isOpen, onClose, sessionToken }) => {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen) {
      fetchCart();
    }
  }, [isOpen, sessionToken]);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const data = await cartAPI.getCart(sessionToken);
      setCart(data);
    } catch (err) {
      setError('Erreur lors du chargement du panier');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productId, quantity = 1) => {
    try {
      await cartAPI.addToCart(productId, quantity, sessionToken);
      fetchCart();
    } catch (err) {
      setError('Erreur lors de l\'ajout au panier');
    }
  };

  const handleUpdateQuantity = async (itemId, quantity) => {
    try {
      await cartAPI.updateQuantity(itemId, quantity, sessionToken);
      fetchCart();
    } catch (err) {
      setError('Erreur lors de la mise à jour');
    }
  };

  const handleRemoveFromCart = async (itemId) => {
    try {
      await cartAPI.removeFromCart(itemId, sessionToken);
      fetchCart();
    } catch (err) {
      setError('Erreur lors de la suppression');
    }
  };

  const handleClearCart = async () => {
    if (cart && window.confirm('Voulez-vous vraiment vider votre panier ?')) {
      try {
        await cartAPI.clearCart(cart.id, sessionToken);
        fetchCart();
      } catch (err) {
        setError('Erreur lors du vidage du panier');
      }
    }
  };

  const handleCheckout = () => {
    if (!sessionToken) {
      alert('Veuillez vous connecter pour passer commande');
      return;
    }
    // Rediriger vers la page de commande
    window.location.href = '/checkout';
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl p-6 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h2 className="font-display text-2xl font-bold">Votre Panier</h2>
          <button 
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 text-2xl"
          >
            ×
          </button>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {loading ? (
          <div className="text-center py-8">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
            <p className="mt-4 text-gray-600">Chargement du panier...</p>
          </div>
        ) : !cart || !cart.items || cart.items.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">🛒</div>
            <p className="text-xl text-gray-600 mb-4">Votre panier est vide</p>
            <button
              onClick={onClose}
              className="btn-primary px-6 py-3 rounded-full font-medium"
            >
              Continuer vos achats
            </button>
          </div>
        ) : (
          <>
            <div className="space-y-4 mb-6">
              {cart.items.map((item) => (
                <div key={item.id} className="flex items-center space-x-4 p-4 border border-gray-200 rounded-lg">
                  <img
                    src={item.product.imageUrl}
                    alt={item.product.name}
                    className="w-16 h-16 object-cover rounded-lg"
                  />
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg">{item.product.name}</h3>
                    <p className="text-gray-600">{item.product.price}€</p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleUpdateQuantity(item.id, Math.max(1, item.quantity - 1))}
                      className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300"
                    >
                      -
                    </button>
                    <span className="w-8 text-center font-medium">{item.quantity}</span>
                    <button
                      onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                      className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300"
                    >
                      +
                    </button>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-lg">{item.totalPrice}€</p>
                    <button
                      onClick={() => handleRemoveFromCart(item.id)}
                      className="text-red-500 hover:text-red-700 text-sm"
                    >
                      Supprimer
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="border-t border-gray-200 pt-6">
              <div className="flex justify-between items-center mb-4">
                <span className="text-xl font-semibold">Total :</span>
                <span className="text-2xl font-bold gradient-text">{cart.totalAmount}€</span>
              </div>
              
              <div className="flex space-x-4">
                <button
                  onClick={handleClearCart}
                  className="flex-1 border-2 border-gray-300 text-gray-700 py-3 rounded-full font-medium hover:border-gray-400 transition-colors"
                >
                  Vider le panier
                </button>
                <button
                  onClick={handleCheckout}
                  className="flex-1 btn-primary py-3 rounded-full font-medium"
                >
                  Commander
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default Cart;