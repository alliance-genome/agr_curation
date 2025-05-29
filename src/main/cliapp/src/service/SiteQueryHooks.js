import { useQuery, useQueryClient } from '@tanstack/react-query';
import { ApiVersionService } from './ApiVersionService';
import { PersonService } from './PersonService';

export const QUERY_KEYS = {
  API_VERSION: 'apiVersion',
  USER_INFO: 'userInfo'
};

export const useApiVersion = (authState) => {
  const apiService = new ApiVersionService();

  return useQuery(
    [QUERY_KEYS.API_VERSION],
    () => apiService.getApiVersion(),
    {
      enabled: !!authState?.isAuthenticated,
    }
  );
};

export const useUserInfo = (authState) => {
  const personService = new PersonService();

  return useQuery(
    [QUERY_KEYS.USER_INFO],
    () => personService.getUserInfo(),
    {
      enabled: !!authState?.isAuthenticated,
      // staleTime: 1000 * 60 * 5, 
    }
  );
};

export const useRegenApiToken = () => {
  const queryClient = useQueryClient();
  const personService = new PersonService();

  return async () => {
    try {
      const data = await personService.regenApiToken();
      queryClient.setQueryData([QUERY_KEYS.USER_INFO], data);
      return data;
    } catch (err) {
      console.error(err);
      throw err;
    }
  };
};

export const getCachedData = (queryClient, queryKey) => {
  return queryClient.getQueryData(queryKey);
};
