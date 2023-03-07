import { Sidebar, Menu, MenuItem, useProSidebar } from 'react-pro-sidebar';
import HomeOutlinedIcon from '@mui/icons-material/HomeOutlined';
import PeopleOutlinedIcon from '@mui/icons-material/PeopleOutlined';
import ContactsOutlinedIcon from '@mui/icons-material/ContactsOutlined';
import ReceiptOutlinedIcon from '@mui/icons-material/ReceiptOutlined';
import CalendarTodayOutlinedIcon from '@mui/icons-material/CalendarTodayOutlined';
import HelpOutlineOutlinedIcon from '@mui/icons-material/HelpOutlineOutlined';
import MenuOutlinedIcon from '@mui/icons-material/MenuOutlined';
import { IconButton, InputBase, Paper, Stack, Typography } from '@mui/material';
import './Dashboard.css';
import { UserMenuInfo } from '../../components';
import PropTypes from 'prop-types';
import SearchIcon from '@mui/icons-material/Search';
import { useEffect, useState } from 'react';
import {
  ClickView,
  CustomView,
  GlobalView,
  PageChangeView,
  ResizeView,
  SiteManagement,
  SiteOverView
} from './components';

function Dashboard({ user }) {
  const { collapseSidebar } = useProSidebar();
  const STATIC_TABS = {
    dashboard: {
      currentTab: false,
      title: 'DASHBOARD',
      description: 'Welcome to your dashboard',
      body: <GlobalView />
    },
    click: {
      currentTab: false,
      title: 'click',
      description: 'Welcome to your dashboard',
      body: <ClickView />
    },
    page_change: {
      currentTab: false,
      title: 'page_change',
      description: 'Welcome to your dashboard',
      body: <PageChangeView />
    },
    resize: {
      currentTab: false,
      title: 'resize',
      description: 'Welcome to your dashboard',
      body: <ResizeView />
    },
    custom: {
      currentTab: false,
      title: 'Custom',
      description: 'Welcome to your dashboard',
      body: <CustomView />
    },
    site_management: {
      currentTab: false,
      title: 'site_management',
      description: 'Welcome to your dashboard',
      body: <SiteManagement />
    },
    site_overview: {
      currentTab: false,
      title: 'site_overview',
      description: 'Welcome to your dashboard',
      body: <SiteOverView />
    }
  };
  const [tabs, setTabs] = useState();
  // eslint-disable-next-line no-unused-vars
  const [currentTab, setCurrentTab] = useState();

  const handleMenuItemClick = (name, obj) => {
    setTabs({
      ...STATIC_TABS,
      [name]: {
        ...obj,
        currentTab: true
      }
    });
    setCurrentTab({
      ...obj,
      currentTab: true
    });
  };

  useEffect(() => {
    setTabs({
      ...STATIC_TABS,
      dashboard: {
        ...STATIC_TABS.dashboard,
        currentTab: true
      }
    });
    setCurrentTab({
      ...STATIC_TABS.dashboard,
      currentTab: true
    });
  }, []);

  return (
    <div id="dashboard" className="dashboard-container">
      {tabs && (
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

            <MenuItem
              onClick={() => handleMenuItemClick('dashboard', tabs.dashboard)}
              style={{ backgroundColor: tabs.dashboard.currentTab && 'red', marginTop: '25px' }}
              icon={<HomeOutlinedIcon />}>
              Dashboard
            </MenuItem>
            <Typography sx={{ backgroundColor: 'gray', paddingX: '20px', marginTop: '25px' }}>
              Events
            </Typography>
            <MenuItem
              onClick={() => handleMenuItemClick('click', tabs.click)}
              style={{ backgroundColor: tabs.click.currentTab && 'red' }}
              icon={<PeopleOutlinedIcon />}>
              Click
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('page_change', tabs.page_change)}
              style={{ backgroundColor: tabs.page_change.currentTab && 'red' }}
              icon={<ContactsOutlinedIcon />}>
              Page Change
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('resize', tabs.resize)}
              style={{ backgroundColor: tabs.resize.currentTab && 'red' }}
              icon={<ReceiptOutlinedIcon />}>
              Resize
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('custom', tabs.custom)}
              style={{ backgroundColor: tabs.custom.currentTab && 'red' }}
              icon={<HelpOutlineOutlinedIcon />}>
              Custom
            </MenuItem>
            <Typography sx={{ backgroundColor: 'gray', paddingX: '20px', marginTop: '25px' }}>
              Sites
            </Typography>
            <MenuItem
              onClick={() => handleMenuItemClick('site_overview', tabs.site_overview)}
              style={{ backgroundColor: tabs.site_overview.currentTab && 'red' }}
              icon={<CalendarTodayOutlinedIcon />}>
              Overview
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('site_management', tabs.site_management)}
              style={{ backgroundColor: tabs.site_management.currentTab && 'red' }}
              icon={<CalendarTodayOutlinedIcon />}>
              Management
            </MenuItem>
          </Menu>
        </Sidebar>
      )}
      <main className="dashboard-content">
        <Stack direction="row" className="dashboard-content-nav-bar">
          <Paper
            component="form"
            sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 250, height: '70%' }}>
            <InputBase
              sx={{ ml: 1, flex: 1 }}
              placeholder="Recherche"
              inputProps={{ 'aria-label': 'recherche' }}
            />
            <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
              <SearchIcon />
            </IconButton>
          </Paper>
          <UserMenuInfo user={user} />
        </Stack>
        <Stack className="dashboard-content-descriptions">
          <Typography variant="h4" fontWeight="bold">
            {currentTab?.title || '...'}
          </Typography>
          <Typography variant="body1">{currentTab?.description || '...'}</Typography>
        </Stack>
        {currentTab?.body && <Stack className="dashboard-current-tab">{currentTab.body}</Stack>}
      </main>
    </div>
  );
}

export default Dashboard;

Dashboard.propTypes = {
  user: PropTypes.object
};
