/* eslint-disable react/prop-types */
/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useEffect, useState, memo, useCallback } from 'react';
import {
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TablePagination,
  Checkbox
} from '@mui/material';
import { getLatestEventsByUserId } from '../../../../../../api/events.service';
import { AuthContext } from '../../../../../../contexts/AuthProvider';

const HEADER = ['ID', 'Url', 'SiteName', 'Time', 'Ip Adress', 'clientUserAgent'];

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

const AllEventsTable = memo(({ type }) => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [selectedRows, setSelectedRows] = useState([]);
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState();
  const [totalElements, setTotalElements] = useState();
  const isCheckbox = false;
  const header = HEADER;
  const { user } = React.useContext(AuthContext);

  const handleSelectAllClick = (event) => {
    if (event.target.checked) {
      const newSelecteds = data.map((row) => row.id);
      setSelectedRows(newSelecteds);
      return;
    }
    setSelectedRows([]);
  };

  const handleClick = (event, id) => {
    const selectedIndex = selectedRows.indexOf(id);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selectedRows, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selectedRows.slice(1));
    } else if (selectedIndex === selectedRows.length - 1) {
      newSelected = newSelected.concat(selectedRows.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selectedRows.slice(0, selectedIndex),
        selectedRows.slice(selectedIndex + 1)
      );
    }

    setSelectedRows(newSelected);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
  };

  const isSelected = (id) => selectedRows.indexOf(id) !== -1;

  const fetchAllEvents = useCallback(async () => {
    const resp = await getLatestEventsByUserId(user.id, type, page, rowsPerPage);
    setTotalPages(resp.totalPages);
    setTotalElements(resp.totalElements);
    const newData = resp.data.map((e, index) => ({
      id: index + 1 + rowsPerPage * page,
      url: e.site.url,
      site_name: e.site.name,
      time: e.clientTime,
      ipAddress: e.ipAddress,
      clientUserAgent: e.clientUserAgent
    }));

    setData((s) => newData);
  }, [user, page, rowsPerPage, type]);

  useEffect(() => {
    if (user) {
      fetchAllEvents();
    }
  }, [user, page, rowsPerPage, type]);

  return data?.length > 0 ? (
    <TableContainer component={Paper} style={styles.container}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table" style={styles.table}>
        <TableHead>
          <TableRow>
            {isCheckbox && (
              <TableCell padding="checkbox">
                <Checkbox
                  indeterminate={selectedRows.length > 0 && selectedRows.length < data.length}
                  checked={selectedRows.length === data.length}
                  onChange={handleSelectAllClick}
                  inputProps={{ 'aria-label': 'select all desserts' }}
                />
              </TableCell>
            )}
            {header.map((item, index) => (
              <TableCell key={index}>{item}</TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody style={styles.body}>
          {data.map((row) => {
            const isRowSelected = isSelected(row.id);

            return (
              <TableRow
                key={row.id}
                hover
                onClick={(event) => isCheckbox && handleClick(event, row.id)}
                role="checkbox"
                aria-checked={isRowSelected}
                tabIndex={-1}
                selected={isRowSelected}>
                {isCheckbox && (
                  <TableCell padding="checkbox" align="center">
                    <Checkbox
                      checked={isRowSelected}
                      onChange={(event) => handleClick(event, row.id)}
                      inputProps={{ 'aria-labelledby': `row-${row.id}-checkbox` }}
                    />
                  </TableCell>
                )}
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
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[5, 10, 25]}
      />
    </TableContainer>
  ) : (
    'Loading ...'
  );
});

AllEventsTable.displayName = 'AllEventsTable';

export default AllEventsTable;
