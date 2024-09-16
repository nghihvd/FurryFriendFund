import "./App.scss";
import Header from "./components/Header";
import Container from "react-bootstrap/Container";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Home from "./components/Home";
import Register from "./components/Register";
import Users from "./components/Users";
function App() {
  return (
    <>
      <div className="app-container">
        <Header />

        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            {/* <Route path="/events" element={<Events />} /> */}
            <Route path="/register" element={<Register />} />
            <Route path="/users" element={<Users />} />
          </Routes>
        </Container>
      </div>
    </>
  );
}

export default App;
