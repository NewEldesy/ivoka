import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { useScrollAnimation } from '../hooks/useScrollAnimation';

const Login = () => {
  const loginRef = useScrollAnimation();
  const navigate = useNavigate();
  const { login, loading, error } = useContext(AuthContext);
  
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [localError, setLocalError] = useState(null);
  const [localLoading, setLocalLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError(null);
    setLocalLoading(true);

    if (!formData.email.trim() || !formData.password.trim()) {
      setLocalError('Veuillez remplir tous les champs');
      setLocalLoading(false);
      return;
    }

    try {
      await login(formData.email, formData.password);
      navigate('/');
    } catch (err) {
      setLocalError(err.response?.data?.error || 'Erreur de connexion');
    } finally {
      setLocalLoading(false);
    }
  };

  return (
    <div className="Login">
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={loginRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6 fade-in-up visible">
            Se <span className="gradient-text">Connecter</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
            Accédez à votre compte IVOKA
          </p>
        </div>
      </section>

      {/* Login Form Section */}
      <section className="py-20 bg-white">
        <div className="max-w-md mx-auto px-6">
          <div className="bg-gray-50 rounded-2xl p-8">
            {(error || localError) && (
              <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
                {error || localError}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Email *
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="votre@email.com"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Mot de passe *
                </label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                />
              </div>

              <button
                type="submit"
                disabled={loading || localLoading}
                className="w-full btn-primary py-3 rounded-full font-semibold hover:opacity-90 disabled:opacity-50"
              >
                {loading || localLoading ? 'Connexion en cours...' : 'Se Connecter'}
              </button>
            </form>

            <div className="mt-6 text-center">
              <p className="text-gray-600">
                Pas encore de compte ?{' '}
                <Link to="/register" className="text-green-600 font-semibold hover:text-green-700">
                  S'inscrire
                </Link>
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Login;
