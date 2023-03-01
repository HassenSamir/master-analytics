import authInterceptor from '../pages/interceptor/auth.interceptor';

export const userSignIn = (username, password) => {
  return authInterceptor
    .post('/auth/signin', {
      username,
      password
    })
    .then((response) => response.data);
};

export const userLogOut = () => {
  localStorage.removeItem('token');
};
