import { Button, Stack, Typography } from '@mui/material';
import React from 'react';
import { Link } from 'react-router-dom';
import './NotLoggedNavBar.css';

const NotLoggedNavBar = () => {
  return (
    <div className="navbar-container">
      <Stack className="navbar-title">
        <Link>
          <Typography variant="h4">Master Analytics</Typography>
        </Link>
      </Stack>
      <Stack className="navbar-links">
        <Link>
          <Typography>Home1</Typography>
        </Link>
        <Link>
          <Typography>Home1</Typography>
        </Link>
        <Link>
          <Typography>Home1</Typography>
        </Link>
      </Stack>
      <Stack direection="row" className="navbar-btn-container">
        <Link to="/login">
          <Button className="navbar-btn" variant="outlined">
            LogIn
          </Button>
        </Link>
        <Link to="/signin">
          <Button className="navbar-btn" variant="outlined">
            SignIn
          </Button>
        </Link>
      </Stack>
    </div>
  );
};

export default NotLoggedNavBar;
