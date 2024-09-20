import "../styles/login.scss";
import "@fortawesome/fontawesome-free/css/all.min.css";

import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useNavigate, NavLink } from "react-router-dom";
import api from "../services/axios";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isShowPassword, setIsShowPassword] = useState(false);
  const navigate = useNavigate();

  // Handle login action
  const handleLogin = async (event) => {
    event.preventDefault();
    if (!username || !password) {
      toast.error("Username/Password is required!");
      return;
    }

    try {
      const response = await api.post("account/login", { username, password });
      if (response && response.data && response.data.token) {
        localStorage.setItem("token", response.data.token);
        toast.success("Login successful!");
        navigate("/");
      } else {
        toast.error("Invalid username or password");
      }
    } catch (error) {
      toast.error("Invalid username or password");
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      navigate("/");
    }
  }, [navigate]);

  const handleGoBack = () => {
    navigate("/");
  };

  return (
    <>
      <div className="login-container col-12 col-sm-4 ">
        <form onSubmit={handleLogin}>
          <div className="title">Login</div>
          <div className="text">Username</div>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
          />
          <div className="input-password">
            <input
              type={isShowPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
            <i
              className={
                isShowPassword ? "fa-solid fa-eye" : "fa-solid fa-eye-slash"
              }
              onClick={() => setIsShowPassword(!isShowPassword)}
            ></i>
          </div>

          <button
            className={`button ${username && password ? "active" : ""}`}
            disabled={!username || !password}
          >
            Login
          </button>

          <div className="back" onClick={handleGoBack}>
            <i className="fa-solid fa-angles-left">Go back</i>
            <p>You do not have an account?</p>
            <NavLink to="/register" className="nav-link">
              Register
            </NavLink>
          </div>
        </form>
      </div>
    </>
  );
};

export default Login;
