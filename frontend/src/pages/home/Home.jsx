import { Button, Paper, Stack, Typography } from '@mui/material';
import React from 'react';
import './Home.css';
import img from '../../assets/home-main-bg.png';

const Auth = () => {
  return (
    <Stack direction="row" className="home-auth">
      <Button variant="outlined">LogIn</Button>
      <Button variant="outlined">SignIn</Button>
    </Stack>
  );
};

const Home = () => {
  return (
    <Stack className="home-container">
      <Stack className="home-navbar">
        <Auth />
      </Stack>
      <Stack direction="row" className="home-content">
        <img src={img} className="home-bg" />
        <Paper className="home-description">
          <Stack direction="column">
            <Typography variant="h1">Master Analytics</Typography>
            <Typography variant="h5">
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Aspernatur omnis nulla
              praesentium provident doloribus porro quasi eum odio voluptate similique iste. Neque,
              ex praesentium!
            </Typography>
          </Stack>
        </Paper>
      </Stack>
    </Stack>
  );
};

export default Home;
