/* eslint-disable react/prop-types */
import React, { useContext, useEffect } from 'react';
import { Alert, Box, Button, Modal, Paper, Stack, Typography } from '@mui/material';
import { useState } from 'react';
import './SiteManagement.css';
import { AuthContext } from '../../../../../contexts/AuthProvider';
import { deleteSiteById, getSitesByUserId } from '../../../../../api/sites.services';
import { CopyBlock, dracula } from 'react-code-blocks';
import analyticsScript from 'raw-loader!../../../../../scripts/analyticsScript.js';
import { SiteTable } from './components';
import SiteForm from './components/SiteForm';

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

const SiteManagement = () => {
  const [open, setOpen] = useState(false);
  const [alertCreatedSite, setAlertCreatedSite] = useState(false);
  const [alertDeletedSite, setAlertDeletedSite] = useState(false);
  const [alertUpdatedSite, setAlertUpdatedSite] = useState(false);
  const [sites, setSites] = useState([]);
  const { user } = useContext(AuthContext);
  const [deleteSiteId, setDeleteSiteId] = useState('');
  const [updateSiteId, setUpdatedSiteId] = useState('');

  const handleOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
    setDeleteSiteId('');
    setUpdatedSiteId('');
  };

  const fetchSite = async () => {
    if (user?.id) {
      const resp = await getSitesByUserId(user.id);
      setSites(resp);
    }
  };

  const deleteSite = async () => {
    await deleteSiteById(deleteSiteId)
      .then(() => {
        handleClose();
        setAlertDeletedSite(true);
        setTimeout(() => {
          setAlertDeletedSite(false);
        }, 2000);
        setDeleteSiteId('');
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    fetchSite();
  }, []);
  useEffect(() => {
    if (alertCreatedSite || alertDeletedSite || alertUpdatedSite) {
      fetchSite();
    }
  }, [alertCreatedSite, alertDeletedSite, alertUpdatedSite]);

  return (
    <Box className="site-management-container">
      {sites?.length > 0 && (
        <Stack direction="row" justifyContent="space-between" alignItems="flex-end">
          <Typography variant="body1" fontStyle="italic" fontWeight="bold">
            3 sites Max*
          </Typography>
          <Button
            sx={{ marginBottom: '15px' }}
            variant="contained"
            onClick={handleOpen}
            disabled={sites.length >= 3}>
            Create Site
          </Button>
          {user && (
            <Modal
              open={open}
              onClose={handleClose}
              aria-labelledby="modal-modal-title"
              aria-describedby="modal-modal-description">
              <SiteForm
                id={user.id}
                closeModal={() => setOpen(false)}
                setAlert={setAlertCreatedSite}
              />
            </Modal>
          )}
          {deleteSiteId && (
            <Modal
              open={deleteSiteId.length > 0}
              onClose={handleClose}
              aria-labelledby="modal-modal-title"
              aria-describedby="modal-modal-description">
              <Box component="form" sx={style}>
                <Typography>Are you sur you want to delete the site ?</Typography>
                <Stack direction="row" gap="10px" justifyContent="flex-end">
                  <Button onClick={() => setDeleteSiteId('')}>Cancel</Button>
                  <Button onClick={deleteSite}>Confirm</Button>
                </Stack>
              </Box>
            </Modal>
          )}
          {updateSiteId && (
            <Modal
              open={updateSiteId.length > 0}
              onClose={handleClose}
              aria-labelledby="modal-modal-title"
              aria-describedby="modal-modal-description">
              <SiteForm
                id={updateSiteId}
                closeModal={handleClose}
                setAlert={setAlertUpdatedSite}
                isUpdate
              />
            </Modal>
          )}
        </Stack>
      )}

      {alertCreatedSite && <Alert severity="success">Success in creating the site</Alert>}
      {alertDeletedSite && <Alert severity="warning">Deleting the site</Alert>}
      {alertUpdatedSite && <Alert severity="info">Update successful</Alert>}
      <Paper elevation={3}>
        {sites?.length > 0 && (
          <SiteTable
            sites={sites}
            setDeleteSiteId={setDeleteSiteId}
            setUpdateSiteId={setUpdatedSiteId}
          />
        )}
      </Paper>
      <Stack mt={4}>
        <Typography variant="h5" fontWeight="bold">
          Script Code
        </Typography>
        <Typography variant="body1" mb={2}>
          Copy this script and implement it on your website with the correct API_KEY to receive
          metrics on your dasboard
        </Typography>
        <CopyBlock
          language="js"
          text={analyticsScript}
          showLineNumbers={true}
          theme={dracula}
          wrapLines={true}
          codeBlock
        />
      </Stack>
    </Box>
  );
};

export default SiteManagement;
