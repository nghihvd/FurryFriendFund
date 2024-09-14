import "../styles/login.scss";
import "@fortawesome/fontawesome-free/css/all.min.css";
import { useState } from "react";
import { loginApi } from "../services/UserServices";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isShowPassword, setIsShowPassword] = useState(false);
  // const handleLogin = () => {
  //   if (!email || !password) {
  //   }
  // };
  return (
    <>
      <div className="login-container col-12 col-sm-4 ">
        <div className="title">Login</div>
        <div className="text">Email or UserName</div>

        <input
          type="text"
          placeholder="Email or UserName..."
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <div className="input-password">
          <input
            type={isShowPassword === true ? "text" : "password"}
            placeholder="Password..."
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
          <i
            className={
              isShowPassword === true
                ? "fa-solid fa-eye"
                : "fa-solid fa-eye-slash"
            }
            onClick={() => setIsShowPassword(!isShowPassword)}
          ></i>
        </div>

        <button
          className={`button ${email && password ? "active" : ""}`}
          disabled={email && password ? false : true}
          // onClick={() => handleLogin()}
        >
          Login
        </button>

        <div className="back">
          <i className="fa-solid fa-angles-left"></i>Go back
        </div>
      </div>
    </>
  );
};
export default Login;
