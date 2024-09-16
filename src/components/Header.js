import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import logoApp from "../assets/images/logo.png";
import { useLocation, NavLink } from "react-router-dom";
import Container from "react-bootstrap/Container";

const Header = (props) => {
  const location = useLocation(); // Đúng cách gọi useLocation

  return (
    <>
      <Navbar bg="light" expand="lg" className="header">
        <Container> 
          <Navbar.Brand href="/">
            <img
              src={logoApp}
              width="30"
              height="30"
              className="d-inline-block align-top"
              alt="React Bootstrap logo"
            />
            FurryFriendsFunny
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto" activeKey={location.pathname}>
              <NavLink to="/" className="nav-link">
                Home
              </NavLink>
              <NavLink to="/adopt" className="nav-link">
                Adopt
              </NavLink>
              <NavLink to="/events" className="nav-link">
                Events
              </NavLink>
              <NavLink to="/donate" className="nav-link">
                Donate
              </NavLink>
              <NavLink to="/users" className="nav-link">
                Users
              </NavLink>
            </Nav>
            <Nav className="login-regist">
              <NavLink to="/login" className="nav-link">
                Login
              </NavLink>
              <NavLink to="/register" className="nav-link">
                Register
              </NavLink>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </>
  );
};

export default Header;
