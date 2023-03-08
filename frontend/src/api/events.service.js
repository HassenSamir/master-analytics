import authInterceptor from '../pages/interceptor/auth.interceptor';

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

export const getEventsByTypeAndUserId = async (userId, type, page = 0, size = 5) => {
  return authInterceptor
    .get(`/events/user/${userId}?page=${page}&size=${size}${type && '&type=' + type}`)
    .then((response) => response.data);
};
