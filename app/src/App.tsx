import React from 'react';
import { ThemeProvider } from './theme/ThemeProvider';
import { AppRouter } from './router/AppRouter';
import { DsuProvider } from './context/DsuContext';
import { SettingsProvider } from './context/SettingsContext';
import { NotificationProvider } from './context/NotificationContext';

function App() {
  return (
    <ThemeProvider>
      <SettingsProvider>
        <DsuProvider>
          <NotificationProvider>
            <AppRouter />
          </NotificationProvider>
        </DsuProvider>
      </SettingsProvider>
    </ThemeProvider>
  );
}

export default App; 