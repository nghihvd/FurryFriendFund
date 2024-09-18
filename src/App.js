import "./App.scss";
import Header from "./components/Header";
import TableUsers from "./components/TableUsers";
import Container from "react-bootstrap/Container";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import HomePage from "./components/HomePage";
import Footer from "./components/Footer";

function App() {
  return (
    <>
      <div className="app-container">
        <Header />
        <Container>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            {/* <Route path="/events" element={<Events />} /> */}
            {/* <Route path="/register" element={<Register />} /> */}
          </Routes>
        </Container>
        <Footer />
      </div>
    </>
  );
}

export default App;
