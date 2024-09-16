import axios from './Customize-axios.js'

const fetchAlluser = () =>{
 return axios.get("/api/users?page=1");
}
export {fetchAlluser}

const loginApi = (email, password) => {
  return axios.post("/api/login", { email, password });
};

export { loginApi };

const registerApi = (userData) => {
  return axios.post("/api/register", {email, password, firstName});
};

export { registerApi };
