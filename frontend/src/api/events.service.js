import authInterceptor from '../interceptor/auth.interceptor';

export const getEventsMetrics = async (userId) => {
  return authInterceptor.get(`/events/metrics/user/${userId}`).then((response) => response.data);
};

export const getEventsMetricsPeriod = async (userId, period = 'month') => {
  return authInterceptor
    .get(`/events/metrics/period/user/${userId}?period=${period}`)
    .then((response) => response.data);
};

export const getEventsMetricsBySiteAndUserId = async (userId, siteId) => {
  return authInterceptor
    .get(`/events/metrics/user/${userId}/site/${siteId}`)
    .then((response) => response.data);
};

export const getEventsByTypeAndUserId = async (userId, page = 0, size = 5, type = null) => {
  const baseUrl = `/events/user/${userId}?page=${page}&size=${size}`;
  const url = !type ? baseUrl : baseUrl + `&type=${type}`;
  return authInterceptor.get(url).then((response) => {
    return response.data;
  });
};

export const getLatestEventsByUserId = async (userId, type, page = 0, size = 5) => {
  return authInterceptor
    .get(`/events/metrics/latest/${userId}?type=${type}&page=${page}&size=${size}`)
    .then((response) => {
      return response.data;
    });
};
