import authInterceptor from '../pages/interceptor/auth.interceptor';

export const getUserById = (userId) => {
  return authInterceptor.get(`/user/${userId}`).then((response) => response.data);
};
