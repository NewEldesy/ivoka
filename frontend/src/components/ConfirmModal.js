import React from 'react';

/**
 * Modal générique pour remplacer window.confirm et alert.
 *
 * Props:
 *   isOpen      {boolean}
 *   type        {"confirm"|"alert"|"error"}  - défaut "confirm"
 *   title       {string}
 *   message     {string}
 *   confirmLabel {string}  - défaut "Confirmer"
 *   cancelLabel  {string}  - défaut "Annuler"
 *   onConfirm   {function}
 *   onCancel    {function}  - omis pour type "alert"/"error"
 */
const ConfirmModal = ({
  isOpen,
  type = 'confirm',
  title,
  message,
  confirmLabel,
  cancelLabel = 'Annuler',
  onConfirm,
  onCancel,
}) => {
  if (!isOpen) return null;

  const isAlert = type === 'alert' || type === 'error';

  const iconBg = {
    confirm: 'bg-amber-50',
    alert:   'bg-blue-50',
    error:   'bg-red-50',
  }[type];

  const iconColor = {
    confirm: 'text-amber-500',
    alert:   'text-blue-500',
    error:   'text-red-500',
  }[type];

  const btnColor = {
    confirm: 'bg-red-500 hover:bg-red-600',
    alert:   'bg-[#7D8471] hover:bg-[#6b7261]',
    error:   'bg-red-500 hover:bg-red-600',
  }[type];

  const Icon = () => {
    if (type === 'error') return (
      <svg className={`w-6 h-6 ${iconColor}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
      </svg>
    );
    if (type === 'alert') return (
      <svg className={`w-6 h-6 ${iconColor}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    );
    return (
      <svg className={`w-6 h-6 ${iconColor}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
      </svg>
    );
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[10000] p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-sm w-full p-8 text-center">
        <div className={`w-14 h-14 ${iconBg} rounded-full flex items-center justify-center mx-auto mb-5`}>
          <Icon />
        </div>
        {title && <h3 className="font-display text-xl font-bold text-gray-900 mb-2">{title}</h3>}
        <p className="text-gray-500 text-sm leading-relaxed mb-8">{message}</p>
        <div className={`flex gap-3 ${isAlert ? 'justify-center' : ''}`}>
          {!isAlert && (
            <button
              onClick={onCancel}
              className="flex-1 border-2 border-gray-200 text-gray-600 py-2.5 rounded-xl font-medium text-sm hover:border-gray-300 transition-colors"
            >
              {cancelLabel}
            </button>
          )}
          <button
            onClick={onConfirm}
            className={`${isAlert ? 'px-10' : 'flex-1'} ${btnColor} text-white py-2.5 rounded-xl font-medium text-sm transition-colors`}
          >
            {confirmLabel || (isAlert ? 'OK' : 'Confirmer')}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
