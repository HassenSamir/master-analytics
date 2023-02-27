import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Dashboard, Home } from './pages';
import React, { useEffect } from 'react';
import analyticsScript from './scripts/analyticsScript.js';

function App() {
  useEffect(() => {
    const script = document.createElement('script');
    script.src = analyticsScript;
    script.async = true;
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Router>
  );
}

export default App;
