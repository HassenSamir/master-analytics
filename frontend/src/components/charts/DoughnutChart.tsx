/* eslint-disable react/prop-types */
import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Chart } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

const STATIC_LABELS = ['Red', 'Blue', 'Yellow', 'Green'];
export const DoughnutChart = ({ labels = STATIC_LABELS, events, title = 'Events' }) => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const options = {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        display: true
      }
    },
    title: {
      display: true,
      text: title
    }
  };

  const total = events.reduce((acc, val) => acc + val, 0);

  const data = {
    labels: labels,
    datasets: [
      {
        label: 'Events %',
        data: events.map((val) => ((val / total) * 100).toFixed(2)),
        backgroundColor: [
          'rgba(255, 99, 132, 0.7)',
          'rgba(54, 162, 235, 0.7)',
          'rgba(75, 192, 192, 0.7)',
          'rgba(153, 102, 255, 0.7)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 1
      }
    ]
  };

  return <Chart width={100} height={20} type="doughnut" data={data} options={options} />;
};
