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
  SiteOverView,
  Profile
} from './tabs';

function Dashboard() {
  const { collapseSidebar } = useProSidebar();
  const STATIC_TABS = {
    dashboard: {
      currentTab: false,
      title: 'Dashboard',
      description: 'Welcome to your dashboard',
      body: <GlobalView />
    },
    click: {
      currentTab: false,
      title: 'Click',
      description: 'View your page change events',
      body: <ClickView />
    },
    page_change: {
      currentTab: false,
      title: 'Page Change',
      description: 'View your page change events',
      body: <PageChangeView />
    },
    resize: {
      currentTab: false,
      title: 'Resize',
      description: 'View your resize events',
      body: <ResizeView />
    },
    custom: {
      currentTab: false,
      title: 'Custom',
      description: 'View your custom change events',
      body: <CustomView />
    },
    site_management: {
      currentTab: false,
      title: 'Management',
      description: 'Manage your sites',
      body: <SiteManagement />
    },
    site_overview: {
      currentTab: false,
      title: 'Site Overview',
      description: 'Visual of your metrics for your sites',
      body: <SiteOverView />
    },
    profile: {
      currentTab: false,
      title: 'Profile',
      description: 'View your profile',
      body: <Profile />
    }
  };

  const [tabs, setTabs] = useState();
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
        <Sidebar className="dashboard-sidebar" style={{ height: '100vh' }}>
          <Menu className="dashboard-sidebar">
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
              style={{
                backgroundColor: tabs.dashboard.currentTab && '#C7C7C7',
                fontWeight: tabs.dashboard.currentTab && 'bold',
                marginTop: '25px'
              }}
              icon={<HomeOutlinedIcon />}>
              Dashboard
            </MenuItem>
            <Typography fontWeight="bold" sx={{ paddingX: '20px', marginTop: '25px' }}>
              Events
            </Typography>
            <MenuItem
              onClick={() => handleMenuItemClick('click', tabs.click)}
              style={{
                backgroundColor: tabs.click.currentTab && '#C7C7C7',
                fontWeight: tabs.click.currentTab && 'bold'
              }}
              icon={<PeopleOutlinedIcon />}>
              Click
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('page_change', tabs.page_change)}
              style={{
                backgroundColor: tabs.page_change.currentTab && '#C7C7C7',
                fontWeight: tabs.page_change.currentTab && 'bold'
              }}
              icon={<ContactsOutlinedIcon />}>
              Page Change
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('resize', tabs.resize)}
              style={{
                backgroundColor: tabs.resize.currentTab && '#C7C7C7',
                fontWeight: tabs.resize.currentTab && 'bold'
              }}
              icon={<ReceiptOutlinedIcon />}>
              Resize
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('custom', tabs.custom)}
              style={{
                backgroundColor: tabs.custom.currentTab && '#C7C7C7',
                fontWeight: tabs.custom.currentTab && 'bold'
              }}
              icon={<HelpOutlineOutlinedIcon />}>
              Custom
            </MenuItem>
            <Typography fontWeight="bold" sx={{ paddingX: '20px', marginTop: '25px' }}>
              Sites
            </Typography>
            <MenuItem
              onClick={() => handleMenuItemClick('site_overview', tabs.site_overview)}
              style={{
                backgroundColor: tabs.site_overview.currentTab && '#C7C7C7',
                fontWeight: tabs.site_overview.currentTab && 'bold'
              }}
              icon={<CalendarTodayOutlinedIcon />}>
              Overview
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('site_management', tabs.site_management)}
              style={{
                backgroundColor: tabs.site_management.currentTab && '#C7C7C7',
                fontWeight: tabs.site_management.currentTab && 'bold'
              }}
              icon={<CalendarTodayOutlinedIcon />}>
              Management
            </MenuItem>
            <MenuItem
              onClick={() => handleMenuItemClick('profile', tabs.profile)}
              style={{
                backgroundColor: tabs.profile.currentTab && '#C7C7C7',
                marginTop: '25px',
                fontWeight: tabs.profile.currentTab && 'bold'
              }}
              icon={<CalendarTodayOutlinedIcon />}>
              Profile
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
          <UserMenuInfo />
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
