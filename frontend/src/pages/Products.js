import React, { useState, useEffect } from 'react';
import { useScrollAnimation } from '../hooks/useScrollAnimation';
import { productsAPI } from '../services/api';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [error, setError] = useState(null);
  
  const heroRef = useScrollAnimation();
  const teaRef = useScrollAnimation();
  const avocadoRef = useScrollAnimation();

  useEffect(() => {
    window.scrollTo(0, 0);
    fetchProducts();
  }, [selectedCategory]);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let data;
      if (selectedCategory === 'all') {
        data = await productsAPI.getAll();
      } else {
        data = await productsAPI.getByCategory(selectedCategory);
      }
      
      // Si l'API retourne une erreur ou pas de données, utiliser les données mock
      if (!data || data.length === 0) {
        setProducts(getMockProducts());
      } else {
        setProducts(data);
      }
    } catch (error) {
      console.error('Erreur lors de la récupération des produits:', error);
      setError('Impossible de charger les produits. Veuillez réessayer plus tard.');
      setProducts(getMockProducts()); // Fallback sur les données mock
    } finally {
      setLoading(false);
    }
  };

  const getMockProducts = () => {
    const mockProducts = [
      {
        id: 1,
        name: "Thé Vert Bio",
        description: "Thé vert biologique cultivé localement, riche en antioxydants. Parfait pour vos moments de détente.",
        price: "12.90",
        category: "tea",
        imageUrl: "/resources/tea-leaves-product.png",
        available: true
      },
      {
        id: 2,
        name: "Thé Noir Premium",
        description: "Thé noir de qualité supérieure avec des notes épicées et maltées. Idéal pour le petit-déjeuner.",
        price: "15.50",
        category: "tea",
        imageUrl: "/resources/tea-leaves-product.png",
        available: true
      },
      {
        id: 3,
        name: "Thé Blanc Délicat",
        description: "Thé blanc aux arômes délicats et floraux. Une expérience gustative raffinée.",
        price: "18.90",
        category: "tea",
        imageUrl: "/resources/tea-leaves-product.png",
        available: true
      },
      {
        id: 4,
        name: "Huile d'Avocat Pressée à Froid",
        description: "Huile d'avocat pure pressée à froid. Parfaite pour la cuisine et les soins cosmétiques.",
        price: "24.90",
        category: "avocado_oil",
        imageUrl: "/resources/avocado-oil-bottle.png",
        available: true
      },
      {
        id: 5,
        name: "Huile d'Avocat Cosmétique",
        description: "Huile d'avocat spécialement conçue pour les soins de la peau et des cheveux. Riche en vitamines E et K.",
        price: "29.90",
        category: "avocado_oil",
        imageUrl: "/resources/avocado-oil-bottle.png",
        available: true
      }
    ];

    if (selectedCategory === 'all') {
      return mockProducts;
    }
    return mockProducts.filter(p => p.category === selectedCategory);
  };

  const teaProducts = products.filter(p => p.category === 'tea');
  const avocadoProducts = products.filter(p => p.category === 'avocado_oil');

  if (error) {
    return (
      <div className="Products">
        <div className="max-w-7xl mx-auto px-6 py-20 text-center">
          <div className="text-red-600 mb-4">⚠️</div>
          <h2 className="text-2xl font-bold mb-4">Erreur de chargement</h2>
          <p className="text-gray-600 mb-8">{error}</p>
          <button 
            onClick={fetchProducts}
            className="btn-primary px-6 py-3 rounded-full font-medium"
          >
            Réessayer
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="Products">
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={heroRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6 fade-in-up visible">
            Nos <span className="gradient-text">Produits</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
            Découvrez notre gamme de produits naturels d'exception, soigneusement sélectionnés 
            pour leur qualité et leurs bienfaits pour votre santé.
          </p>
        </div>
      </section>

      {/* Category Filter */}
      <section className="py-8 bg-white border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex justify-center space-x-4">
            <button
              onClick={() => setSelectedCategory('all')}
              className={`px-6 py-3 rounded-full font-medium transition-colors ${
                selectedCategory === 'all'
                  ? 'btn-primary'
                  : 'border-2 border-gray-300 text-gray-700 hover:border-gray-400'
              }`}
            >
              Tous les produits
            </button>
            <button
              onClick={() => setSelectedCategory('tea')}
              className={`px-6 py-3 rounded-full font-medium transition-colors ${
                selectedCategory === 'tea'
                  ? 'btn-primary'
                  : 'border-2 border-gray-300 text-gray-700 hover:border-gray-400'
              }`}
            >
              Thés
            </button>
            <button
              onClick={() => setSelectedCategory('avocado_oil')}
              className={`px-6 py-3 rounded-full font-medium transition-colors ${
                selectedCategory === 'avocado_oil'
                  ? 'btn-primary'
                  : 'border-2 border-gray-300 text-gray-700 hover:border-gray-400'
              }`}
            >
              Huiles d'Avocat
            </button>
          </div>
        </div>
      </section>

      {/* Tea Products Section */}
      {(selectedCategory === 'all' || selectedCategory === 'tea') && (
        <section id="tea" className="py-20 bg-white" ref={teaRef}>
          <div className="max-w-7xl mx-auto px-6">
            <div className="text-center mb-16">
              <h2 className="font-display text-4xl font-bold mb-6 fade-in-up visible">
                Thé <span className="gradient-text">IVOKA</span>
              </h2>
              <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
                Des variétés de thé biologiques, cultivées localement avec des méthodes traditionnelles. 
                Riches en antioxydants, parfaits pour vos moments de détente.
              </p>
            </div>

            {loading ? (
              <div className="text-center py-12">
                <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
                <p className="mt-4 text-gray-600">Chargement des produits...</p>
              </div>
            ) : (
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
                {teaProducts.map((product) => (
                  <div key={product.id} className="product-card p-6 rounded-2xl fade-in-up visible">
                    <div className="mb-6">
                      <img src={product.imageUrl} alt={product.name} 
                           className="w-full h-48 object-cover rounded-xl" />
                    </div>
                    <h3 className="font-display text-xl font-semibold mb-3">{product.name}</h3>
                    <p className="text-gray-600 mb-4 text-sm leading-relaxed">{product.description}</p>
                    <div className="flex justify-between items-center mb-4">
                      <span className="text-2xl font-bold gradient-text">{product.price}€</span>
                      <span className="text-sm text-green-600 bg-green-100 px-2 py-1 rounded-full">
                        Disponible
                      </span>
                    </div>
                    <div className="space-y-2 mb-6">
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Cultivé localement
                      </div>
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Sans pesticides
                      </div>
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Riche en antioxydants
                      </div>
                    </div>
                    <button className="w-full btn-primary py-3 rounded-full font-medium hover:scale-105 transition-transform">
                      Ajouter au panier
                    </button>
                  </div>
                ))}
              </div>
            )}

            {teaProducts.length === 0 && !loading && (
              <div className="text-center py-12">
                <p className="text-gray-600">Aucun produit trouvé dans cette catégorie.</p>
              </div>
            )}
          </div>
        </section>
      )}

      {/* Avocado Oil Products Section */}
      {(selectedCategory === 'all' || selectedCategory === 'avocado_oil') && (
        <section id="avocado" className="py-20 organic-bg" ref={avocadoRef}>
          <div className="max-w-7xl mx-auto px-6">
            <div className="text-center mb-16">
              <h2 className="font-display text-4xl font-bold mb-6 fade-in-up visible">
                Huile d'Avocat <span className="gradient-text">IVOKA</span>
              </h2>
              <p className="text-xl text-gray-600 max-w-3xl mx-auto fade-in-up visible">
                Huile d'avocat pressée à froid, idéale pour la cuisine et les soins cosmétiques. 
                Pure, naturelle, et riche en nutriments essentiels.
              </p>
            </div>

            {loading ? (
              <div className="text-center py-12">
                <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
                <p className="mt-4 text-gray-600">Chargement des produits...</p>
              </div>
            ) : (
              <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
                {avocadoProducts.map((product) => (
                  <div key={product.id} className="product-card p-6 rounded-2xl fade-in-up visible">
                    <div className="mb-6">
                      <img src={product.imageUrl} alt={product.name} 
                           className="w-full h-48 object-cover rounded-xl" />
                    </div>
                    <h3 className="font-display text-xl font-semibold mb-3">{product.name}</h3>
                    <p className="text-gray-600 mb-4 text-sm leading-relaxed">{product.description}</p>
                    <div className="flex justify-between items-center mb-4">
                      <span className="text-2xl font-bold gradient-text">{product.price}€</span>
                      <span className="text-sm text-green-600 bg-green-100 px-2 py-1 rounded-full">
                        Disponible
                      </span>
                    </div>
                    <div className="space-y-2 mb-6">
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Pressée à froid
                      </div>
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Usage cosmétique & alimentaire
                      </div>
                      <div className="flex items-center text-sm text-gray-500">
                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                        </svg>
                        Riche en vitamines
                      </div>
                    </div>
                    <button className="w-full btn-primary py-3 rounded-full font-medium hover:scale-105 transition-transform">
                      Ajouter au panier
                    </button>
                  </div>
                ))}
              </div>
            )}

            {avocadoProducts.length === 0 && !loading && (
              <div className="text-center py-12">
                <p className="text-gray-600">Aucun produit trouvé dans cette catégorie.</p>
              </div>
            )}
          </div>
        </section>
      )}
    </div>
  );
};

export default Products;