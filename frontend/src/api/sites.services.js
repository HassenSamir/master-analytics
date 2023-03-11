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

export const deleteSiteById = (siteId) => {
  return authInterceptor.delete(`/sites/${siteId}`).then((response) => response.data);
};

export const updateSiteById = (siteId, name, url) => {
  return authInterceptor
    .put(`/sites/${siteId}`, {
      name,
      url
    })
    .then((response) => response.data);
};
