/* eslint-disable react/prop-types */
import React, { useState } from 'react';
import { Alert, Box, Button, TextField } from '@mui/material';
import { createSite, updateSiteById } from '../../../../../../api/sites.services';

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

const SiteForm = React.forwardRef(({ setAlert, closeModal, id, isUpdate = false }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [url, setUrl] = useState('');
  const [isError, setIsError] = useState(false);
  const [wrongUrl, setWrongUrl] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsError(false);
    setWrongUrl(false);
    const fetchFunction = isUpdate
      ? updateSiteById(id, name, url)
      : createSite(id, name, description, url);

    fetchFunction
      .then((resp) => {
        console.log(resp);
        closeModal();
        setAlert(true);
        setTimeout(() => {
          setAlert(false);
        }, 2000);
      })
      .catch((err) => {
        console.log(err);
        if (err?.response?.data?.message.toLowerCase().includes('url')) {
          setWrongUrl(true);
        } else {
          setIsError(true);
        }
      });
  };

  const defaultCondition = !name.length > 0 || !description.length > 0 || !url.length > 0;

  const updateCondition = !name.length > 0 && !url.length > 0;

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
      {!isUpdate && (
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
      )}
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
        disabled={isUpdate ? updateCondition : defaultCondition}>
        Submit
      </Button>
      {isError && <Alert severity="warning">Error while creating site, try again.</Alert>}
    </Box>
  );
});

SiteForm.displayName = 'SiteForm';

export default SiteForm;
