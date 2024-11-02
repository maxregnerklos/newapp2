import React, { createContext, useContext, useState } from 'react';
import { Settings } from '../types';

interface SettingsContextType {
  settings: Settings;
  updateSettings: (newSettings: Partial<Settings>) => void;
}

const defaultSettings: Settings = {
  autoReboot: true,
  autoBackup: true,
  batteryCheck: true,
  darkMode: false,
  accentColor: '#1976d2'
};

export const SettingsContext = createContext<SettingsContextType>({} as SettingsContextType);

export const SettingsProvider = ({ children }: { children: React.ReactNode }) => {
  const [settings, setSettings] = useState<Settings>(defaultSettings);

  const updateSettings = (newSettings: Partial<Settings>) => {
    setSettings(prev => ({ ...prev, ...newSettings }));
  };

  return (
    <SettingsContext.Provider value={{ settings, updateSettings }}>
      {children}
    </SettingsContext.Provider>
  );
}; 