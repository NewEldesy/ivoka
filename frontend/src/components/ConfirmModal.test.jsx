import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import ConfirmModal from './ConfirmModal';

describe('ConfirmModal', () => {
  const baseProps = {
    isOpen: true,
    title: 'Titre test',
    message: 'Message de test',
    onConfirm: jest.fn(),
    onCancel: jest.fn(),
  };

  beforeEach(() => jest.clearAllMocks());

  it('ne rend rien quand isOpen est false', () => {
    const { container } = render(<ConfirmModal {...baseProps} isOpen={false} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('affiche le titre et le message', () => {
    render(<ConfirmModal {...baseProps} />);
    expect(screen.getByText('Titre test')).toBeInTheDocument();
    expect(screen.getByText('Message de test')).toBeInTheDocument();
  });

  it('type confirm, affiche les boutons Confirmer et Annuler', () => {
    render(<ConfirmModal {...baseProps} type="confirm" />);
    expect(screen.getByText('Confirmer')).toBeInTheDocument();
    expect(screen.getByText('Annuler')).toBeInTheDocument();
  });

  it('type confirm, appelle onConfirm au clic sur Confirmer', () => {
    render(<ConfirmModal {...baseProps} type="confirm" />);
    fireEvent.click(screen.getByText('Confirmer'));
    expect(baseProps.onConfirm).toHaveBeenCalledTimes(1);
  });

  it('type confirm, appelle onCancel au clic sur Annuler', () => {
    render(<ConfirmModal {...baseProps} type="confirm" />);
    fireEvent.click(screen.getByText('Annuler'));
    expect(baseProps.onCancel).toHaveBeenCalledTimes(1);
  });

  it('type alert, affiche seulement le bouton OK, pas Annuler', () => {
    render(<ConfirmModal {...baseProps} type="alert" />);
    expect(screen.getByText('OK')).toBeInTheDocument();
    expect(screen.queryByText('Annuler')).not.toBeInTheDocument();
  });

  it('type error, affiche seulement le bouton OK, pas Annuler', () => {
    render(<ConfirmModal {...baseProps} type="error" />);
    expect(screen.getByText('OK')).toBeInTheDocument();
    expect(screen.queryByText('Annuler')).not.toBeInTheDocument();
  });

  it('utilise confirmLabel personnalisé', () => {
    render(<ConfirmModal {...baseProps} type="confirm" confirmLabel="Supprimer" />);
    expect(screen.getByText('Supprimer')).toBeInTheDocument();
  });

  it('utilise cancelLabel personnalisé', () => {
    render(<ConfirmModal {...baseProps} type="confirm" cancelLabel="Non merci" />);
    expect(screen.getByText('Non merci')).toBeInTheDocument();
  });

  it("n'affiche pas le titre si absent", () => {
    render(<ConfirmModal {...baseProps} title={undefined} />);
    expect(screen.queryByText('Titre test')).not.toBeInTheDocument();
  });
});
