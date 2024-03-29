import { Avatar, Fade, IconButton, Menu, MenuItem, Typography } from '@mui/material';
import React from 'react';
import './UserMenuInfo.css';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import PropTypes from 'prop-types';
import { AuthContext } from '../../contexts/AuthProvider';
import LoadingText from '../LoadingText/LoadingText';

const UserMenuInfo = () => {
  const { user, logout } = React.useContext(AuthContext);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="user-menu-container">
      <Avatar alt="Remy Sharp" src="https://mui.com/static/images/avatar/1.jpg" />
      {user ? <Typography>{user?.username || 'Remy Sharp'}</Typography> : <LoadingText />}
      <IconButton
        aria-label="more"
        id="long-button"
        aria-controls={open ? 'long-menu' : undefined}
        aria-expanded={open ? 'true' : undefined}
        aria-haspopup="true"
        onClick={handleClick}>
        <MoreVertIcon />
      </IconButton>
      <Menu
        id="fade-menu"
        MenuListProps={{
          'aria-labelledby': 'fade-button'
        }}
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        TransitionComponent={Fade}>
        <MenuItem onClick={handleLogout}>Logout</MenuItem>
      </Menu>
    </div>
  );
};

export default UserMenuInfo;

UserMenuInfo.propTypes = {
  username: PropTypes.string
};
