/* eslint-disable @typescript-eslint/no-empty-function */
import { createContext, useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';

export const AuthContext = createContext({
  user: null,
  token: null,
  role: null,
  login: () => {},
  logout: () => {}
});

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [roles, setRoles] = useState(null);

  const login = (data) => {
    setUser(data);
    setToken(data.accessToken);
    setRoles(data.roles);
    localStorage.setItem('master-analytics-user', JSON.stringify(data));
    localStorage.setItem('master-analytics-token', data.accessToken);
    localStorage.setItem('master-analytics-roles', JSON.stringify(data.roles));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    setRoles(null);
    localStorage.removeItem('master-analytics-user');
    localStorage.removeItem('master-analytics-token');
    localStorage.removeItem('master-analytics-roles');
  };

  useEffect(() => {
    const userFromStorage = localStorage.getItem('master-analytics-user');
    const tokenFromStorage = localStorage.getItem('master-analytics-token');
    const rolesFromStorage = localStorage.getItem('master-analytics-roles');

    if (userFromStorage && tokenFromStorage && rolesFromStorage) {
      setUser(JSON.parse(userFromStorage));
      setToken(tokenFromStorage);
      setRoles(JSON.parse(rolesFromStorage));
    }
  }, []);

  const contextValue = useMemo(() => {
    return { user, token, roles, login, logout };
  }, [user, token, roles]);

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired
};
