import React, { useContext, useState } from 'react';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import { ordersAPI } from '../services/api';

const Cart = ({ isOpen, onClose }) => {
  const { cart, removeFromCart, updateQuantity, clearCart, getCartTotal } = useContext(CartContext);
  const { user } = useContext(AuthContext);
  const [isLoading, setIsLoading] = useState(false);
  const [showCheckoutForm, setShowCheckoutForm] = useState(false);
  const [formData, setFormData] = useState({
    shippingAddress: '',
    billingAddress: ''
  });

  const handleCheckout = async () => {
    if (!cart || cart.length === 0) {
      alert('Votre panier est vide');
      return;
    }

    // Demander les informations de livraison
    setShowCheckoutForm(true);
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmitOrder = async () => {
    if (!formData.shippingAddress.trim()) {
      alert('Veuillez entrer une adresse de livraison');
      return;
    }

    setIsLoading(true);
    try {
      // Préparer les données de la commande
      const items = cart.map(item => ({
        id: item.id,
        name: item.name,
        price: String(item.price),
        quantity: item.quantity,
        imageUrl: item.imageUrl || ''
      }));

      const orderData = {
        userId: user?.id || 0, // Utiliser l'ID utilisateur ou 0 pour commande anonyme
        totalAmount: String(getCartTotal()),
        shippingAddress: formData.shippingAddress.trim(),
        billingAddress: formData.billingAddress.trim() || formData.shippingAddress.trim(),
        items: items
      };

      console.log('Sending order data:', JSON.stringify(orderData, null, 2));

      const response = await ordersAPI.create(orderData);
      
      console.log('Order response:', response);

      if (response && response.id) {
        alert(`Commande créée avec succès ! Numéro : ${response.orderNumber}`);
        clearCart();
        setShowCheckoutForm(false);
        setFormData({ shippingAddress: '', billingAddress: '' });
        onClose();
      } else {
        alert('Erreur lors de la création de la commande: pas d\'ID retourné');
      }
    } catch (error) {
      console.error('Erreur lors de la commande:', error);
      const errorMsg = error.response?.data?.message || error.response?.data?.error || error.message;
      alert('Erreur lors de la création de la commande: ' + errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[9999] p-4">
      <div className="bg-white rounded-2xl p-8 max-w-2xl w-full max-h-[85vh] overflow-y-auto shadow-2xl border-2 border-gray-100">
        <div className="flex justify-between items-center mb-6">
          <h2 className="font-display text-2xl font-bold">Votre Panier</h2>
          <button 
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 text-2xl"
          >
            ×
          </button>
        </div>

        {!cart || cart.length === 0 ? (
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
              {cart.map((item) => (
                <div key={item.id} className="flex items-center space-x-4 p-4 border border-gray-200 rounded-lg">
                  <img
                    src={item.imageUrl || './resources/product.png'}
                    alt={item.name}
                    className="w-16 h-16 object-cover rounded-lg"
                  />
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg">{item.name}</h3>
                    <p className="text-gray-600">{item.price}€</p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                      className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300"
                    >
                      -
                    </button>
                    <span className="w-8 text-center font-medium">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300"
                    >
                      +
                    </button>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-lg">
                      {(parseFloat(item.price) * item.quantity).toFixed(2)}€
                    </p>
                    <button
                      onClick={() => removeFromCart(item.id)}
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
                <span className="text-2xl font-bold gradient-text">{getCartTotal()}€</span>
              </div>
              
              <div className="flex space-x-4">
                <button
                  onClick={() => {
                    if (window.confirm('Voulez-vous vraiment vider votre panier ?')) {
                      clearCart();
                    }
                  }}
                  className="flex-1 border-2 border-gray-300 text-gray-700 py-3 rounded-full font-medium hover:border-gray-400 transition-colors"
                >
                  Vider le panier
                </button>
                <button
                  onClick={() => {
                    if (showCheckoutForm) {
                      setShowCheckoutForm(false);
                    } else {
                      handleCheckout();
                    }
                  }}
                  className="flex-1 btn-primary py-3 rounded-full font-medium"
                  disabled={isLoading}
                >
                  {showCheckoutForm ? 'Annuler' : 'Commander'}
                </button>
              </div>

              {showCheckoutForm && (
                <div className="mt-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
                  <h3 className="font-semibold mb-4 text-lg">Informations de livraison</h3>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Adresse de livraison *
                      </label>
                      <textarea
                        name="shippingAddress"
                        value={formData.shippingAddress}
                        onChange={handleFormChange}
                        placeholder="Entrez votre adresse de livraison"
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                        rows="3"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Adresse de facturation (optionnel)
                      </label>
                      <textarea
                        name="billingAddress"
                        value={formData.billingAddress}
                        onChange={handleFormChange}
                        placeholder="Laissez vide si identique à l'adresse de livraison"
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                        rows="3"
                      />
                    </div>

                    <button
                      onClick={handleSubmitOrder}
                      disabled={isLoading}
                      className="w-full btn-primary py-3 rounded-full font-medium mt-4 disabled:opacity-50"
                    >
                      {isLoading ? 'Traitement...' : 'Valider la commande'}
                    </button>
                  </div>
                </div>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default Cart;
