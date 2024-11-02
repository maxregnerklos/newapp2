import React, { createContext, useContext, useState } from 'react';
import { DsuPackage } from '../types';

interface DsuContextType {
  dsuList: DsuPackage[];
  currentDsu: DsuPackage | null;
  addDsu: (dsu: DsuPackage) => void;
  removeDsu: (id: string) => void;
  setCurrentDsu: (dsu: DsuPackage | null) => void;
  isLoading: boolean;
  progress: number;
}

const DsuContext = createContext<DsuContextType>({} as DsuContextType);

export const DsuProvider = ({ children }: { children: React.ReactNode }) => {
  const [dsuList, setDsuList] = useState<DsuPackage[]>([]);
  const [currentDsu, setCurrentDsu] = useState<DsuPackage | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [progress, setProgress] = useState(0);

  const addDsu = async (dsu: DsuPackage) => {
    // Implement compatibility check
    const isCompatible = await checkCompatibility(dsu);
    if (!isCompatible) {
      throw new Error('Incompatible DSU package');
    }
    setDsuList([...dsuList, dsu]);
  };

  const removeDsu = (id: string) => {
    setDsuList(dsuList.filter(dsu => dsu.id !== id));
  };

  return (
    <DsuContext.Provider value={{
      dsuList,
      currentDsu,
      addDsu,
      removeDsu,
      setCurrentDsu,
      isLoading,
      progress
    }}>
      {children}
    </DsuContext.Provider>
  );
}; 