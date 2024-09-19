import React from "react";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import "../styles/homepage.scss";
import { NavLink } from "react-router-dom";
import dog1 from "../assets/images/dog-1.jpg";
import dog2 from "../assets/images/dog-2.jpg";
import dog3 from "../assets/images/dog-3.jpg";
import dog4 from "../assets/images/dog-4.jpg";
const HomePage = () => {
  const responsive = {
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: 4,
    },
    tablet: {
      breakpoint: { max: 1024, min: 464 },
      items: 2,
    },
    mobile: {
      breakpoint: { max: 464, min: 0 },
      items: 1,
    },
  };

  const pets = [
    {
      name: "Win",
      image: dog1,
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Elvis",
      image: dog2,
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Chưa rõ",
    },
    {
      name: "Nicky",
      image: dog3,
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Orion",
      image: dog4,
      gender: "Cái",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
  ];

  return (
    <div className="homepage">
      <section className="hero-section">
        <h2>FurryFriendsFund</h2>
        <p>Nhận nuôi thú cưng - Mang yêu thương về nhà</p>
      </section>

      <section className="featured-pets">
        <h2>Danh sách các bé</h2>
        <Carousel
          responsive={responsive}
          infinite={true}
          removeArrowOnDeviceType={["tablet", "mobile"]}
        >
          {pets.map((pet, index) => (
            <div key={index} className="pet-card">
              <NavLink to="/adopt" className="nav-link">
                <img src={pet.image} alt={pet.name} />
                <h3>{pet.name}</h3>
                <p>Gender: {pet.gender}</p>
                <p>Age: {pet.age}</p>
                <p>Vaccinated: {pet.vaccinated}</p>
              </NavLink>
            </div>
          ))}
        </Carousel>
        <button className="adopt-button">Adopt</button>
      </section>

      <section className="about-us">
        <h2>About us</h2>
        <p>
          Chúng tôi là một nhóm sinh viên Đại học FPT bao gồm tình nguyện viên
          cùng hoạt động vì tình yêu chó mèo. Để giúp các bé có được một cuộc
          sống hạnh phúc.
        </p>
        <div className="action-buttons">
          <button>
            <NavLink to="/adopt" className="nav-link">
              Adopt
            </NavLink>
          </button>
          <button>
            <NavLink to="/donate" className="nav-link">
              Donate
            </NavLink>
          </button>
        </div>
      </section>

      <section className="news">
        <h2>News</h2>
      </section>

      <section className="sponsors">
        <h2>Donators</h2>
        <p>
          Chúng tôi là một nhóm sinh viên Đại học FPT bao gồm tình nguyện viên
          cùng hoạt động vì tình yêu chó mèo. Để giúp các bé có được một cuộc
          sống hạnh phúc.
        </p>
      </section>
    </div>
  );
};

export default HomePage;
