import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { productsAPI, messagesAPI, ordersAPI } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';

const Admin = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout, loading: authLoading } = useContext(AuthContext);
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [messages, setMessages] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [modal, setModal] = useState(null); // { type, title, message, onConfirm }

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: 'tea',
    imageUrl: '',
    available: true
  });

  useEffect(() => {
    if (!authLoading) {
      if (!isAuthenticated) { navigate('/login'); return; }
      if (user && user.role !== 'admin') { navigate('/'); return; }
    }
  }, [isAuthenticated, user, authLoading, navigate]);

  useEffect(() => {
    window.scrollTo(0, 0);
    if (activeTab === 'products') fetchProducts();
    else if (activeTab === 'messages') fetchMessages();
    else if (activeTab === 'orders') fetchOrders();
  }, [activeTab]);

  const fetchProducts = async () => {
    try { setLoading(true); const data = await productsAPI.getAll(); setProducts(data || []); }
    catch (err) { setError('Impossible de charger les produits'); }
    finally { setLoading(false); }
  };

  const fetchMessages = async () => {
    try { setLoading(true); const data = await messagesAPI.getAll(); setMessages(data || []); }
    catch (err) { setError('Impossible de charger les messages'); }
    finally { setLoading(false); }
  };

  const fetchOrders = async () => {
    try { setLoading(true); const data = await ordersAPI.getAll(); setOrders(data || []); }
    catch (err) { setError('Impossible de charger les commandes'); }
    finally { setLoading(false); }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null); setSuccess(null);
    if (!formData.name.trim() || !formData.price || !formData.category.trim()) { setError('Veuillez remplir tous les champs obligatoires'); return; }
    if (isNaN(formData.price) || parseFloat(formData.price) <= 0) { setError('Le prix doit être un nombre positif'); return; }
    try {
      setLoading(true);
      const response = await productsAPI.create(formData);
      if (response) {
        setSuccess(`Produit "${formData.name}" ajouté avec succès !`);
        setFormData({ name: '', description: '', price: '', category: 'tea', imageUrl: '', available: true });
        setShowAddForm(false);
        fetchProducts();
      }
    } catch (err) { setError('Erreur lors de l\'ajout du produit: ' + (err.response?.data?.message || err.message)); }
    finally { setLoading(false); }
  };

  const handleDeleteProduct = (productId) => {
    setModal({
      type: 'confirm', title: 'Supprimer le produit',
      message: 'Cette action est irréversible. Êtes-vous sûr de vouloir supprimer ce produit ?',
      confirmLabel: 'Supprimer',
      onConfirm: async () => {
        setModal(null);
        try { setLoading(true); await productsAPI.delete(productId); setSuccess('Produit supprimé avec succès'); fetchProducts(); }
        catch (err) { setError('Erreur lors de la suppression du produit'); }
        finally { setLoading(false); }
      }
    });
  };

  const handleMarkMessageAsRead = async (messageId) => {
    try {
      setLoading(true); setError(''); setSuccess('');
      await messagesAPI.markAsRead(messageId);
      setSuccess('Message marqué comme lu ✓');
      setTimeout(() => fetchMessages(), 300);
    } catch (err) { setError('Erreur lors de la mise à jour du message: ' + (err.response?.data?.error || err.message)); }
    finally { setLoading(false); }
  };

  const handleDeleteMessage = (messageId) => {
    setModal({
      type: 'confirm', title: 'Supprimer le message',
      message: 'Êtes-vous sûr de vouloir supprimer ce message ?',
      confirmLabel: 'Supprimer',
      onConfirm: async () => {
        setModal(null);
        try { setLoading(true); await messagesAPI.delete(messageId); setSuccess('Message supprimé avec succès'); fetchMessages(); }
        catch (err) { setError('Erreur lors de la suppression du message'); }
        finally { setLoading(false); }
      }
    });
  };

  const handleUpdateOrderStatus = async (orderId, newStatus) => {
    try { setLoading(true); await ordersAPI.updateStatus(orderId, newStatus); setSuccess(`Statut mis à jour : ${newStatus}`); fetchOrders(); }
    catch (err) { setError('Erreur lors de la mise à jour du statut'); }
    finally { setLoading(false); }
  };

  const handleCancelOrder = (orderId) => {
    setModal({
      type: 'confirm', title: 'Annuler la commande',
      message: 'Êtes-vous sûr de vouloir annuler cette commande ?',
      confirmLabel: 'Annuler la commande',
      onConfirm: async () => {
        setModal(null);
        try { setLoading(true); await ordersAPI.cancel(orderId); setSuccess('Commande annulée avec succès'); fetchOrders(); }
        catch (err) { setError('Erreur lors de l\'annulation de la commande: ' + (err.response?.data?.error || err.message)); }
        finally { setLoading(false); }
      }
    });
  };

  const handleDeleteOrder = (orderId) => {
    setModal({
      type: 'confirm', title: 'Supprimer la commande',
      message: 'Cette action est irréversible. Êtes-vous sûr de vouloir supprimer cette commande ?',
      confirmLabel: 'Supprimer',
      onConfirm: async () => {
        setModal(null);
        try { setLoading(true); await ordersAPI.delete(orderId); setSuccess('Commande supprimée avec succès'); fetchOrders(); }
        catch (err) { setError('Erreur lors de la suppression de la commande'); }
        finally { setLoading(false); }
      }
    });
  };

  const unreadCount = messages.filter(m => !m.read).length;
  const pendingCount = orders.filter(o => o.status === 'pending').length;

  const tabLabels = { products: 'Produits', messages: 'Messages', orders: 'Commandes' };

  const StatusBadge = ({ status, display }) => {
    const styles = {
      delivered: 'bg-emerald-100 text-emerald-700 border border-emerald-200',
      shipped:   'bg-blue-100 text-blue-700 border border-blue-200',
      confirmed: 'bg-amber-100 text-amber-700 border border-amber-200',
      cancelled: 'bg-red-100 text-red-700 border border-red-200',
      pending:   'bg-gray-100 text-gray-600 border border-gray-200',
    };
    const icons = { delivered: '✓', shipped: '→', confirmed: '◎', cancelled: '✕', pending: '◷' };
    return (
      <span className={`inline-flex items-center gap-1 text-xs font-medium px-2.5 py-1 rounded-full ${styles[status] || styles.pending}`}>
        <span>{icons[status] || '◷'}</span>
        {display || status}
      </span>
    );
  };

  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#7D8471] mx-auto mb-4"></div>
          <p className="text-gray-500 text-sm">Vérification des permissions...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center bg-white rounded-2xl p-12 shadow-sm border border-gray-100 max-w-sm">
          <div className="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center text-3xl mx-auto mb-6">🔒</div>
          <h1 className="font-display text-2xl font-bold text-gray-900 mb-2">Accès Refusé</h1>
          <p className="text-gray-500 mb-8 text-sm">Veuillez vous connecter pour accéder au panneau d'administration.</p>
          <a href="/login" className="inline-block px-6 py-3 bg-[#7D8471] text-white rounded-xl hover:bg-[#6B7280] font-medium transition-colors text-sm">
            Se connecter
          </a>
        </div>
      </div>
    );
  }

  if (user && user.role !== 'admin') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center bg-white rounded-2xl p-12 shadow-sm border border-gray-100 max-w-sm">
          <div className="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center text-3xl mx-auto mb-6">⛔</div>
          <h1 className="font-display text-2xl font-bold text-gray-900 mb-2">Accès Interdit</h1>
          <p className="text-gray-500 mb-8 text-sm">Seuls les administrateurs peuvent accéder à cette page.</p>
          <a href="/" className="inline-block px-6 py-3 bg-[#2D3436] text-white rounded-xl hover:bg-[#3d4749] font-medium transition-colors text-sm">
            Retour à l'accueil
          </a>
        </div>
      </div>
    );
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
    <ConfirmModal
      isOpen={!!modal}
      type={modal?.type}
      title={modal?.title}
      message={modal?.message}
      confirmLabel={modal?.confirmLabel}
      onConfirm={modal?.onConfirm}
      onCancel={() => setModal(null)}
    />
    <div className="Admin">
      <div className="flex min-h-screen">

        {/* ── Sidebar ── */}
        <aside
          className="fixed left-0 w-56 flex flex-col z-30"
          style={{ top: '0', height: '100vh', background: '#2D3436' }}
        >
          {/* Brand */}
          <div className="px-5 py-5 border-b" style={{ borderColor: 'rgba(255,255,255,0.08)' }}>
            <p className="text-xs uppercase tracking-widest font-medium mb-0.5" style={{ color: '#7D8471' }}>Espace</p>
            <h2 className="font-display text-lg font-bold text-white leading-tight">Administration</h2>
          </div>

          {/* Nav */}
          <nav className="flex-1 px-3 py-5 space-y-1 overflow-y-auto">
            {[
              { id: 'products', label: 'Produits', badge: null,
                icon: (
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 10V7" />
                  </svg>
                )
              },
              { id: 'messages', label: 'Messages', badge: unreadCount > 0 ? unreadCount : null,
                icon: (
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                  </svg>
                )
              },
              { id: 'orders', label: 'Commandes', badge: pendingCount > 0 ? pendingCount : null,
                icon: (
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                )
              },
            ].map(item => (
              <button
                key={item.id}
                onClick={() => { setError(null); setSuccess(null); setActiveTab(item.id); }}
                className="w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-left transition-all group"
                style={{
                  background: activeTab === item.id ? '#7D8471' : 'transparent',
                  color: activeTab === item.id ? 'white' : 'rgba(255,255,255,0.55)',
                }}
                onMouseEnter={e => { if (activeTab !== item.id) e.currentTarget.style.background = 'rgba(255,255,255,0.07)'; e.currentTarget.style.color = 'white'; }}
                onMouseLeave={e => { if (activeTab !== item.id) { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'rgba(255,255,255,0.55)'; } }}
              >
                <span className="flex items-center gap-2.5">
                  {item.icon}
                  <span className="text-sm font-medium">{item.label}</span>
                </span>
                {item.badge && (
                  <span className="text-xs font-bold bg-blue-500 text-white w-5 h-5 rounded-full flex items-center justify-center">
                    {item.badge}
                  </span>
                )}
              </button>
            ))}
          </nav>

          {/* User info + logout */}
          <div className="px-4 py-4 border-t space-y-3" style={{ borderColor: 'rgba(255,255,255,0.08)' }}>
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-[#7D8471] flex items-center justify-center text-white text-xs font-bold uppercase flex-shrink-0">
                {user?.email?.charAt(0) || 'A'}
              </div>
              <div className="min-w-0">
                <p className="text-xs font-medium text-white truncate">{user?.email}</p>
                <p className="text-xs" style={{ color: '#7D8471' }}>Administrateur</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              className="w-full flex items-center gap-2 px-3 py-2 rounded-xl text-sm transition-colors"
              style={{ color: 'rgba(255,255,255,0.55)', background: 'transparent' }}
              onMouseEnter={e => { e.currentTarget.style.background = 'rgba(239,68,68,0.15)'; e.currentTarget.style.color = '#f87171'; }}
              onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'rgba(255,255,255,0.55)'; }}
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              <span className="font-medium">Déconnexion</span>
            </button>
          </div>
        </aside>

        {/* ── Main content ── */}
        <div className="flex-1 flex flex-col min-h-full" style={{ marginLeft: '224px', background: '#f8fafc' }}>

          {/* Top bar */}
          <div className="bg-white border-b border-gray-200 px-8 py-4 flex items-center justify-between sticky top-0 z-20">
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-wider font-medium">Admin /</p>
              <h1 className="font-display text-2xl font-bold text-gray-900 leading-tight">
                {tabLabels[activeTab]}
              </h1>
            </div>
            {activeTab === 'products' && (
              <button
                onClick={() => { setShowAddForm(v => !v); setError(null); setSuccess(null); }}
                className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium text-white transition-colors"
                style={{ background: showAddForm ? '#6B7280' : '#7D8471' }}
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={showAddForm ? "M6 18L18 6M6 6l12 12" : "M12 4v16m8-8H4"} />
                </svg>
                {showAddForm ? 'Annuler' : 'Ajouter un produit'}
              </button>
            )}
          </div>

          <div className="flex-1 p-8">

            {/* Stats cards */}
            <div className="grid grid-cols-3 gap-5 mb-8">
              {[
                {
                  label: 'Produits',
                  value: products.length,
                  sub: `${products.filter(p => p.available).length} disponibles`,
                  color: '#7D8471',
                  bg: 'rgba(125,132,113,0.08)',
                  icon: (
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 10V7" />
                    </svg>
                  ),
                },
                {
                  label: 'Messages',
                  value: messages.length,
                  sub: unreadCount > 0 ? `${unreadCount} non lu${unreadCount > 1 ? 's' : ''}` : 'Tous lus',
                  color: '#3B82F6',
                  bg: '#EFF6FF',
                  badgeColor: unreadCount > 0 ? '#3B82F6' : undefined,
                  icon: (
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                    </svg>
                  ),
                },
                {
                  label: 'Commandes',
                  value: orders.length,
                  sub: pendingCount > 0 ? `${pendingCount} en attente` : 'Aucune en attente',
                  color: '#F59E0B',
                  bg: '#FFFBEB',
                  icon: (
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                    </svg>
                  ),
                },
              ].map(stat => (
                <div
                  key={stat.label}
                  className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 cursor-pointer hover:shadow-md transition-shadow"
                  onClick={() => setActiveTab(stat.label === 'Produits' ? 'products' : stat.label === 'Messages' ? 'messages' : 'orders')}
                >
                  <div className="flex items-start justify-between mb-4">
                    <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: stat.bg, color: stat.color }}>
                      {stat.icon}
                    </div>
                  </div>
                  <p className="text-3xl font-bold text-gray-900 mb-1">{stat.value}</p>
                  <p className="text-sm font-medium text-gray-500">{stat.label}</p>
                  <p className="text-xs mt-1" style={{ color: stat.badgeColor || '#9CA3AF' }}>{stat.sub}</p>
                </div>
              ))}
            </div>

            {/* Alerts */}
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 flex items-center gap-3 text-sm">
                <svg className="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {error}
              </div>
            )}
            {success && (
              <div className="bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-xl mb-6 flex items-center gap-3 text-sm">
                <svg className="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {success}
              </div>
            )}

            {/* ── PRODUCTS TAB ── */}
            {activeTab === 'products' && (
              <div>
                {/* Add product form (collapsible) */}
                {showAddForm && (
                  <div className="bg-white rounded-2xl border border-gray-200 p-6 mb-6 shadow-sm">
                    <h2 className="font-display text-lg font-bold text-gray-900 mb-5">Nouveau produit</h2>
                    <form onSubmit={handleSubmit}>
                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">Nom *</label>
                          <input type="text" name="name" value={formData.name} onChange={handleInputChange}
                            placeholder="Ex: Thé Vert Bio"
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#7D8471] focus:ring-2 focus:ring-[#7D8471]/20 transition-all bg-gray-50 focus:bg-white"
                            required />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">Prix (€) *</label>
                          <input type="number" name="price" value={formData.price} onChange={handleInputChange}
                            placeholder="12.90" step="0.01" min="0"
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#7D8471] focus:ring-2 focus:ring-[#7D8471]/20 transition-all bg-gray-50 focus:bg-white"
                            required />
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">Catégorie *</label>
                          <select name="category" value={formData.category} onChange={handleInputChange}
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#7D8471] focus:ring-2 focus:ring-[#7D8471]/20 transition-all bg-gray-50 focus:bg-white"
                            required>
                            <option value="tea">Thé</option>
                            <option value="avocado_oil">Huile d'Avocat</option>
                          </select>
                        </div>
                        <div>
                          <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">URL de l'image</label>
                          <input type="text" name="imageUrl" value={formData.imageUrl} onChange={handleInputChange}
                            placeholder="/resources/product.png"
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#7D8471] focus:ring-2 focus:ring-[#7D8471]/20 transition-all bg-gray-50 focus:bg-white" />
                        </div>
                        <div className="col-span-2">
                          <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">Description</label>
                          <textarea name="description" value={formData.description} onChange={handleInputChange}
                            placeholder="Décrivez le produit..." rows="2"
                            className="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#7D8471] focus:ring-2 focus:ring-[#7D8471]/20 transition-all bg-gray-50 focus:bg-white resize-none" />
                        </div>
                      </div>
                      <div className="flex items-center justify-between">
                        <label className="flex items-center gap-2 cursor-pointer">
                          <input type="checkbox" name="available" checked={formData.available} onChange={handleInputChange}
                            className="w-4 h-4 rounded border-gray-300 text-[#7D8471] focus:ring-[#7D8471]" />
                          <span className="text-sm text-gray-600">Disponible à la vente</span>
                        </label>
                        <button type="submit" disabled={loading}
                          className="px-6 py-2.5 rounded-xl text-sm font-semibold text-white transition-all disabled:opacity-50 hover:opacity-90"
                          style={{ background: '#7D8471' }}>
                          {loading ? 'Ajout en cours...' : 'Ajouter le produit'}
                        </button>
                      </div>
                    </form>
                  </div>
                )}

                {/* Products list */}
                <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
                  <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                    <h2 className="font-semibold text-gray-900">Catalogue produits</h2>
                    <span className="text-sm text-gray-400">{products.length} produit{products.length !== 1 ? 's' : ''}</span>
                  </div>

                  {loading && products.length === 0 ? (
                    <div className="text-center py-16">
                      <div className="inline-block animate-spin rounded-full h-7 w-7 border-t-2 border-b-2 border-[#7D8471] mb-3"></div>
                      <p className="text-sm text-gray-400">Chargement...</p>
                    </div>
                  ) : products.length === 0 ? (
                    <div className="text-center py-16">
                      <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center mx-auto mb-3 text-xl">📦</div>
                      <p className="text-gray-400 text-sm">Aucun produit dans le catalogue</p>
                    </div>
                  ) : (
                    <div className="divide-y divide-gray-50">
                      {products.map(product => (
                        <div key={product.id} className="flex items-center gap-4 px-6 py-4 hover:bg-gray-50 transition-colors group">
                          {/* Image */}
                          <div className="w-12 h-12 rounded-xl overflow-hidden bg-gray-100 flex-shrink-0">
                            {product.imageUrl ? (
                              <img src={product.imageUrl.startsWith('/') ? `${process.env.PUBLIC_URL}${product.imageUrl}` : product.imageUrl}
                                alt={product.name} className="w-full h-full object-cover" />
                            ) : (
                              <div className="w-full h-full flex items-center justify-center text-gray-300 text-lg">🌿</div>
                            )}
                          </div>
                          {/* Info */}
                          <div className="flex-1 min-w-0">
                            <p className="font-semibold text-gray-900 text-sm truncate">{product.name}</p>
                            <p className="text-xs text-gray-400 mt-0.5">
                              {product.category === 'tea' ? 'Thé' : "Huile d'Avocat"}
                              {product.description && ` · ${product.description.substring(0, 50)}...`}
                            </p>
                          </div>
                          {/* Price + Status */}
                          <div className="text-right flex-shrink-0 mr-4">
                            <p className="font-bold text-gray-900">{product.price}€</p>
                            <span className={`text-xs font-medium px-2 py-0.5 rounded-full mt-1 inline-block ${
                              product.available ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-500'
                            }`}>
                              {product.available ? 'Disponible' : 'Indisponible'}
                            </span>
                          </div>
                          {/* Delete */}
                          <button
                            onClick={() => handleDeleteProduct(product.id)}
                            className="opacity-0 group-hover:opacity-100 transition-opacity p-2 rounded-lg hover:bg-red-50 text-gray-300 hover:text-red-500"
                            title="Supprimer"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* ── MESSAGES TAB ── */}
            {activeTab === 'messages' && (
              <div>
                {loading && messages.length === 0 ? (
                  <div className="text-center py-16">
                    <div className="inline-block animate-spin rounded-full h-7 w-7 border-t-2 border-b-2 border-[#7D8471] mb-3"></div>
                    <p className="text-sm text-gray-400">Chargement...</p>
                  </div>
                ) : messages.length === 0 ? (
                  <div className="bg-white rounded-2xl border border-gray-200 shadow-sm text-center py-16">
                    <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center mx-auto mb-3 text-xl">💬</div>
                    <p className="text-gray-400 text-sm">Aucun message pour le moment</p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {messages.map(message => (
                      <div
                        key={message.id}
                        className={`bg-white rounded-2xl border shadow-sm overflow-hidden transition-all ${
                          message.read ? 'border-gray-200' : 'border-blue-200'
                        }`}
                      >
                        {/* Unread indicator */}
                        {!message.read && <div className="h-1 bg-blue-500 w-full"></div>}
                        <div className="p-5">
                          <div className="flex items-start justify-between gap-4">
                            {/* Avatar + Info */}
                            <div className="flex items-start gap-3">
                              <div className="w-9 h-9 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-sm flex-shrink-0 mt-0.5">
                                {message.senderEmail?.charAt(0).toUpperCase() || '?'}
                              </div>
                              <div className="min-w-0">
                                <div className="flex items-center gap-2 flex-wrap">
                                  <h3 className="font-semibold text-gray-900 text-sm">{message.subject}</h3>
                                  {!message.read && (
                                    <span className="bg-blue-100 text-blue-600 text-xs font-medium px-2 py-0.5 rounded-full">Nouveau</span>
                                  )}
                                </div>
                                <p className="text-xs text-gray-400 mt-0.5">{message.senderEmail}</p>
                              </div>
                            </div>
                            <p className="text-xs text-gray-400 flex-shrink-0">
                              {new Date(message.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })}
                            </p>
                          </div>
                          <p className="text-sm text-gray-600 mt-3 leading-relaxed pl-12">{message.message}</p>
                          <div className="flex gap-2 mt-4 pl-12">
                            {!message.read && (
                              <button
                                onClick={() => handleMarkMessageAsRead(message.id)}
                                className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-lg text-xs font-medium transition-colors"
                              >
                                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                </svg>
                                Marquer comme lu
                              </button>
                            )}
                            <button
                              onClick={() => handleDeleteMessage(message.id)}
                              className="flex items-center gap-1.5 px-3 py-1.5 bg-red-50 text-red-500 hover:bg-red-100 rounded-lg text-xs font-medium transition-colors"
                            >
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                              Supprimer
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* ── ORDERS TAB ── */}
            {activeTab === 'orders' && (
              <div>
                {loading && orders.length === 0 ? (
                  <div className="text-center py-16">
                    <div className="inline-block animate-spin rounded-full h-7 w-7 border-t-2 border-b-2 border-[#7D8471] mb-3"></div>
                    <p className="text-sm text-gray-400">Chargement...</p>
                  </div>
                ) : orders.length === 0 ? (
                  <div className="bg-white rounded-2xl border border-gray-200 shadow-sm text-center py-16">
                    <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center mx-auto mb-3 text-xl">🛒</div>
                    <p className="text-gray-400 text-sm">Aucune commande pour le moment</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {orders.map(order => (
                      <div key={order.id} className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
                        {/* Header */}
                        <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between gap-4">
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-xl bg-gray-100 flex items-center justify-center">
                              <svg className="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                              </svg>
                            </div>
                            <div>
                              <p className="font-semibold text-gray-900 text-sm">#{order.orderNumber}</p>
                              <p className="text-xs text-gray-400">
                                {new Date(order.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })}
                              </p>
                            </div>
                          </div>
                          <div className="flex items-center gap-3">
                            <StatusBadge status={order.status} display={order.statusDisplay} />
                            <p className="font-bold text-gray-900">{order.totalAmount}€</p>
                          </div>
                        </div>

                        {/* Body */}
                        <div className="px-6 py-4">
                          <div className="grid grid-cols-2 gap-6">
                            {/* Items */}
                            <div>
                              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">
                                Articles ({order.items.length})
                              </p>
                              <div className="space-y-1.5">
                                {order.items.map(item => (
                                  <div key={item.id} className="flex justify-between items-center text-sm">
                                    <span className="text-gray-700">{item.productName} <span className="text-gray-400">×{item.quantity}</span></span>
                                    <span className="font-medium text-gray-900">{item.totalPrice}€</span>
                                  </div>
                                ))}
                              </div>
                            </div>
                            {/* Address */}
                            <div>
                              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">Livraison</p>
                              <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-wrap">{order.shippingAddress}</p>
                            </div>
                          </div>
                        </div>

                        {/* Actions */}
                        <div className="px-6 py-3 bg-gray-50 border-t border-gray-100 flex flex-wrap gap-2">
                          {order.status === 'pending' && (
                            <button onClick={() => handleUpdateOrderStatus(order.id, 'confirmed')}
                              className="flex items-center gap-1.5 px-3 py-1.5 bg-amber-50 text-amber-700 hover:bg-amber-100 rounded-lg text-xs font-medium transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                              </svg>
                              Confirmer
                            </button>
                          )}
                          {order.status === 'confirmed' && (
                            <button onClick={() => handleUpdateOrderStatus(order.id, 'shipped')}
                              className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-50 text-blue-700 hover:bg-blue-100 rounded-lg text-xs font-medium transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
                              </svg>
                              Marquer expédiée
                            </button>
                          )}
                          {order.status === 'shipped' && (
                            <button onClick={() => handleUpdateOrderStatus(order.id, 'delivered')}
                              className="flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded-lg text-xs font-medium transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                              </svg>
                              Marquer livrée
                            </button>
                          )}
                          {order.canBeCancelled && (
                            <button onClick={() => handleCancelOrder(order.id)}
                              className="flex items-center gap-1.5 px-3 py-1.5 bg-orange-50 text-orange-600 hover:bg-orange-100 rounded-lg text-xs font-medium transition-colors">
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                              </svg>
                              Annuler
                            </button>
                          )}
                          <button onClick={() => handleDeleteOrder(order.id)}
                            className="flex items-center gap-1.5 px-3 py-1.5 bg-red-50 text-red-500 hover:bg-red-100 rounded-lg text-xs font-medium transition-colors ml-auto">
                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
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
        </div>
      </div>
    </div>
    </>
  );
};

export default Admin;
