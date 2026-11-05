import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import Login from './Login';

// Axios utilise des ESM dans v1.x
jest.mock('../services/api', () => ({
  authAPI: {
    login: jest.fn(),
    register: jest.fn(),
    logout: jest.fn(),
  },
  productsAPI: { getAll: jest.fn() },
  ordersAPI: { create: jest.fn() },
  cartAPI: { getCart: jest.fn() },
  adminAPI: {},
  messagesAPI: {},
}));

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderLogin = (authOverrides = {}) => {
  const authValue = {
    login: jest.fn(),
    loading: false,
    error: null,
    ...authOverrides,
  };
  return {
    ...render(
      <AuthContext.Provider value={authValue}>
        <MemoryRouter>
          <Login />
        </MemoryRouter>
      </AuthContext.Provider>
    ),
    authValue,
  };
};

describe('Login page', () => {
  beforeEach(() => jest.clearAllMocks());

  it('affiche le formulaire de connexion', () => {
    renderLogin();
    expect(screen.getByPlaceholderText('votre@email.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /se connecter/i })).toBeInTheDocument();
  });

  it('affiche une erreur si les champs sont vides à la soumission', async () => {
    renderLogin();
    fireEvent.click(screen.getByRole('button', { name: /se connecter/i }));
    await waitFor(() => {
      expect(screen.getByText(/veuillez remplir tous les champs/i)).toBeInTheDocument();
    });
  });

  it('redirige vers /admin pour un utilisateur admin', async () => {
    const { authValue } = renderLogin({
      login: jest.fn().mockResolvedValue({ user: { role: 'admin' } }),
    });

    fireEvent.change(screen.getByPlaceholderText('votre@email.com'), {
      target: { value: 'admin@ivoka.fr' },
    });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), {
      target: { value: 'Admin@ivoka2024' },
    });
    fireEvent.click(screen.getByRole('button', { name: /se connecter/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/admin');
    });
  });

  it('redirige vers / pour un utilisateur customer', async () => {
    renderLogin({
      login: jest.fn().mockResolvedValue({ user: { role: 'customer' } }),
    });

    fireEvent.change(screen.getByPlaceholderText('votre@email.com'), {
      target: { value: 'user@ivoka.fr' },
    });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), {
      target: { value: 'User@ivoka2024' },
    });
    fireEvent.click(screen.getByRole('button', { name: /se connecter/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/');
    });
  });

  it('affiche le message d\'erreur retourné par l\'API', async () => {
    renderLogin({
      login: jest.fn().mockRejectedValue({
        response: { data: { error: 'Email ou mot de passe incorrect' } },
      }),
    });

    fireEvent.change(screen.getByPlaceholderText('votre@email.com'), {
      target: { value: 'bad@ivoka.fr' },
    });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), {
      target: { value: 'WrongPass1!' },
    });
    fireEvent.click(screen.getByRole('button', { name: /se connecter/i }));

    await waitFor(() => {
      expect(screen.getByText('Email ou mot de passe incorrect')).toBeInTheDocument();
    });
  });

  it('désactive le bouton pendant le chargement', () => {
    renderLogin({ loading: true });
    expect(screen.getByRole('button', { name: /connexion en cours/i })).toBeDisabled();
  });
});
