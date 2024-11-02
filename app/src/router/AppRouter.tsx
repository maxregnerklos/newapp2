import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MainScreen } from '../pages/MainScreen';
import { SettingsScreen } from '../pages/SettingsScreen';
import { Layout } from '../components/Layout';

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<MainScreen />} />
          <Route path="/settings" element={<SettingsScreen />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}; 