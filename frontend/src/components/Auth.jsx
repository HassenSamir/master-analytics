import { useLocation, Navigate, Outlet } from 'react-router-dom';
import { useContext } from 'react';

import PropTypes from 'prop-types';
import { AuthContext } from '../contexts/AuthProvider';

const Auth = ({ allowedRoles }) => {
  const { roles } = useContext(AuthContext);
  const location = useLocation();

  return allowedRoles.includes(roles) ? (
    <Outlet />
  ) : (
    <Navigate to="/unauthorized" state={{ from: location }} replace />
  );
};

export default Auth;

Auth.propTypes = {
  allowedRoles: PropTypes.arrayOf(PropTypes.string).isRequired
};
