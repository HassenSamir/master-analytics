import React, { useEffect, useState } from 'react';
import { Paper, Typography, Stack, CircularProgress } from '@mui/material';
import { getEventsMetrics, getEventsMetricsPeriod } from '../../../../api/events.service';
import './GlobalView.css';
import PropTypes from 'prop-types';
import { AuthContext } from '../../../../contexts/AuthProvider';
import MouseIcon from '@mui/icons-material/Mouse';
import FindInPageIcon from '@mui/icons-material/FindInPage';
import AspectRatioIcon from '@mui/icons-material/AspectRatio';
import TuneIcon from '@mui/icons-material/Tune';
import { AreaChart, BarChart } from '../../../../components';

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

  useEffect(() => {
    if (user) {
      fetchEventMetrics();
      fetchEventsMetricsPeriod();
    }
  }, []);
  useEffect(() => {
    console.log(eventsMetricsPeriod);
  }, [eventsMetricsPeriod]);

  return (
    <div className="globalview-container">
      <div className="globalview-first-section">
        <EventMetricsCards data={eventsTotalMetrics} />
        <Stack>
          {eventsMetricsPeriod && (
            <AreaChart
              title={eventsMetricsPeriod.title}
              labels={eventsMetricsPeriod.labels}
              events={eventsMetricsPeriod.values}
            />
          )}
        </Stack>
      </div>
      <div className="globalview-second-section">
        <Stack sx={{ backgroundColor: 'orange' }}>
          <BarChart />
        </Stack>
        <Stack sx={{ backgroundColor: 'cyan', height: '100%' }}>
          <Typography>POURCENTAGE</Typography>
        </Stack>
      </div>
      <div className="globalview-third-section">HELLO</div>
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
