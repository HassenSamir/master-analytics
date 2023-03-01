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
    console.log('login', data);
    setUser(data.username);
    setToken(data.accessToken);
    setRoles(data.roles);
    if (data.accessToken) {
      localStorage.setItem('token', data.accessToken);
    }
  };

  useEffect(() => {
    console.log({ user, token, roles });
  }, [user, token, roles]);

  const logout = () => {
    setUser(null);
    setToken(null);
    setRoles(null);
    localStorage.removeItem('token');
  };

  const contextValue = useMemo(() => {
    return { user, token, roles, login, logout };
  }, [user, token, roles]);

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired
};
