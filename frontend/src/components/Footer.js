import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-6">
        <div className="text-center">
          <Link to="/" className="font-display text-3xl font-bold gradient-text mb-4 block">
            IVOKA
          </Link>
          <p className="text-gray-600 mb-6">Thé & Huile d'Avocat Naturels</p>
          
          <div className="flex justify-center space-x-6 mb-8">
            <a href="#" className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-600 hover:bg-green-100 hover:text-green-600 transition-colors">
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/>
              </svg>
            </a>
            <a href="#" className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-600 hover:bg-green-100 hover:text-green-600 transition-colors">
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M22.675 0H1.325C.593 0 0 .593 0 1.326v21.348C0 23.406.593 24 1.325 24h11.495v-9.294H9.847v-3.622h2.973V8.413c0-2.946 1.797-4.555 4.422-4.555 1.258 0 2.339.093 2.654.135v3.077l-1.822.001c-1.43 0-1.707.68-1.707 1.676v2.198h3.414l-.445 3.622h-2.969V24h5.824C23.406 24 24 23.406 24 22.674V1.326C24 .593 23.406 0 22.675 0z"/>
              </svg>
            </a>
          </div>
          
          <div className="flex justify-center space-x-8 mb-8 text-sm">
            <Link to="/about" className="text-gray-600 hover:text-gray-800 transition-colors">
              À propos
            </Link>
            <Link to="/products" className="text-gray-600 hover:text-gray-800 transition-colors">
              Produits
            </Link>
            <Link to="/benefits" className="text-gray-600 hover:text-gray-800 transition-colors">
              Bienfaits
            </Link>
            <Link to="/contact" className="text-gray-600 hover:text-gray-800 transition-colors">
              Contact
            </Link>
          </div>
          
          <p className="text-gray-500 text-sm">
            © 2025 IVOKA. Tous droits réservés. | Site web développé avec passion pour la nature.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;