import React, { useState } from "react";
import {
  FaUser,
  FaEnvelope,
  FaIdCard,
  FaCheck,
  FaTimes,
  FaEdit,
  FaSave,
  FaCamera,
} from "react-icons/fa";
import "../styles/user.scss";

const User = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [userData, setUserData] = useState({
    name: "John Doe",
    email: "johndoe@example.com",
    username: "johndoe123",
    role: "User",
    bio: "I'm a software developer passionate about creating intuitive user experiences.",
    avatar:
      "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  });
  const [errors, setErrors] = useState({});
  const [isSuccess, setIsSuccess] = useState(false);
  const [isError, setIsError] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData({ ...userData, [name]: value });
    validateField(name, value);
  };

  const validateField = (name, value) => {
    let error = "";
    switch (name) {
      case "name":
        if (!value) error = "Name is required";
        break;
      case "email":
        if (!value) {
          error = "Email is required";
        } else if (!/\S+@\S+\.\S+/.test(value)) {
          error = "Email is invalid";
        }
        break;
      case "username":
        if (!value) error = "Username is required";
        break;
      case "role":
        if (!value) error = "Role is required";
        break;
      default:
        break;
    }
    setErrors((prevErrors) => ({ ...prevErrors, [name]: error }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formErrors = Object.values(errors).filter((error) => error);
    if (formErrors.length === 0) {
      try {
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setIsSuccess(true);
        setIsError(false);
        setIsEditing(false);
        setTimeout(() => setIsSuccess(false), 3000);
      } catch (error) {
        console.error("Error updating user:", error);
        setIsError(true);
        setIsSuccess(false);
      }
    }
  };

  const toggleEdit = () => {
    setIsEditing(!isEditing);
    setIsSuccess(false);
    setIsError(false);
  };

  return (
    <div className="user-profile">
      <div className="user-header">
        <h2>User Profile</h2>
        <button onClick={toggleEdit} className="edit-button">
          {isEditing ? (
            <>
              <FaSave className="icon" /> Save
            </>
          ) : (
            <>
              <FaEdit className="icon" /> Edit Profile
            </>
          )}
        </button>
      </div>

      <div className="user-content">
        <div className="user-avatar">
          <img src={userData.avatar} alt="Profile" />
          {isEditing && (
            <label htmlFor="avatar-upload" className="avatar-edit">
              <FaCamera size={24} />
              <input id="avatar-upload" type="file" className="hidden" />
            </label>
          )}
        </div>

        <div className="user-form">
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <input
                type="text"
                name="name"
                id="name"
                placeholder="Full Name"
                value={userData.name}
                onChange={handleChange}
                disabled={!isEditing}
                className={errors.name ? "input-error" : ""}
              />
              <FaUser className="input-icon" />
              {errors.name && <p className="error">{errors.name}</p>}
            </div>

            <div className="input-group">
              <input
                type="email"
                name="email"
                id="email"
                placeholder="Email Address"
                value={userData.email}
                onChange={handleChange}
                disabled={!isEditing}
                className={errors.email ? "input-error" : ""}
              />
              <FaEnvelope className="input-icon" />
              {errors.email && <p className="error">{errors.email}</p>}
            </div>

            <div className="input-group">
              <input
                type="text"
                name="username"
                id="username"
                placeholder="Username"
                value={userData.username}
                onChange={handleChange}
                disabled={!isEditing}
                className={errors.username ? "input-error" : ""}
              />
              <FaIdCard className="input-icon" />
              {errors.username && <p className="error">{errors.username}</p>}
            </div>

            <div className="input-group">
              <select
                id="role"
                name="role"
                value={userData.role}
                onChange={handleChange}
                disabled={!isEditing}
                className={errors.role ? "input-error" : ""}
              >
                <option value="" disabled>
                  Select a role
                </option>
                <option value="Admin">Admin</option>
                <option value="User">User</option>
                <option value="Editor">Editor</option>
              </select>
              {errors.role && <p className="error">{errors.role}</p>}
            </div>

            <div className="input-group">
              <textarea
                name="bio"
                id="bio"
                rows="4"
                placeholder="Bio"
                value={userData.bio}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            {isEditing && <button type="submit">Update Profile</button>}
          </form>
        </div>
      </div>

      {isSuccess && (
        <div className="message success">
          <span>Profile updated successfully!</span>
          <FaCheck className="message-icon" />
        </div>
      )}

      {isError && (
        <div className="message error">
          <span>Error updating profile. Please try again.</span>
          <FaTimes className="message-icon" />
        </div>
      )}
    </div>
  );
};

export default User;
