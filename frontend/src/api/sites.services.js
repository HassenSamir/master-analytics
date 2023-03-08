import authInterceptor from '../pages/interceptor/auth.interceptor';

export const getSitesByUserId = (userId) => {
  return authInterceptor.get(`/sites/${userId}`).then((response) => response.data);
};
