import axios from "axios";

// Create an axios instance with your custom configuration
const instance = axios.create({
  baseURL: 'https://localhost:8080/',
  headers: {
    'Content-Type': 'application/json',
  },
});


// Add a response interceptor
instance.interceptors.response.use(
  function (response) {
    return response.data ? response.data : { statusCode: response.status };
  },
  function (error) {
    return Promise.reject(error);
  }
);

// Create an API object to handle HTTP methods
const api = {
  get: (url, config = {}) => instance.get(url, config),
  post: (url, data, config = {}) => instance.post(url, data, config),
  put: (url, data, config = {}) => instance.put(url, data, config)
};

export default api;

