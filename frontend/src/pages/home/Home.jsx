import { Stack, Typography } from '@mui/material';
import React from 'react';
import './Home.css';
import NotLoggedNavBar from '../../components/notloggednavbar/NotLoggedNavBar';
import rightContentImg from '../../assets/home-image.png';

const Home = () => {
  return (
    <>
      <NotLoggedNavBar />
      <div className="home-container">
        <div className="home-left-content">
          <div className="home-description">
            <Typography variant="h2" fontWeight="bold">
              Grow your business with next-level marketing analytics
            </Typography>
            <Typography variant="h6" fontStyle="italic" color="#A2A8BC">
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Aspernatur omnis nulla
              praesentium provident doloribus porro quasi eum odio voluptate similique iste. Neque,
              ex praesentium!
            </Typography>
          </div>
        </div>
        <Stack className="home-right-content">
          <img
            className="home-right-content-img"
            src={rightContentImg}
            alt="homepage-analytics-bg"
          />
        </Stack>
      </div>
    </>
  );
};

export default Home;
