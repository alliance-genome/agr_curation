import { useQuery, useQueryClient } from '@tanstack/react-query';
import { ApiVersionService } from './ApiVersionService';
import { PersonService } from './PersonService';

// Query key constants
export const QUERY_KEYS = {
  API_VERSION: 'apiVersion',
  USER_INFO: 'userInfo'
};

/**
 * Custom hook to get the API version
 */
export const useApiVersion = (authState) => {
  const apiService = new ApiVersionService();
  
  return useQuery(
    [QUERY_KEYS.API_VERSION],
    () => apiService.getApiVersion(),
    {
      enabled: !!authState?.isAuthenticated,
      staleTime: Infinity, // API version doesn't change during a session
      cacheTime: Infinity,
    }
  );
};

/**
 * Custom hook to get the user info including API token
 */
export const useUserInfo = (authState) => {
  const personService = new PersonService();
  
  return useQuery(
    [QUERY_KEYS.USER_INFO],
    () => personService.getUserInfo(),
    {
      enabled: !!authState?.isAuthenticated,
      staleTime: 1000 * 60 * 5, // 5 minutes
    }
  );
};

/**
 * Custom hook to regenerate the API token
 */
export const useRegenApiToken = () => {
  const queryClient = useQueryClient();
  const personService = new PersonService();
  
  return async () => {
    try {
      const data = await personService.regenApiToken();
      // Update the cache with the new user info including new token
      queryClient.setQueryData([QUERY_KEYS.USER_INFO], data);
      return data;
    } catch (err) {
      console.error(err);
      throw err;
    }
  };
};

/**
 * Utility function to access cached values directly
 */
export const getCachedData = (queryClient, queryKey) => {
  return queryClient.getQueryData(queryKey);
};
