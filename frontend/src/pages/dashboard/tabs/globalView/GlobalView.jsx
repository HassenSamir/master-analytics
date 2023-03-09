import React, { useEffect, useState } from 'react';
import {
  Paper,
  Typography,
  Stack,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import {
  getEventsMetrics,
  getEventsMetricsPeriod,
  getEventsMetricsBySiteAndUserId
} from '../../../../api/events.service';
import './GlobalView.css';
import PropTypes from 'prop-types';
import { AuthContext } from '../../../../contexts/AuthProvider';
import MouseIcon from '@mui/icons-material/Mouse';
import FindInPageIcon from '@mui/icons-material/FindInPage';
import AspectRatioIcon from '@mui/icons-material/AspectRatio';
import TuneIcon from '@mui/icons-material/Tune';
import { AreaChart, BarChart, DoughnutChart } from '../../../../components';
import { getSitesByUserId } from '../../../../api/sites.services';
import { AllEventsTable } from './components';

const EventCards = ({ title, icon, number }) => {
  return (
    <Paper elevation={3} sx={{ height: '100%' }}>
      <Stack>
        <Stack direction="row" justifyContent="space-between" padding="10px">
          <Typography>{title}</Typography>
          {icon}
        </Stack>
        <Typography fontWeight="bold" fontSize="25px" paddingLeft="10px">
          {number !== undefined ? number : <CircularProgress />}
        </Typography>
      </Stack>
    </Paper>
  );
};

const EventMetricsCards = ({ data }) => {
  return (
    <div className="globalview-events-metrics-card">
      <EventCards title="Total Click" icon={<MouseIcon />} number={data?.click} />
      <EventCards title="Total Page Change" icon={<FindInPageIcon />} number={data?.page_change} />
      <EventCards title="Total Resize" icon={<AspectRatioIcon />} number={data?.resize} />
      <EventCards title="Total Custom" icon={<TuneIcon />} number={data?.custom} />
    </div>
  );
};

const GlobalView = () => {
  const [eventsTotalMetrics, setEventsTotalMetrics] = useState();
  const [eventsMetricsPeriod, setEventsMetricsPeriod] = useState();
  const { user } = React.useContext(AuthContext);
  const [sites, setSites] = useState([]);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [currentSite, setCurrentSite] = useState();
  const [eventsMetricsCurrentSite, setEventsMetricsCurrentSite] = useState();
  const EVENTS_TYPES = [
    { id: 0, value: 'click' },
    { id: 1, value: 'page_change' },
    { id: 2, value: 'resize' }
  ];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [currentType, setCurrentType] = useState(EVENTS_TYPES[0].value);

  const fetchEventMetrics = async () => {
    const data = await getEventsMetrics(user.id);
    console.log(data);
    if (data) {
      setEventsTotalMetrics(data);
    }
  };

  const fetchEventsMetricsPeriod = async () => {
    const resp = await getEventsMetricsPeriod(user.id);
    console.log(resp);
    if (resp?.data) {
      setEventsMetricsPeriod({
        title: `Events - ${resp.period} - ${resp.year}`,
        labels: resp.data.map((x) => x.label),
        values: resp.data.map((x) => x.value)
      });
    }
  };

  const fetchEventsMetricsBySiteAndUserId = async (userId, siteId) => {
    const events = await getEventsMetricsBySiteAndUserId(userId, siteId);
    console.log('fetchEventsMetricsBySiteAndUserId', events);
    const labels = ['Click', 'PageChange', 'Resize', 'Custom'];
    const values = [events.click, events.pageChange, events.resize, events.custom];
    setEventsMetricsCurrentSite({
      labels,
      values
    });
  };

  const fetchSitesForUser = async () => {
    const sites = await getSitesByUserId(user.id);
    console.log('fetchEventsMetricsBySiteAndUserId', sites);
    if (sites) {
      setSites(sites);
      setCurrentSite(sites[0].id);
      fetchEventsMetricsBySiteAndUserId(user.id, sites[0].id);
    }
  };

  const handleCurrSiteUpdate = (e) => {
    console.log(e);
    const currSite = sites.filter((x) => x.id === e.target.value)[0];

    console.log(currSite);
    setCurrentSite(currSite.id);
    fetchEventsMetricsBySiteAndUserId(user.id, currSite.id);
  };

  const handleCurrTypeUpdate = (e) => {
    setCurrentType(e.target.value);
  };

  useEffect(() => {
    if (user) {
      fetchEventMetrics();
      fetchEventsMetricsPeriod();
      fetchSitesForUser();
    }
  }, []);
  useEffect(() => {
    console.log(eventsMetricsPeriod);
  }, [eventsMetricsPeriod]);

  return (
    <div className="globalview-container">
      <div className="globalview-first-section">
        <EventMetricsCards data={eventsTotalMetrics} />
        <Paper elevation={3} sx={{ display: 'flex', width: 'auto', height: '100%' }}>
          {eventsMetricsPeriod && (
            <AreaChart
              title={eventsMetricsPeriod.title}
              labels={eventsMetricsPeriod.labels}
              events={eventsMetricsPeriod.values}
            />
          )}
        </Paper>
      </div>
      {sites?.length > 0 && (
        <Stack>
          <Stack>
            <FormControl variant="filled" sx={{ width: 250 }}>
              <InputLabel id="demo-simple-select-label">Site</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={currentSite}
                label="Site"
                onChange={handleCurrSiteUpdate}>
                {sites.map((s) => {
                  return (
                    <MenuItem key={s.id} value={s.id}>
                      {s.name}
                    </MenuItem>
                  );
                })}
              </Select>
            </FormControl>
          </Stack>

          <div className="globalview-second-section">
            <Paper
              elevation={3}
              sx={{ display: 'flex', width: 'auto', height: '100%', padding: '10px' }}>
              {eventsMetricsCurrentSite && (
                <BarChart
                  labels={eventsMetricsCurrentSite.labels}
                  events={eventsMetricsCurrentSite.values}
                />
              )}
            </Paper>
            <Paper elevation={3} sx={{ height: '100%', width: 'auto', padding: '25px' }}>
              {eventsMetricsCurrentSite && (
                <DoughnutChart
                  labels={eventsMetricsCurrentSite.labels}
                  events={eventsMetricsCurrentSite.values}
                />
              )}
            </Paper>
          </div>
        </Stack>
      )}

      <Stack justifyContent="space-between" sx={{ marginBottom: '-30px' }}>
        <FormControl variant="filled" sx={{ width: 250 }}>
          <InputLabel id="demo-simple-select-label">Last Event Types</InputLabel>
          <Select
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={currentType}
            label="Event Types"
            onChange={handleCurrTypeUpdate}>
            {EVENTS_TYPES.map((s, index) => {
              return (
                <MenuItem key={index} value={s.value}>
                  {s.value}
                </MenuItem>
              );
            })}
          </Select>
        </FormControl>
      </Stack>
      <div className="globalview-third-section">
        <AllEventsTable type={currentType} />
      </div>
    </div>
  );
};

export default GlobalView;

EventMetricsCards.propTypes = {
  data: PropTypes.any
};

EventCards.propTypes = {
  title: PropTypes.string,
  icon: PropTypes.any,
  number: PropTypes.number
};
