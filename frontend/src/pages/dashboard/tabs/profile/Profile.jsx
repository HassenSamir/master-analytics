import { Paper, TextField } from '@mui/material';
import React, { useContext } from 'react';
import './Profile.css';
import { AuthContext } from '../../../../contexts/AuthProvider';

const Profile = () => {
  const { user } = useContext(AuthContext);

  return (
    <div className="profile-container">
      <Paper elevation={3}>
        <TextField
          margin="normal"
          required
          fullWidth
          name="username"
          label="Username"
          type="text"
          id="username"
          autoComplete="current-username"
          value={user.username}
          disabled
        />
        <TextField
          margin="email"
          required
          fullWidth
          name="email"
          label="Email"
          type="email"
          id="email"
          autoComplete="current-email"
          value={user.email}
          disabled
        />
      </Paper>
    </div>
  );
};

export default Profile;
