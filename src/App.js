import "./App.scss";
import Header from "./components/Header";
import Container from "react-bootstrap/Container";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import Footer from "./components/Footer";
import Pets from "./components/Pets";
import HomePage from "./components/HomePage";

function App() {
  return (
    <>
      <div className="app-container">
        <Header />
        <div>
          <Container>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<Login />} />
              {/* <Route path="/events" element={<Events />} /> */}
              <Route path="/register" element={<Register />} />
              <Route path="/Pets" element={<Pets />} />
            </Routes>
          </Container>
        </div>
        <Footer />
      </div>
    </>
  );
}

export default App;
