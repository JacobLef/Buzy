import React, { useEffect, useRef } from 'react';
import { X } from 'lucide-react';
import { createPortal } from 'react-dom';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl';
}

export const Modal = ({ 
  isOpen, 
  onClose, 
  children, 
  title,
  maxWidth = 'md'
}: ModalProps) => {
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      document.body.style.overflow = 'hidden';
    }
    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const maxWidthClasses = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
    '2xl': 'max-w-2xl',
  };

  return createPortal(
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Overlay - Updated for blurred, darkened glassmorphism effect */}
      <div 
        className="fixed inset-0 bg-navy-900/40 backdrop-blur-sm transition-opacity" 
        onClick={onClose} 
        aria-hidden="true"
      />

      {/* Modal Dialog */}
      <div className="flex min-h-screen items-center justify-center p-4 text-center sm:p-0 pointer-events-none">
        <div 
          ref={modalRef}
          className={`relative transform overflow-hidden rounded-2xl bg-white text-left shadow-xl transition-all sm:my-8 w-full pointer-events-auto ${maxWidthClasses[maxWidth]}`}
          role="dialog"
          aria-modal="true"
          aria-labelledby={title ? "modal-title" : undefined}
        >
          {/* Header */}
          <div className="bg-white px-6 py-4 border-b border-slate-100 flex items-center justify-between">
            {title && (
              <h3 className="text-lg font-bold text-navy-900" id="modal-title">
                {title}
              </h3>
            )}
            <button 
              onClick={onClose} 
              className="rounded-full p-1 hover:bg-slate-50 text-slate-400 hover:text-navy-900 transition-colors outline-none focus:ring-2 focus:ring-blue-500"
              aria-label="Close modal"
            >
              <X size={20} />
            </button>
          </div>

          {/* Content */}
          <div className="px-6 py-6">
            {children}
          </div>
        </div>
      </div>
    </div>,
    document.body
  );
};

