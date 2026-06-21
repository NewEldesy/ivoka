import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { useScrollAnimation } from '../hooks/useScrollAnimation';

const Register = () => {
  const registerRef = useScrollAnimation();
  const navigate = useNavigate();
  const { register, loading, error } = useContext(AuthContext);
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    newsletter: false
  });
  const [localError, setLocalError] = useState(null);
  const [localLoading, setLocalLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const validateForm = () => {
    if (!formData.firstName.trim() || !formData.lastName.trim() || 
        !formData.email.trim() || !formData.password.trim()) {
      setLocalError('Veuillez remplir tous les champs obligatoires');
      return false;
    }

    if (formData.password.length < 6) {
      setLocalError('Le mot de passe doit contenir au moins 6 caractères');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setLocalError('Les mots de passe ne correspondent pas');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError(null);

    if (!validateForm()) {
      return;
    }

    setLocalLoading(true);

    try {
      await register({
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        newsletter: formData.newsletter
      });
      navigate('/');
    } catch (err) {
      setLocalError(err.response?.data?.error || 'Erreur lors de l\'inscription');
    } finally {
      setLocalLoading(false);
    }
  };

  return (
    <div className="Register">
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={registerRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6 fade-in-up visible">
            S'<span className="gradient-text">Inscrire</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
            Rejoignez la communauté IVOKA
          </p>
        </div>
      </section>

      {/* Registration Form Section */}
      <section className="py-20 bg-white">
        <div className="max-w-md mx-auto px-6">
          <div className="bg-gray-50 rounded-2xl p-8">
            {(error || localError) && (
              <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
                {error || localError}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Prénom *
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    placeholder="Jean"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Nom *
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    placeholder="Dupont"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                    required
                  />
                </div>
              </div>

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

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Confirmer le mot de passe *
                </label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                />
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  name="newsletter"
                  id="newsletter"
                  checked={formData.newsletter}
                  onChange={handleChange}
                  className="w-4 h-4 rounded border-gray-300 text-green-600"
                />
                <label htmlFor="newsletter" className="ml-2 text-sm text-gray-600">
                  S'abonner à notre newsletter
                </label>
              </div>

              <button
                type="submit"
                disabled={loading || localLoading}
                className="w-full btn-primary py-3 rounded-full font-semibold hover:opacity-90 disabled:opacity-50"
              >
                {loading || localLoading ? 'Inscription en cours...' : 'S\'Inscrire'}
              </button>
            </form>

            <div className="mt-6 text-center">
              <p className="text-gray-600">
                Vous avez déjà un compte ?{' '}
                <Link to="/login" className="text-green-600 font-semibold hover:text-green-700">
                  Se connecter
                </Link>
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Register;
