/* eslint-disable react/prop-types */
import React, { useContext, useEffect } from 'react';
import {
  Alert,
  Box,
  Button,
  Modal,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField
} from '@mui/material';
import { useState } from 'react';
import './SiteManagement.css';
import { AuthContext } from '../../../../../contexts/AuthProvider';
import { createSite, getSitesByUserId } from '../../../../../api/sites.services';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4
};

const styles = {
  container: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column'
  },
  table: {
    width: '100%',
    flex: '1 1 auto'
  },
  head: {
    backgroundColor: '#3A3A3A',
    color: 'white'
  },
  body: {
    overflowY: 'auto'
  },
  row: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between'
  }
};

const SiteTable = ({ sites }) => {
  const header = ['ID', 'name', 'url', 'API KEY', 'creation date', 'Script', 'Actions'];
  const [data, setData] = useState([]);

  useEffect(() => {
    const sitesFormatted = sites.map((site, index) => {
      return {
        id: index + 1,
        name: site.name,
        url: site.url,
        api_key: site.apiKey,
        date: site.creationDate,
        script: <Button>Action</Button>,
        actions: <Button>Action</Button>
      };
    });
    setData(sitesFormatted);
  }, [sites]);

  return (
    <Table sx={{ minWidth: 650 }} aria-label="simple table" style={styles.table}>
      <TableHead style={styles.head}>
        <TableRow>
          {header.map((item, index) => (
            <TableCell key={index} sx={{ color: 'white' }}>
              {item}
            </TableCell>
          ))}
        </TableRow>
      </TableHead>
      <TableBody style={styles.body}>
        {data.map((row) => {
          return (
            <TableRow key={row.id} hover role="checkbox" tabIndex={-1}>
              {Object.values(row).map((cell, index) => (
                <TableCell key={index} component="th" scope="row">
                  {cell}
                </TableCell>
              ))}
            </TableRow>
          );
        })}
      </TableBody>
    </Table>
  );
};

const SiteForm = React.forwardRef(({ setAlertCreatedSite, closeModal, userId }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [url, setUrl] = useState('');
  const [isError, setIsError] = useState(false);
  const [wrongUrl, setWrongUrl] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsError(false);
    setWrongUrl(false);
    console.log(e);
    if (userId) {
      createSite(userId, name, description, url)
        .then((resp) => {
          console.log(resp);
          closeModal();
          setAlertCreatedSite(true);
          setTimeout(() => {
            setAlertCreatedSite(false);
          }, 3000);
        })
        .catch((err) => {
          console.log(err);
          if (err?.response?.data?.message.toLowerCase().includes('url')) {
            console.log('ERROR URL');
            setWrongUrl(true);
          } else {
            setIsError(true);
          }
        });
    }
  };

  return (
    <Box component="form" noValidate onSubmit={handleSubmit} sx={style}>
      <TextField
        margin="normal"
        required
        fullWidth
        id="name"
        label="Name"
        name="name"
        autoComplete="name"
        autoFocus
        value={name}
        onChange={(event) => setName(event.target.value)}
      />
      <TextField
        margin="normal"
        required
        fullWidth
        name="description"
        label="Description"
        type="description"
        id="description"
        autoComplete="current-description"
        value={description}
        onChange={(event) => setDescription(event.target.value)}
      />
      <TextField
        margin="normal"
        required
        fullWidth
        name="url"
        label="Url"
        type="url"
        id="url"
        autoComplete="current-url"
        value={url}
        onChange={(event) => setUrl(event.target.value)}
      />
      {wrongUrl && <Alert severity="warning">Wrong url, enter a correct url</Alert>}
      <Button
        type="submit"
        fullWidth
        variant="contained"
        sx={{ mt: 3, mb: 2 }}
        disabled={!name.length > 0 || !description.length > 0 || !url.length > 0}>
        Submit
      </Button>
      {isError && <Alert severity="warning">Error while creating site, try again.</Alert>}
    </Box>
  );
});

SiteForm.displayName = 'SiteForm';

const SiteManagement = () => {
  const [open, setOpen] = useState(false);
  const [alertCreatedSite, setAlertCreatedSite] = useState(false);
  const [sites, setSites] = useState([]);
  const { user } = useContext(AuthContext);

  const handleOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  const fetchSite = async () => {
    console.log(sites);
    const resp = await getSitesByUserId(user.id);
    console.log(resp);
    setSites(resp);
  };

  useEffect(() => {
    fetchSite();
  }, []);
  useEffect(() => {
    console.log(alertCreatedSite);
    if (alertCreatedSite) {
      fetchSite();
    }
  }, [alertCreatedSite]);

  return (
    <Box className="site-management-container">
      <Stack direction="row" justifyContent="flex-end" mb={1}>
        <Button variant="contained" onClick={handleOpen}>
          Create Site
        </Button>
        {user && (
          <Modal
            open={open}
            onClose={handleClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description">
            <SiteForm
              userId={user.id}
              closeModal={() => setOpen(false)}
              setAlertCreatedSite={setAlertCreatedSite}
            />
          </Modal>
        )}
      </Stack>
      {alertCreatedSite && <Alert severity="success">Success in creating the site</Alert>}
      <Paper elevation={3}>{sites?.length > 0 && <SiteTable sites={sites} />}</Paper>
    </Box>
  );
};

export default SiteManagement;
