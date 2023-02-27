import { Sidebar, Menu, MenuItem, useProSidebar } from 'react-pro-sidebar';
import HomeOutlinedIcon from '@mui/icons-material/HomeOutlined';
import PeopleOutlinedIcon from '@mui/icons-material/PeopleOutlined';
import ContactsOutlinedIcon from '@mui/icons-material/ContactsOutlined';
import ReceiptOutlinedIcon from '@mui/icons-material/ReceiptOutlined';
import CalendarTodayOutlinedIcon from '@mui/icons-material/CalendarTodayOutlined';
import HelpOutlineOutlinedIcon from '@mui/icons-material/HelpOutlineOutlined';
import MenuOutlinedIcon from '@mui/icons-material/MenuOutlined';
import { IconButton, InputBase, Paper, Stack, Typography } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import './Dashboard.css';

function Dashboard() {
  const { collapseSidebar } = useProSidebar();

  return (
    <div id="dashboard" className="dashboard-container">
      <Sidebar style={{ height: '100vh' }}>
        <Menu>
          <MenuItem
            icon={<MenuOutlinedIcon />}
            onClick={() => {
              collapseSidebar();
            }}
            style={{ textAlign: 'center' }}>
            <h2>Analytics</h2>
          </MenuItem>

          <MenuItem icon={<HomeOutlinedIcon />}>Dashboard</MenuItem>
          <MenuItem icon={<PeopleOutlinedIcon />}>Team</MenuItem>
          <MenuItem icon={<ContactsOutlinedIcon />}>Contacts</MenuItem>
          <MenuItem icon={<ReceiptOutlinedIcon />}>Profile</MenuItem>
          <MenuItem icon={<HelpOutlineOutlinedIcon />}>FAQ</MenuItem>
          <MenuItem icon={<CalendarTodayOutlinedIcon />}>Calendar</MenuItem>
        </Menu>
      </Sidebar>
      <main className="dashboard-content">
        <Stack
          direction="column"
          sx={{
            height: '100%',
            justifyContent: 'center'
          }}>
          <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
            Hello Client
          </Typography>
          <Typography variant="body1" sx={{ color: 'gray' }}>
            blalbalbalbalbalbab
          </Typography>
        </Stack>
        <Paper
          component="form"
          sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 250 }}>
          <InputBase
            sx={{ ml: 1, flex: 1 }}
            placeholder="Recherche"
            inputProps={{ 'aria-label': 'recherche' }}
          />
          <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
            <SearchIcon />
          </IconButton>
        </Paper>
      </main>
    </div>
  );
}

export default Dashboard;
