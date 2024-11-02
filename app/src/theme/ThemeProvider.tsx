import React, { createContext, useContext, useState, useEffect } from 'react';
import { ThemeProvider as MUIThemeProvider, createTheme } from '@mui/material';

const ThemeContext = createContext({
  isDarkMode: false,
  toggleTheme: () => {},
  accentColor: '#1976d2',
  setAccentColor: (color: string) => {},
});

export const ThemeProvider = ({ children }: { children: React.ReactNode }) => {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [accentColor, setAccentColor] = useState('#1976d2');

  useEffect(() => {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    setIsDarkMode(prefersDark);
  }, []);

  const theme = createTheme({
    palette: {
      mode: isDarkMode ? 'dark' : 'light',
      primary: {
        main: accentColor,
      },
    },
  });

  const toggleTheme = () => setIsDarkMode(!isDarkMode);

  return (
    <ThemeContext.Provider value={{ isDarkMode, toggleTheme, accentColor, setAccentColor }}>
      <MUIThemeProvider theme={theme}>
        {children}
      </MUIThemeProvider>
    </ThemeContext.Provider>
  );
}; 