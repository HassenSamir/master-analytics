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
import { faker } from '@faker-js/faker';
import { Chart } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const STATIC_LABELS = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

export const BarChart = ({ labels = STATIC_LABELS, events, title = 'Events' }) => {
  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top' as const
      },
      title: {
        display: true,
        text: title
      }
    }
  };

  const data = {
    labels,
    datasets: events || [
      {
        label: 'Dataset 1',
        data: labels.map(() => faker.datatype.number({ min: 0, max: 1000 })),
        backgroundColor: 'rgba(255, 99, 132, 0.5)'
      },
      {
        label: 'Dataset 2',
        data: labels.map(() => faker.datatype.number({ min: 0, max: 1000 })),
        backgroundColor: 'rgba(53, 162, 235, 0.5)'
      }
    ]
  };

  return <Chart width={100} height={40} type="bar" data={data} options={options} />;
};
