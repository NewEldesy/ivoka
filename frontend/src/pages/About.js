import React, { useEffect } from 'react';
import { useScrollAnimation } from '../hooks/useScrollAnimation';

const About = () => {
  const heroRef = useScrollAnimation();
  const storyRef = useScrollAnimation();
  const missionRef = useScrollAnimation();
  const teamRef = useScrollAnimation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <div className="About">
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={heroRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6">
            À <span className="gradient-text">propos</span> d'IVOKA
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Découvrez notre histoire, nos valeurs et notre engagement envers une agriculture durable 
            et des produits naturels de qualité exceptionnelle.
          </p>
        </div>
      </section>

      {/* Story Section */}
      <section className="py-20 bg-white" ref={storyRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="font-display text-4xl font-bold mb-6">
                Notre <span className="gradient-text">Histoire</span>
              </h2>
              <div className="space-y-6 text-gray-600">
                <p className="text-lg leading-relaxed">
                  Fondée en 2020 dans la région de l'Hérault, IVOKA est née d'une passion profonde 
                  pour l'agriculture biologique et le respect de la nature. Notre fondateur, 
                  Jean-Pierre Dubois, a décidé de transformer ses terres familiales en une exploitation 
                  biologique après avoir constaté les effets néfastes des pesticides sur l'environnement.
                </p>
                <p className="text-lg leading-relaxed">
                  Commencée avec une petite plantation de thé vert, l'aventure IVOKA s'est rapidement 
                  étendue à l'huile d'avocat, produit rare et précieux en France. Aujourd'hui, 
                  nous cultivons sur plus de 50 hectares avec une équipe de 15 personnes passionnées 
                  par l'agriculture durable.
                </p>
                <p className="text-lg leading-relaxed">
                  Chaque produit IVOKA est le fruit d'un travail minutieux, respectant les cycles 
                  naturels et privilégiant les méthodes traditionnelles. Notre engagement va au-delà 
                  de la simple production : nous formons d'autres agriculteurs aux techniques 
                  biologiques et participons à la préservation de la biodiversité locale.
                </p>
              </div>
            </div>
            <div className="relative">
              <div className="floating-animation">
                <img src="/resources/hero-tea-plantation.png" alt="Plantation IVOKA" 
                     className="w-full h-96 object-cover rounded-2xl shadow-2xl" />
              </div>
              <div className="absolute -bottom-4 -right-4 w-24 h-24 bg-gradient-to-br from-green-100 to-green-200 rounded-full opacity-50"></div>
              <div className="absolute -top-4 -left-4 w-16 h-16 bg-gradient-to-br from-yellow-100 to-yellow-200 rounded-full opacity-50"></div>
            </div>
          </div>
        </div>
      </section>

      {/* Mission Section */}
      <section className="py-20 organic-bg" ref={missionRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="font-display text-4xl font-bold mb-6">
              Notre <span className="gradient-text">Mission</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Chez IVOKA, nous croyons que l'agriculture peut être à la fois productive 
              et respectueuse de l'environnement. Notre mission est de démontrer cette 
              compatibilité au quotidien.
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="text-center product-card p-6 rounded-2xl">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064" />
                </svg>
              </div>
              <h3 className="font-display text-xl font-semibold mb-3">Agriculture Durable</h3>
              <p className="text-gray-600 text-sm">
                Cultiver sans pesticides ni engrais chimiques, en respectant les sols et la biodiversité.
              </p>
            </div>
            
            <div className="text-center product-card p-6 rounded-2xl">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <h3 className="font-display text-xl font-semibold mb-3">Qualité Supérieure</h3>
              <p className="text-gray-600 text-sm">
                Produits 100% naturels, sans additifs, sélectionnés avec soin pour leur pureté.
              </p>
            </div>
            
            <div className="text-center product-card p-6 rounded-2xl">
              <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </div>
              <h3 className="font-display text-xl font-semibold mb-3">Bien-être Naturel</h3>
              <p className="text-gray-600 text-sm">
                Offrir des produits qui contribuent à la santé et au bien-être de nos clients.
              </p>
            </div>
            
            <div className="text-center product-card p-6 rounded-2xl">
              <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <h3 className="font-display text-xl font-semibold mb-3">Communauté Locale</h3>
              <p className="text-gray-600 text-sm">
                Soutenir l'économie locale et former les agriculteurs aux pratiques durables.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="py-20 bg-white" ref={teamRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="font-display text-4xl font-bold mb-6">
              Notre <span className="gradient-text">Équipe</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Une équipe passionnée d'experts en agriculture biologique, 
              dédiée à la qualité et au respect de l'environnement.
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div className="text-center product-card p-8 rounded-2xl">
              <div className="w-24 h-24 bg-gradient-to-br from-green-400 to-green-600 rounded-full flex items-center justify-center mx-auto mb-6">
                <span className="text-2xl font-bold text-white">JP</span>
              </div>
              <h3 className="font-display text-xl font-semibold mb-2">Jean-Pierre Dubois</h3>
              <p className="text-gray-500 mb-4">Fondateur & Directeur</p>
              <p className="text-gray-600 text-sm">
                30 ans d'expérience en agriculture biologique. Pionnier de la culture du thé en France.
              </p>
            </div>
            
            <div className="text-center product-card p-8 rounded-2xl">
              <div className="w-24 h-24 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center mx-auto mb-6">
                <span className="text-2xl font-bold text-white">MC</span>
              </div>
              <h3 className="font-display text-xl font-semibold mb-2">Marie-Claire Martin</h3>
              <p className="text-gray-500 mb-4">Responsable Qualité</p>
              <p className="text-gray-600 text-sm">
                Ingénieure agronome spécialisée en contrôle qualité des produits biologiques.
              </p>
            </div>
            
            <div className="text-center product-card p-8 rounded-2xl">
              <div className="w-24 h-24 bg-gradient-to-br from-yellow-400 to-yellow-600 rounded-full flex items-center justify-center mx-auto mb-6">
                <span className="text-2xl font-bold text-white">AL</span>
              </div>
              <h3 className="font-display text-xl font-semibold mb-2">Antoine Lefèvre</h3>
              <p className="text-gray-500 mb-4">Responsable Production</p>
              <p className="text-gray-600 text-sm">
                Expert en techniques de pression à froid et transformation des huiles végétales.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default About;