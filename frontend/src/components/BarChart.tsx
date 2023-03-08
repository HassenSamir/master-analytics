/* eslint-disable react/prop-types */
import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import { Chart } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const STATIC_LABELS = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

export const BarChart = ({ labels = STATIC_LABELS, events, title = 'Events' }) => {
  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: false
      }
    },
    title: {
      display: false,
      text: title
    },
    scales: {
      y: {
        ticks: { color: 'white', beginAtZero: true, precision: 0 }
      },
      x: {
        ticks: { color: 'white', beginAtZero: true, precision: 0 }
      }
    }
  };

  const data = {
    labels,
    datasets: [
      {
        fill: true,
        label: '',
        data: events,
        backgroundColor: 'rgba(255, 99, 132, 0.5)'
      }
    ]
  };

  return <Chart width={100} height={40} type="bar" data={data} options={options} />;
};
