import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import anime from 'animejs';
import Splitting from 'splitting';
import 'splitting/dist/splitting.css';
import { useScrollAnimation } from '../hooks/useScrollAnimation';

const Home = () => {
  const heroRef = useRef(null);
  const valuesRef = useScrollAnimation();
  const productsRef = useScrollAnimation();
  const ctaRef = useScrollAnimation();

  useEffect(() => {
    // Initialize Splitting.js
    Splitting();

    // Animate hero elements
    anime({
      targets: '.fade-in-up',
      opacity: [0, 1],
      translateY: [30, 0],
      delay: anime.stagger(200),
      duration: 800,
      easing: 'easeOutQuart'
    });
  }, []);

  return (
    <div className="Home">
      {/* Hero Section */}
      <section className="min-h-screen flex items-center hero-bg pt-20" ref={heroRef}>
        <div className="max-w-7xl mx-auto px-6 grid lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-8">
            <h1 className="font-display text-5xl lg:text-7xl font-bold leading-tight fade-in-up" data-splitting>
              <span className="gradient-text">Nature</span><br />
              <span className="text-gray-800">& Bien-être</span>
            </h1>
            <p className="text-xl text-gray-600 leading-relaxed fade-in-up max-w-lg">
              Découvrez nos produits naturels d'exception : thés biologiques et huile d'avocat pressée à froid, 
              cultivés avec respect pour l'environnement et votre santé.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 fade-in-up">
              <Link to="/products" className="btn-primary px-8 py-4 rounded-full text-lg font-medium text-center">
                Découvrir nos produits
              </Link>
              <Link to="/about" className="border-2 border-gray-300 text-gray-700 px-8 py-4 rounded-full text-lg font-medium text-center hover:border-gray-400 transition-colors">
                Notre histoire
              </Link>
            </div>
          </div>
          <div className="relative">
            <div className="floating-animation">
              <img src="./resources/hero-tea-plantation.png" alt="Plantation de thé IVOKA" 
                   className="w-full h-auto rounded-2xl shadow-2xl" />
            </div>
            <div className="absolute -bottom-6 -right-6 w-32 h-32 bg-gradient-to-br from-green-100 to-green-200 rounded-full opacity-50"></div>
            <div className="absolute -top-6 -left-6 w-24 h-24 bg-gradient-to-br from-yellow-100 to-yellow-200 rounded-full opacity-50"></div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="py-20 organic-bg" ref={valuesRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16 fade-in-up">
            <h2 className="font-display text-4xl lg:text-5xl font-bold mb-6">
              Nos <span className="gradient-text">Valeurs</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Chez IVOKA, nous croyons en une agriculture durable, des produits naturels 
              et un impact positif sur la santé et l'environnement.
            </p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center product-card p-8 rounded-2xl fade-in-up">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </div>
              <h3 className="font-display text-2xl font-semibold mb-4">Durabilité</h3>
              <p className="text-gray-600">Des pratiques agricoles respectueuses de l'environnement pour préserver notre planète.</p>
            </div>
            
            <div className="text-center product-card p-8 rounded-2xl fade-in-up">
              <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="font-display text-2xl font-semibold mb-4">Qualité</h3>
              <p className="text-gray-600">Des produits 100% naturels, sans additifs, sélectionnés avec soin pour leur pureté.</p>
            </div>
            
            <div className="text-center product-card p-8 rounded-2xl fade-in-up">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                </svg>
              </div>
              <h3 className="font-display text-2xl font-semibold mb-4">Bien-être</h3>
              <p className="text-gray-600">Des bienfaits prouvés pour votre santé, de la digestion à la beauté naturelle.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Products Preview */}
      <section className="py-20 bg-white" ref={productsRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16 fade-in-up">
            <h2 className="font-display text-4xl lg:text-5xl font-bold mb-6">
              Nos <span className="gradient-text">Produits</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Deux gammes d'exception, une même philosophie : la nature au service de votre bien-être.
            </p>
          </div>
          
          <div className="grid lg:grid-cols-2 gap-12">
            <div className="product-card p-8 rounded-3xl fade-in-up">
              <div className="flex flex-col lg:flex-row items-center gap-8">
                <div className="flex-1">
                  <img src="./resources/tea-leaves-product.png" alt="Thé IVOKA" 
                       className="w-full h-64 object-cover rounded-2xl" />
                </div>
                <div className="flex-1 text-center lg:text-left">
                  <h3 className="font-display text-3xl font-bold mb-4">Thé IVOKA</h3>
                  <p className="text-gray-600 mb-6">
                    Des variétés de thé biologiques, cultivées localement avec des méthodes traditionnelles. 
                    Riches en antioxydants, parfaits pour vos moments de détente.
                  </p>
                  <ul className="text-sm text-gray-500 mb-6 space-y-2">
                    <li>• Cultivé localement</li>
                    <li>• Sans pesticides</li>
                    <li>• Riche en antioxydants</li>
                  </ul>
                  <Link to="/products#tea" className="btn-primary px-6 py-3 rounded-full font-medium inline-block">
                    Découvrir le thé
                  </Link>
                </div>
              </div>
            </div>
            
            <div className="product-card p-8 rounded-3xl fade-in-up">
              <div className="flex flex-col lg:flex-row items-center gap-8">
                <div className="flex-1">
                  <img src="./resources/avocado-oil-bottle.png" alt="Huile d'Avocat IVOKA" 
                       className="w-full h-64 object-cover rounded-2xl" />
                </div>
                <div className="flex-1 text-center lg:text-left">
                  <h3 className="font-display text-3xl font-bold mb-4">Huile d'Avocat</h3>
                  <p className="text-gray-600 mb-6">
                    Huile d'avocat pressée à froid, idéale pour la cuisine et les soins cosmétiques. 
                    Pure, naturelle, et riche en nutriments essentiels.
                  </p>
                  <ul className="text-sm text-gray-500 mb-6 space-y-2">
                    <li>• Pressée à froid</li>
                    <li>• Usage cosmétique & alimentaire</li>
                    <li>• Riche en vitamines</li>
                  </ul>
                  <Link to="/products#avocado" className="btn-primary px-6 py-3 rounded-full font-medium inline-block">
                    Découvrir l'huile
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 hero-bg" ref={ctaRef}>
        <div className="max-w-4xl mx-auto px-6 text-center">
          <h2 className="font-display text-4xl lg:text-5xl font-bold mb-6 fade-in-up">
            Prêt à découvrir <span className="gradient-text">IVOKA</span> ?
          </h2>
          <p className="text-xl text-gray-600 mb-8 fade-in-up">
            Rejoignez notre communauté et profitez de nos offres exclusives sur nos produits naturels.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center fade-in-up">
            <Link to="/contact" className="btn-primary px-8 py-4 rounded-full text-lg font-medium">
              Nous contacter
            </Link>
            <Link to="/benefits" className="border-2 border-gray-300 text-gray-700 px-8 py-4 rounded-full text-lg font-medium hover:border-gray-400 transition-colors">
              Voir les bienfaits
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;