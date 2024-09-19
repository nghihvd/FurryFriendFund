import React from "react";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import "../styles/homepage.scss";
<<<<<<< HEAD
=======
import { NavLink } from "react-router-dom";
import dog1 from "../assets/images/dog-1.jpg";
import dog2 from "../assets/images/dog-2.jpg";
import dog3 from "../assets/images/dog-3.jpg";
import dog4 from "../assets/images/dog-4.jpg";
>>>>>>> 40e99ee (third)

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
<<<<<<< HEAD
      image: "../assets/images/dog-1.jpg",
=======
      image: dog1,
>>>>>>> 40e99ee (third)
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Elvis",
<<<<<<< HEAD
      image: "../assets/images/dog-2.jpg",
=======
      image: dog2,
>>>>>>> 40e99ee (third)
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Chưa rõ",
    },
    {
      name: "Nicky",
<<<<<<< HEAD
      image: "../assets/images/dog-3.jpg",
=======
      image: dog3,
>>>>>>> 40e99ee (third)
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Orion",
<<<<<<< HEAD
      image: "../assets/images/dog-4.jpg",
=======
      image: dog4,
>>>>>>> 40e99ee (third)
      gender: "Cái",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
  ];

  return (
    <div className="homepage">
      <section className="hero-section">
<<<<<<< HEAD
        <h1>FurryFriendsFund</h1>
=======
        <h2>FurryFriendsFund</h2>
>>>>>>> 40e99ee (third)
        <p>Nhận nuôi thú cưng - Mang yêu thương về nhà</p>
      </section>

      <section className="featured-pets">
<<<<<<< HEAD
        <h2>Bé ngoan trong tuần</h2>
=======
        <h2>Danh sách các bé</h2>
>>>>>>> 40e99ee (third)
        <Carousel
          responsive={responsive}
          infinite={true}
          removeArrowOnDeviceType={["tablet", "mobile"]}
        >
          {pets.map((pet, index) => (
            <div key={index} className="pet-card">
<<<<<<< HEAD
              <img src={pet.image} alt={pet.name} />
              <h3>{pet.name}</h3>
              <p>Giới tính: {pet.gender}</p>
              <p>Tuổi: {pet.age}</p>
              <p>Tiêm phòng: {pet.vaccinated}</p>
=======
              <NavLink to="/adopt" className="nav-link">
                <img src={pet.image} alt={pet.name} />
                <h3>{pet.name}</h3>
                <p>Giới tính: {pet.gender}</p>
                <p>Tuổi: {pet.age}</p>
                <p>Tiêm phòng: {pet.vaccinated}</p>
              </NavLink>
>>>>>>> 40e99ee (third)
            </div>
          ))}
        </Carousel>
        <button className="adopt-button">Nhận nuôi</button>
      </section>

      <section className="about-us">
        <h2>Về chúng tôi</h2>
        <p>
<<<<<<< HEAD
          Chúng tôi là một nhóm trẻ gồm tình nguyện viên Việt Nam và một số bạn
          nước ngoài, cùng hoạt động vì tình yêu chó mèo.
        </p>
        <div className="action-buttons">
          <button>Ủng hộ</button>
          <button>Nhận nuôi</button>
=======
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
>>>>>>> 40e99ee (third)
        </div>
      </section>

      <section className="news">
<<<<<<< HEAD
        <h2>Tin tức</h2>
        {/* Thêm các bài viết tin tức ở đây */}
      </section>

      <section className="sponsors">
        <h2>Nhà tài trợ cố định</h2>
        {/* Thêm logo các nhà tài trợ ở đây */}
=======
        <h2>News</h2>
      </section>

      <section className="sponsors">
        <h2>Donators</h2>
>>>>>>> 40e99ee (third)
      </section>
    </div>
  );
};

export default HomePage;
