<<<<<<< HEAD
import axios from "./axios.js";
=======
import axios from './Customize-axios.js'

>>>>>>> 5f8573f549205bf3fd48b1af49263834fe906858

const loginApi = (email, password) => {
  return axios.post("/http:locoalhost8080", { email, password });
};

export { loginApi };
