
import axios from "./axios.js";

const loginApi = (email, password) => {
  return axios.post("/http:locoalhost8080", { email, password });
};

export { loginApi };