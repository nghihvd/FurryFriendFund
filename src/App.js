import "./App.scss";
import Header from "./components/Header";
import Container from "react-bootstrap/Container";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Home from "./components/Home";
import Register from "./components/Register";
import Footers from "./components/Footers";
import Pets from "./components/Pets";

function App() {
  return (<>
      <div className="app-container">
        <Header />
        <div>
        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            {/* <Route path="/events" element={<Events />} /> */}
            <Route path="/register" element={<Register />} />
            <Route path="/Pets" element={<Pets />} />
          </Routes>
        </Container>
        </div>
      <Footers/>
      </div>
      </>
  );
}

export default App;
