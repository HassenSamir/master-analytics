import authInterceptor from '../pages/interceptor/auth.interceptor';

export const userSignIn = (username, password) => {
  return authInterceptor
    .post('/auth/signin', {
      username,
      password
    })
    .then((response) => response.data);
};

export const userSignUp = (username, email, password) => {
  return authInterceptor
    .post('/auth/signup', {
      username,
      email,
      password
    })
    .then((response) => response.data);
};

export const userLogOut = () => {
  localStorage.removeItem('token');
};
