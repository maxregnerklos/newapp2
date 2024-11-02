import { useContext } from 'react';
import { DsuContext } from '../context/DsuContext';

export const useDsu = () => {
  const context = useContext(DsuContext);
  if (!context) {
    throw new Error('useDsu must be used within a DsuProvider');
  }
  return context;
}; 