/* eslint-disable react/prop-types */
import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Filler,
  Legend
} from 'chart.js';
import { Chart } from 'react-chartjs-2';
import { faker } from '@faker-js/faker';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Filler,
  Legend
);

const STATIC_LABELS = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December'
];

export const AreaChart = ({ labels = STATIC_LABELS, events, title = 'Events' }) => {
  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      },
      title: {
        display: true,
        text: title,
        color: 'black'
      }
    },
    scales: {
      y: {
        ticks: { color: 'black', beginAtZero: true, precision: 0 }
      },
      x: {
        ticks: { color: 'black', beginAtZero: true, precision: 0 }
      }
    }
  };

  const data = {
    labels,
    datasets: [
      {
        fill: true,
        label: '',
        data: events || labels.map(() => faker.datatype.number({ min: 0, max: 1000 })),
        borderColor: 'rgb(53, 162, 235)',
        backgroundColor: 'rgba(53, 162, 235, 0.5)',
        color: '#fff'
      }
    ]
  };

  return <Chart width={'100%'} height={25} type="line" data={data} options={options} />;
};
