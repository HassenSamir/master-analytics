import authInterceptor from '../pages/interceptor/auth.interceptor';

export const getSitesByUserId = (userId) => {
  return authInterceptor.get(`/sites/${userId}`).then((response) => response.data);
};

export const createSite = (userId, name, description, url) => {
  return authInterceptor
    .post(`/sites/${userId}`, {
      name,
      description,
      url
    })
    .then((response) => response.data);
};
