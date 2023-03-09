import React, { useContext, useState } from 'react';
import { Paper, Typography, TextField, Button, Stack } from '@mui/material';
import { styled } from '@mui/material/styles';
import './Login.css';
import { userSignIn } from '../../api/auth.service';
import { useNavigate } from 'react-router-dom';
import { ROLE } from '../../utils/utils';
import NotLoggedNavBar from '../../components/notloggednavbar/NotLoggedNavBar';
import { AuthContext } from '../../contexts/AuthProvider';

const Wrapper = styled('div')(({ theme }) => ({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  marginTop: theme.spacing(8)
}));

const Login = () => {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const [username, setUsername] = useState();
  const [password, setPassword] = useState();

  const handleClick = (e) => {
    e.preventDefault();
    userSignIn(username, password).then(
      (user) => {
        login(user);
        if (user.accessToken) {
          localStorage.setItem('token', user.accessToken);
        }
        if (user.roles.includes(ROLE.ADMIN)) {
          navigate('/admin');
        } else {
          navigate('/dashboard');
        }
      },
      (error) => {
        console.log(error);
      }
    );
  };

  return (
    <>
      <NotLoggedNavBar />
      <main className="login-container">
        <Paper elevation={1} className="login-paper">
          <Wrapper>
            <Typography variant="h4" component="h1" gutterBottom>
              Connexion
            </Typography>
            <Stack direction="column">
              <TextField
                required
                fullWidth
                id="username"
                label="Nom d'utilisateur"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
              />
              <TextField
                required
                fullWidth
                type="password"
                id="password"
                label="Mot de passe"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
              <Button type="submit" variant="contained" color="primary" onClick={handleClick}>
                Se connecter
              </Button>
            </Stack>
          </Wrapper>
        </Paper>
      </main>
    </>
  );
};

export default Login;
