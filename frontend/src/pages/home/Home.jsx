import { Paper, Stack, Typography } from '@mui/material';
import React from 'react';
import './Home.css';

const Home = () => {
  return (
    <Stack className="home-container">
      <Stack direction="row" className="home-content">
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
