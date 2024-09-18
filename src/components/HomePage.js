import React from "react";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import "../styles/homepage.scss";

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
      image: "../assets/images/dog-1.jpg",
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Elvis",
      image: "../assets/images/dog-2.jpg",
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Chưa rõ",
    },
    {
      name: "Nicky",
      image: "../assets/images/dog-3.jpg",
      gender: "Đực",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
    {
      name: "Orion",
      image: "../assets/images/dog-4.jpg",
      gender: "Cái",
      age: "Trưởng thành",
      vaccinated: "Có",
    },
  ];

  return (
    <div className="homepage">
      <section className="hero-section">
        <h1>FurryFriendsFund</h1>
        <p>Nhận nuôi thú cưng - Mang yêu thương về nhà</p>
      </section>

      <section className="featured-pets">
        <h2>Bé ngoan trong tuần</h2>
        <Carousel
          responsive={responsive}
          infinite={true}
          removeArrowOnDeviceType={["tablet", "mobile"]}
        >
          {pets.map((pet, index) => (
            <div key={index} className="pet-card">
              <img src={pet.image} alt={pet.name} />
              <h3>{pet.name}</h3>
              <p>Giới tính: {pet.gender}</p>
              <p>Tuổi: {pet.age}</p>
              <p>Tiêm phòng: {pet.vaccinated}</p>
            </div>
          ))}
        </Carousel>
        <button className="adopt-button">Nhận nuôi</button>
      </section>

      <section className="about-us">
        <h2>Về chúng tôi</h2>
        <p>
          Chúng tôi là một nhóm trẻ gồm tình nguyện viên Việt Nam và một số bạn
          nước ngoài, cùng hoạt động vì tình yêu chó mèo.
        </p>
        <div className="action-buttons">
          <button>Ủng hộ</button>
          <button>Nhận nuôi</button>
        </div>
      </section>

      <section className="news">
        <h2>Tin tức</h2>
        {/* Thêm các bài viết tin tức ở đây */}
      </section>

      <section className="sponsors">
        <h2>Nhà tài trợ cố định</h2>
        {/* Thêm logo các nhà tài trợ ở đây */}
      </section>
    </div>
  );
};

export default HomePage;
