/* eslint-disable react/prop-types */
import { IconButton, Stack, Table, TableBody, TableCell, TableHead, TableRow } from '@mui/material';
import React from 'react';
import { useEffect, useState } from 'react';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

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

const SiteTable = ({ sites, setDeleteSiteId, setUpdateSiteId }) => {
  const header = ['ID', 'name', 'url', 'API KEY', 'creation date', 'Actions'];
  const [data, setData] = useState([]);

  useEffect(() => {
    const sitesFormatted = sites.map((site, index) => {
      return {
        id: index + 1,
        name: site.name,
        url: site.url,
        api_key: site.apiKey,
        date: site.creationDate,
        actions: (
          <Stack direction="row" gap="10px">
            <IconButton onClick={() => setUpdateSiteId(site.id)}>
              <EditIcon />
            </IconButton>
            <IconButton onClick={() => setDeleteSiteId(site.id)}>
              <DeleteIcon />
            </IconButton>
          </Stack>
        )
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

export default SiteTable;
