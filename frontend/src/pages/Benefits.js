import React, { useEffect } from 'react';
import { useScrollAnimation } from '../hooks/useScrollAnimation';

const Benefits = () => {
  const heroRef = useScrollAnimation();
  const teaRef = useScrollAnimation();
  const avocadoRef = useScrollAnimation();
  const ctaRef = useScrollAnimation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const teaBenefits = [
    {
      title: "Riche en Antioxydants",
      description: "Protège vos cellules contre les dommages causés par les radicaux libres et ralentit le vieillissement.",
      icon: "🛡️"
    },
    {
      title: "Boost d'Énergie Naturel",
      description: "La caféine naturelle du thé fournit une énergie durable sans les effets secondaires du café.",
      icon: "⚡"
    },
    {
      title: "Améliore la Digestion",
      description: "Facilite la digestion et réduit les ballonnements grâce à ses propriétés anti-inflammatoires.",
      icon: "🌿"
    },
    {
      title: "Soutient le Système Immunitaire",
      description: "Les polyphénols renforcent vos défenses naturelles contre les infections.",
      icon: "💪"
    },
    {
      title: "Réduit le Stress",
      description: "L'acide aminé L-théanine favorise la détente et améliore la concentration.",
      icon: "🧘"
    },
    {
      title: "Protège le Cœur",
      description: "Réduit le mauvais cholestérol et soutient la santé cardiovasculaire.",
      icon: "❤️"
    }
  ];

  const avocadoBenefits = [
    {
      title: "Hydratation Profonde",
      description: "Pénètre en profondeur pour nourrir et hydrater la peau en profondeur sans laisser de film gras.",
      icon: "💧"
    },
    {
      title: "Anti-Âge Naturel",
      description: "Riche en vitamine E et antioxydants qui combattent les signes du vieillissement cutané.",
      icon: "✨"
    },
    {
      title: "Cœur Sain",
      description: "Les acides gras monoinsaturés réduisent le mauvais cholestérol et protègent le cœur.",
      icon: "❤️"
    },
    {
      title: "Cheveux Forts et Brillants",
      description: "Nourrit le cuir chevelu, répare les pointes fourchues et donne de la brillance naturelle.",
      icon: "💆"
    },
    {
      title: "Cuisine Saine",
      description: "Huile stable à haute température, parfaite pour la cuisson saine et savoureuse.",
      icon: "🍳"
    },
    {
      title: "Système Immunitaire",
      description: "Les vitamines A, D et E renforcent les défenses naturelles de l'organisme.",
      icon: "🛡️"
    }
  ];

  return (
    <div className="Benefits">
      {/* Hero Section */}
      <section className="pt-24 pb-16 hero-bg" ref={heroRef}>
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="font-display text-5xl lg:text-6xl font-bold mb-6">
            Bienfaits <span className="gradient-text">Naturels</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Découvrez les nombreux bienfaits santé de nos produits naturels, 
            approuvés par la science et testés par nos clients.
          </p>
        </div>
      </section>

      {/* Tea Benefits Section */}
      <section id="tea-benefits" className="py-20 bg-white" ref={teaRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="font-display text-4xl font-bold mb-6">
              Bienfaits du <span className="gradient-text">Thé</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Le thé est l'une des boissons les plus saines au monde. Découvrez pourquoi nos thés biologiques 
              sont un choix exceptionnel pour votre santé.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {teaBenefits.map((benefit, index) => (
              <div key={index} className="product-card p-6 rounded-2xl">
                <div className="text-center mb-4">
                  <div className="text-4xl mb-4">{benefit.icon}</div>
                  <h3 className="font-display text-xl font-semibold mb-3">{benefit.title}</h3>
                </div>
                <p className="text-gray-600 text-center leading-relaxed">{benefit.description}</p>
              </div>
            ))}
          </div>

          <div className="mt-16 bg-green-50 rounded-2xl p-8">
            <h3 className="font-display text-2xl font-semibold mb-6 text-center">
              Pourquoi notre thé est-il spécial ?
            </h3>
            <div className="grid md:grid-cols-2 gap-8">
              <div>
                <h4 className="font-semibold text-lg mb-3 text-green-800">Cultivé en France</h4>
                <p className="text-gray-600">
                  Nos plantations bénéficient d'un climat méditerranéen idéal pour la culture du thé, 
                  avec des méthodes ancestrales transmises de génération en génération.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-green-800">Zéro Pesticide</h4>
                <p className="text-gray-600">
                  Une agriculture 100% biologique qui respecte la biodiversité et préserve 
                  la pureté naturelle de nos thés.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-green-800">Récolte Manuelle</h4>
                <p className="text-gray-600">
                  Chaque feuille est cueillie à la main au moment optimal pour garantir 
                  une concentration maximale en principes actifs.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-green-800">Séchage Naturel</h4>
                <p className="text-gray-600">
                  Un processus de séchage lent et naturel qui préserve tous les arômes 
                  et les propriétés bénéfiques du thé.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Avocado Oil Benefits Section */}
      <section id="avocado-benefits" className="py-20 organic-bg" ref={avocadoRef}>
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="font-display text-4xl font-bold mb-6">
              Bienfaits de l'Huile d'<span className="gradient-text">Avocat</span>
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              L'huile d'avocat est un trésor de bienfaits pour la santé et la beauté. 
              Découvrez pourquoi c'est l'un de nos produits phares.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {avocadoBenefits.map((benefit, index) => (
              <div key={index} className="product-card p-6 rounded-2xl">
                <div className="text-center mb-4">
                  <div className="text-4xl mb-4">{benefit.icon}</div>
                  <h3 className="font-display text-xl font-semibold mb-3">{benefit.title}</h3>
                </div>
                <p className="text-gray-600 text-center leading-relaxed">{benefit.description}</p>
              </div>
            ))}
          </div>

          <div className="mt-16 bg-yellow-50 rounded-2xl p-8">
            <h3 className="font-display text-2xl font-semibold mb-6 text-center">
              Notre procédé de fabrication
            </h3>
            <div className="grid md:grid-cols-2 gap-8">
              <div>
                <h4 className="font-semibold text-lg mb-3 text-yellow-800">Pression à Froid</h4>
                <p className="text-gray-600">
                  Une méthode douce qui préserve toutes les qualités nutritionnelles 
                  de l'avocat sans utiliser de solvants chimiques.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-yellow-800">Filtration Naturelle</h4>
                <p className="text-gray-600">
                  Un processus de filtration lent qui élimine les impuretés tout en 
                  conservant les vitamines et minéraux essentiels.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-yellow-800">Avocats Mûrs</h4>
                <p className="text-gray-600">
                  Nous utilisons uniquement des avocats parfaitement mûrs, récoltés 
                  au pic de leur maturité pour une qualité optimale.
                </p>
              </div>
              <div>
                <h4 className="font-semibold text-lg mb-3 text-yellow-800">Embouteillage Propre</h4>
                <p className="text-gray-600">
                  Une chaîne d'embouteillage stérile qui garantit la pureté et la 
                  fraîcheur de chaque goutte d'huile.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-white" ref={ctaRef}>
        <div className="max-w-4xl mx-auto px-6 text-center">
          <h2 className="font-display text-4xl lg:text-5xl font-bold mb-6">
            Prêt à profiter de ces <span className="gradient-text">bienfaits</span> ?
          </h2>
          <p className="text-xl text-gray-600 mb-8">
            Découvrez nos produits naturels et commencez votre voyage vers une meilleure santé et un bien-être durable.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <a href="/products" className="btn-primary px-8 py-4 rounded-full text-lg font-medium">
              Découvrir nos produits
            </a>
            <a href="/contact" className="border-2 border-gray-300 text-gray-700 px-8 py-4 rounded-full text-lg font-medium hover:border-gray-400 transition-colors">
              Nous contacter
            </a>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Benefits;