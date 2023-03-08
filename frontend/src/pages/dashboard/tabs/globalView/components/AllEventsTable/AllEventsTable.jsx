/* eslint-disable react/prop-types */
/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useState } from 'react';
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

/*
const HEADER = ['Type', 'Url', 'SiteName', 'Time', 'clientUserAgent'];

const DATA = [
  {
    type: 'John Doe',
    url: 'john.doe',
    site_name: 'site_name',
    time: 'time',
    userAgent: 'userAgent'
  }
*/
const HEADER = ['ID', 'Name', 'Email', 'Phone'];

const DATA = [
  { id: 1, name: 'John Doe', email: 'john.doe@example.com', phone: '+1-555-1234' },
  { id: 2, name: 'Jane Smith', email: 'jane.smith@example.com', phone: '+1-555-5678' },
  { id: 3, name: 'Bob Johnson', email: 'bob.johnson@example.com', phone: '+1-555-9012' },
  { id: 4, name: 'Alice Lee', email: 'alice.lee@example.com', phone: '+1-555-3456' },
  { id: 5, name: 'Charlie Brown', email: 'charlie.brown@example.com', phone: '+1-555-7890' },
  { id: 6, name: 'Eva Davis', email: 'eva.davis@example.com', phone: '+1-555-2345' },
  { id: 7, name: 'Frank Miller', email: 'frank.miller@example.com', phone: '+1-555-6789' },
  { id: 8, name: 'Grace Kim', email: 'grace.kim@example.com', phone: '+1-555-0123' },
  { id: 9, name: 'Henry Adams', email: 'henry.adams@example.com', phone: '+1-555-4567' },
  { id: 10, name: 'Isabella Brown', email: 'isabella.brown@example.com', phone: '+1-555-8901' },
  { id: 11, name: 'Jack Wilson', email: 'jack.wilson@example.com', phone: '+1-555-2345' },
  { id: 12, name: 'Karen Lee', email: 'karen.lee@example.com', phone: '+1-555-6789' },
  { id: 13, name: 'Larry Johnson', email: 'larry.johnson@example.com', phone: '+1-555-0123' },
  { id: 14, name: 'Mary Johnson', email: 'mary.johnson@example.com', phone: '+1-555-4567' },
  { id: 15, name: 'Nathan Davis', email: 'nathan.davis@example.com', phone: '+1-555-8901' },
  { id: 16, name: 'Olivia Taylor', email: 'olivia.taylor@example.com', phone: '+1-555-2345' },
  { id: 17, name: 'Peter Brown', email: 'peter.brown@example.com', phone: '+1-555-6789' },
  { id: 18, name: 'Rachel Wilson', email: 'rachel.wilson@example.com', phone: '+1-555-0123' },
  { id: 19, name: 'Samuel Lee', email: 'samuel.lee@example.com', phone: '+1-555-4567' },
  { id: 20, name: 'Tina Johnson', email: 'tina.johnson@example.com', phone: '+1-555-8901' }
];

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

const AllEventsTable = ({ header = HEADER, data = DATA, isCheckbox = false }) => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [selectedRows, setSelectedRows] = useState([]);

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
    setPage(0);
  };

  const isSelected = (id) => selectedRows.indexOf(id) !== -1;

  return (
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
          {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => {
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
        count={data.length}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[5, 10, 25]}
      />
    </TableContainer>
  );
};

export default AllEventsTable;
