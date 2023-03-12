import { Button, Stack, Typography } from '@mui/material';
import React from 'react';
import { Link } from 'react-router-dom';
import './NotLoggedNavBar.css';

const NotLoggedNavBar = () => {
  return (
    <Stack direction="row" className="navbar-container">
      <Stack className="navbar-title">
        <Link>
          <Typography variant="h4" fontWeight="bold">
            Master Analytics
          </Typography>
        </Link>
        <Stack className="navbar-links">
          <Link>
            <Typography>Home</Typography>
          </Link>
          <Link>
            <Typography>Plan</Typography>
          </Link>
          <Link>
            <Typography>Integration</Typography>
          </Link>
        </Stack>
      </Stack>
      <Stack direction="row" className="navbar-btn-container">
        <Link to="/signin" style={{ textDecoration: 'none' }}>
          <Button className="navbar-btn-signin">Sign In</Button>
        </Link>
        <Link to="/signup" style={{ textDecoration: 'none' }}>
          <Button className="navbar-btn-signup" variant="outlined">
            Sign Up
          </Button>
        </Link>
      </Stack>
    </Stack>
  );
};

export default NotLoggedNavBar;
