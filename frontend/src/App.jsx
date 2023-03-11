import './App.css';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Admin, Dashboard, Home, SignIn, NotFound, SignUp, UnAuthorized } from './pages';
import PropTypes from 'prop-types';
import { AuthContext } from './contexts/AuthProvider';
import { ROLE } from './utils/utils';
import { useContext } from 'react';

const hasRole = (user, roles) => {
  return roles.some((role) => user.roles.includes(role));
};

const ProtectedRoute = ({ user, roles, children }) => {
  const userFromStorage = localStorage.getItem('master-analytics-user');

  if (!userFromStorage && (!user || !hasRole(user, roles))) {
    return <Navigate to="/" replace />;
  }

  return children;
};

function App() {
  const { user } = useContext(AuthContext);

  return (
    <Routes>
      <Route path="/" element={user ? <Navigate to="/dashboard" replace /> : <Home />} />
      <Route path="/signin" element={<SignIn />} />
      <Route path="/signup" element={<SignUp />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute user={user} roles={[ROLE.USER]}>
            <Dashboard />
          </ProtectedRoute>
        }
      />
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
  );
}

export default App;

ProtectedRoute.propTypes = {
  user: PropTypes.object,
  roles: PropTypes.arrayOf(PropTypes.string).isRequired,
  children: PropTypes.any
};
