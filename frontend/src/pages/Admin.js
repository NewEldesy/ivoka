import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { useScrollAnimation } from '../hooks/useScrollAnimation';
import { productsAPI, messagesAPI, ordersAPI } from '../services/api';

const Admin = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, loading: authLoading } = useContext(AuthContext);
  const adminRef = useScrollAnimation();
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [messages, setMessages] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: 'tea',
    imageUrl: '',
    available: true
  });

  // Vérifier que l'utilisateur est admin
  useEffect(() => {
    if (!authLoading) {
      // Si pas authentifié, rediriger vers login
      if (!isAuthenticated) {
        navigate('/login');
        return;
      }
      
      // Si authentifié mais pas admin, rediriger vers accueil
      if (user && user.role !== 'admin') {
        navigate('/');
        return;
      }
    }
  }, [isAuthenticated, user, authLoading, navigate]);

  useEffect(() => {
    window.scrollTo(0, 0);
    if (activeTab === 'products') {
      fetchProducts();
    } else if (activeTab === 'messages') {
      fetchMessages();
    } else if (activeTab === 'orders') {
      fetchOrders();
    }
  }, [activeTab]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const data = await productsAPI.getAll();
      setProducts(data || []);
    } catch (err) {
      console.error('Erreur lors de la récupération des produits:', err);
      setError('Impossible de charger les produits');
    } finally {
      setLoading(false);
    }
  };

  const fetchMessages = async () => {
    try {
      setLoading(true);
      const data = await messagesAPI.getAll();
      setMessages(data || []);
    } catch (err) {
      console.error('Erreur lors de la récupération des messages:', err);
      setError('Impossible de charger les messages');
    } finally {
      setLoading(false);
    }
  };

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await ordersAPI.getAll();
      setOrders(data || []);
    } catch (err) {
      console.error('Erreur lors de la récupération des commandes:', err);
      setError('Impossible de charger les commandes');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validation
    if (!formData.name.trim() || !formData.price || !formData.category.trim()) {
      setError('Veuillez remplir tous les champs obligatoires');
      return;
    }

    if (isNaN(formData.price) || parseFloat(formData.price) <= 0) {
      setError('Le prix doit être un nombre positif');
      return;
    }

    try {
      setLoading(true);
      const response = await productsAPI.create(formData);
      
      if (response) {
        setSuccess(`Produit "${formData.name}" ajouté avec succès !`);
        setFormData({
          name: '',
          description: '',
          price: '',
          category: 'tea',
          imageUrl: '',
          available: true
        });
        fetchProducts();
      }
    } catch (err) {
      console.error('Erreur lors de l\'ajout du produit:', err);
      setError('Erreur lors de l\'ajout du produit: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      return;
    }

    try {
      setLoading(true);
      await productsAPI.delete(productId);
      setSuccess('Produit supprimé avec succès');
      fetchProducts();
    } catch (err) {
      console.error('Erreur lors de la suppression:', err);
      setError('Erreur lors de la suppression du produit');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkMessageAsRead = async (messageId) => {
    try {
      setLoading(true);
      setError('');
      setSuccess('');
      await messagesAPI.markAsRead(messageId);
      setSuccess('Message marqué comme lu ✓');
      // Recharger les messages après un délai court
      setTimeout(() => {
        fetchMessages();
      }, 300);
    } catch (err) {
      console.error('Erreur lors de la mise à jour:', err);
      setError('Erreur lors de la mise à jour du message: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteMessage = async (messageId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce message ?')) {
      return;
    }

    try {
      setLoading(true);
      await messagesAPI.delete(messageId);
      setSuccess('Message supprimé avec succès');
      fetchMessages();
    } catch (err) {
      console.error('Erreur lors de la suppression:', err);
      setError('Erreur lors de la suppression du message');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateOrderStatus = async (orderId, newStatus) => {
    try {
      setLoading(true);
      await ordersAPI.updateStatus(orderId, newStatus);
      setSuccess(`Statut de la commande mis à jour en: ${newStatus}`);
      fetchOrders();
    } catch (err) {
      console.error('Erreur lors de la mise à jour:', err);
      setError('Erreur lors de la mise à jour du statut');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir annuler cette commande ?')) {
      return;
    }

    try {
      setLoading(true);
      await ordersAPI.cancel(orderId);
      setSuccess('Commande annulée avec succès');
      fetchOrders();
    } catch (err) {
      console.error('Erreur lors de l\'annulation:', err);
      setError('Erreur lors de l\'annulation de la commande: ' + (err.response?.data?.error || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteOrder = async (orderId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette commande ?')) {
      return;
    }

    try {
      setLoading(true);
      await ordersAPI.delete(orderId);
      setSuccess('Commande supprimée avec succès');
      fetchOrders();
    } catch (err) {
      console.error('Erreur lors de la suppression:', err);
      setError('Erreur lors de la suppression de la commande');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="Admin">
      {/* Protection Admin - Vérification de l'authentification et du rôle */}
      {authLoading ? (
        // Écran de chargement pendant la vérification de l'auth
        <section className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-green-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Vérification des permissions...</p>
          </div>
        </section>
      ) : !isAuthenticated ? (
        // Pas authentifié
        <section className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="text-center">
            <div className="text-6xl mb-4">🔒</div>
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Accès Refusé</h1>
            <p className="text-gray-600 mb-6">Veuillez vous connecter pour accéder au panneau d'administration.</p>
            <a 
              href="/login"
              className="inline-block px-6 py-3 bg-green-600 text-white rounded-full hover:bg-green-700 font-medium transition-colors"
            >
              Se Connecter
            </a>
          </div>
        </section>
      ) : user && user.role !== 'admin' ? (
        // Connecté mais pas admin
        <section className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="text-center">
            <div className="text-6xl mb-4">⛔</div>
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Accès Interdit</h1>
            <p className="text-gray-600 mb-2">Vous n'avez pas les permissions nécessaires pour accéder à cette page.</p>
            <p className="text-gray-500 text-sm mb-6">Seuls les administrateurs peuvent accéder au panneau d'administration.</p>
            <a 
              href="/"
              className="inline-block px-6 py-3 bg-blue-600 text-white rounded-full hover:bg-blue-700 font-medium transition-colors"
            >
              Retour à l'Accueil
            </a>
          </div>
        </section>
      ) : (
        // Utilisateur est admin - afficher le contenu
        <>
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={adminRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6 fade-in-up visible">
            Panneau <span className="gradient-text">Admin</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
            Gérez votre catalogue de produits IVOKA et les messages clients.
          </p>
        </div>
      </section>

      {/* Tabs Navigation */}
      <section className="py-8 bg-gray-50 border-b border-gray-200">
        <div className="max-w-6xl mx-auto px-6">
          <div className="flex gap-4">
            <button
              onClick={() => setActiveTab('products')}
              className={`px-6 py-3 rounded-lg font-medium transition-colors ${
                activeTab === 'products'
                  ? 'bg-green-600 text-white'
                  : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-100'
              }`}
            >
              📦 Produits
            </button>
            <button
              onClick={() => setActiveTab('messages')}
              className={`px-6 py-3 rounded-lg font-medium transition-colors ${
                activeTab === 'messages'
                  ? 'bg-green-600 text-white'
                  : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-100'
              }`}
            >
              💬 Messages ({messages.length})
            </button>
            <button
              onClick={() => setActiveTab('orders')}
              className={`px-6 py-3 rounded-lg font-medium transition-colors ${
                activeTab === 'orders'
                  ? 'bg-green-600 text-white'
                  : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-100'
              }`}
            >
              🛒 Commandes ({orders.length})
            </button>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="py-20 bg-white">
        <div className="max-w-6xl mx-auto px-6">
          {/* Products Tab */}
          {activeTab === 'products' && (
            <div className="grid md:grid-cols-3 gap-8">
              {/* Add Product Form */}
              <div className="md:col-span-1">
                <div className="bg-gray-50 rounded-2xl p-8 sticky top-20">
                <h2 className="font-display text-2xl font-bold mb-6">Ajouter un produit</h2>
                
                {error && (
                  <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-4">
                    {error}
                  </div>
                )}

                {success && (
                  <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg mb-4">
                    {success}
                  </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Nom du produit *
                    </label>
                    <input
                      type="text"
                      name="name"
                      value={formData.name}
                      onChange={handleInputChange}
                      placeholder="Ex: Thé Vert Bio"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-600"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Description
                    </label>
                    <textarea
                      name="description"
                      value={formData.description}
                      onChange={handleInputChange}
                      placeholder="Décrivez le produit..."
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-600"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Prix (€) *
                    </label>
                    <input
                      type="number"
                      name="price"
                      value={formData.price}
                      onChange={handleInputChange}
                      placeholder="12.90"
                      step="0.01"
                      min="0"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-600"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Catégorie *
                    </label>
                    <select
                      name="category"
                      value={formData.category}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-600"
                      required
                    >
                      <option value="tea">Thé</option>
                      <option value="avocado_oil">Huile d'Avocat</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      URL de l'image
                    </label>
                    <input
                      type="text"
                      name="imageUrl"
                      value={formData.imageUrl}
                      onChange={handleInputChange}
                      placeholder="/resources/product.png"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-600"
                    />
                  </div>

                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      name="available"
                      checked={formData.available}
                      onChange={handleInputChange}
                      className="w-4 h-4 text-green-600 rounded focus:ring-2 focus:ring-green-600"
                    />
                    <label className="ml-2 text-sm text-gray-700">Disponible</label>
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full btn-primary py-3 rounded-lg font-semibold hover:opacity-90 disabled:opacity-50"
                  >
                    {loading ? 'Ajout en cours...' : 'Ajouter le produit'}
                  </button>
                </form>
              </div>
            </div>

            {/* Products List */}
            <div className="md:col-span-2">
              <h2 className="font-display text-2xl font-bold mb-6">Liste des produits ({products.length})</h2>

              {loading && products.length === 0 ? (
                <div className="text-center py-12">
                  <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
                  <p className="mt-4 text-gray-600">Chargement des produits...</p>
                </div>
              ) : products.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-8 text-center">
                  <p className="text-gray-500">Aucun produit pour le moment</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {products.map((product) => (
                    <div key={product.id} className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow">
                      <div className="flex justify-between items-start mb-3">
                        <div className="flex-1">
                          <h3 className="font-display text-lg font-semibold text-gray-800">{product.name}</h3>
                          <p className="text-sm text-gray-500">{product.category}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-2xl font-bold gradient-text">{product.price}€</p>
                          <span className={`text-xs px-2 py-1 rounded-full ${
                            product.available 
                              ? 'bg-green-100 text-green-700' 
                              : 'bg-red-100 text-red-700'
                          }`}>
                            {product.available ? 'Disponible' : 'Indisponible'}
                          </span>
                        </div>
                      </div>

                      {product.description && (
                        <p className="text-sm text-gray-600 mb-4 line-clamp-2">{product.description}</p>
                      )}

                      <div className="flex gap-2">
                        <button
                          onClick={() => handleDeleteProduct(product.id)}
                          className="flex-1 px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors font-medium"
                        >
                          Supprimer
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
            </div>
          )}

          {/* Messages Tab */}
          {activeTab === 'messages' && (
            <div className="space-y-6">
              <h2 className="font-display text-2xl font-bold mb-6">Gestion des messages ({messages.length})</h2>

              {loading && messages.length === 0 ? (
                <div className="text-center py-12">
                  <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
                  <p className="mt-4 text-gray-600">Chargement des messages...</p>
                </div>
              ) : messages.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-8 text-center">
                  <p className="text-gray-500">Aucun message pour le moment</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {messages.map((message) => (
                    <div 
                      key={message.id} 
                      className={`border rounded-lg p-6 transition-colors ${
                        message.read 
                          ? 'bg-gray-50 border-gray-200' 
                          : 'bg-blue-50 border-blue-200'
                      }`}
                    >
                      <div className="flex justify-between items-start mb-3">
                        <div className="flex-1">
                          <div className="flex items-center gap-3">
                            <h3 className="font-display text-lg font-semibold text-gray-800">{message.subject}</h3>
                            {!message.read && (
                              <span className="bg-blue-600 text-white text-xs px-2 py-1 rounded-full">Non lu</span>
                            )}
                          </div>
                          <p className="text-sm text-gray-600 mt-1">De: {message.senderEmail}</p>
                        </div>
                        <p className="text-xs text-gray-500">{new Date(message.createdAt).toLocaleDateString('fr-FR')}</p>
                      </div>

                      <p className="text-gray-700 mb-4">{message.message}</p>

                      <div className="flex gap-2">
                        {!message.read && (
                          <button
                            onClick={() => handleMarkMessageAsRead(message.id)}
                            className="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors font-medium text-sm"
                          >
                            Marquer comme lu
                          </button>
                        )}
                        <button
                          onClick={() => handleDeleteMessage(message.id)}
                          className="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors font-medium text-sm"
                        >
                          Supprimer
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Orders Tab */}
          {activeTab === 'orders' && (
            <div className="space-y-6">
              <h2 className="font-display text-2xl font-bold mb-6">Gestion des commandes ({orders.length})</h2>

              {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-4">
                  {error}
                </div>
              )}

              {success && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg mb-4">
                  {success}
                </div>
              )}

              {loading && orders.length === 0 ? (
                <div className="text-center py-12">
                  <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
                  <p className="mt-4 text-gray-600">Chargement des commandes...</p>
                </div>
              ) : orders.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-8 text-center">
                  <p className="text-gray-500">Aucune commande pour le moment</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {orders.map((order) => (
                    <div 
                      key={order.id} 
                      className="border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow"
                    >
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <h3 className="font-display text-lg font-semibold text-gray-800">
                            Commande #{order.orderNumber}
                          </h3>
                          <p className="text-sm text-gray-600 mt-1">Client ID: {order.userId}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-2xl font-bold gradient-text">{order.totalAmount}€</p>
                          <span className={`text-xs px-3 py-1 rounded-full font-medium ${
                            order.status === 'delivered' ? 'bg-green-100 text-green-700' :
                            order.status === 'shipped' ? 'bg-blue-100 text-blue-700' :
                            order.status === 'confirmed' ? 'bg-yellow-100 text-yellow-700' :
                            order.status === 'cancelled' ? 'bg-red-100 text-red-700' :
                            'bg-gray-100 text-gray-700'
                          }`}>
                            {order.statusDisplay}
                          </span>
                        </div>
                      </div>

                      <div className="mb-4 pb-4 border-b border-gray-200">
                        <p className="text-xs text-gray-500 mb-2">
                          Créée le {new Date(order.createdAt).toLocaleDateString('fr-FR')}
                        </p>
                        <div className="bg-gray-50 rounded p-3 text-sm">
                          <p><strong>Adresse de livraison:</strong></p>
                          <p className="text-gray-600 whitespace-pre-wrap">{order.shippingAddress}</p>
                        </div>
                      </div>

                      <div className="mb-4">
                        <p className="font-semibold mb-2">Articles ({order.items.length})</p>
                        <div className="space-y-2">
                          {order.items.map(item => (
                            <div key={item.id} className="flex justify-between text-sm bg-gray-50 p-2 rounded">
                              <span>{item.productName} x{item.quantity}</span>
                              <span>{item.totalPrice}€</span>
                            </div>
                          ))}
                        </div>
                      </div>

                      <div className="flex flex-wrap gap-2">
                        {order.status === 'pending' && (
                          <>
                            <button
                              onClick={() => handleUpdateOrderStatus(order.id, 'confirmed')}
                              className="px-4 py-2 bg-yellow-100 text-yellow-700 rounded-lg hover:bg-yellow-200 transition-colors font-medium text-sm"
                            >
                              Confirmer
                            </button>
                          </>
                        )}
                        {order.status === 'confirmed' && (
                          <button
                            onClick={() => handleUpdateOrderStatus(order.id, 'shipped')}
                            className="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors font-medium text-sm"
                          >
                            Marquer comme expédiée
                          </button>
                        )}
                        {order.status === 'shipped' && (
                          <button
                            onClick={() => handleUpdateOrderStatus(order.id, 'delivered')}
                            className="px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors font-medium text-sm"
                          >
                            Marquer comme livrée
                          </button>
                        )}
                        {order.canBeCancelled && (
                          <button
                            onClick={() => handleCancelOrder(order.id)}
                            className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg hover:bg-orange-200 transition-colors font-medium text-sm"
                          >
                            Annuler
                          </button>
                        )}
                        <button
                          onClick={() => handleDeleteOrder(order.id)}
                          className="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors font-medium text-sm"
                        >
                          Supprimer
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </section>
        </>
      )}
    </div>
  );
};

export default Admin;
