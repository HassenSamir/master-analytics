import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Admin, Dashboard, Home, Login, NotFound, SignUp, UnAuthorized } from './pages';
import React, { useEffect, useState } from 'react';
import analyticsScript from './scripts/analyticsScript.js';
import PropTypes from 'prop-types';
//import NotLoggedNavBar from './components/notloggednavbar/NotLoggedNavBar';
import { AuthProvider } from './contexts/AuthProvider';
import { ROLE } from './utils/utils';

const hasRole = (user, roles) => {
  console.log('user', user);
  return roles.some((role) => user.roles.includes(role));
};

const ProtectedRoute = ({ user, roles, children }) => {
  console.log('ProtectedRoute', { user, roles, children });

  if (!user || !hasRole(user, roles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const script = document.createElement('script');
    script.src = analyticsScript;
    script.async = true;
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  useEffect(() => {
    console.log('USER1', user);
  }, [user]);

  return (
    <AuthProvider>
      <Router>
        {/*!user && <NotLoggedNavBar />*/}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login setUser={setUser} />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/dashboard" element={<Dashboard user={user} />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute user={user} roles={[ROLE.ADMIN]}>
                <Admin />
              </ProtectedRoute>
            }
          />
          <Route path="/unauthorized" element={<UnAuthorized />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;

ProtectedRoute.propTypes = {
  user: PropTypes.object,
  roles: PropTypes.arrayOf(PropTypes.string).isRequired,
  children: PropTypes.any
};
