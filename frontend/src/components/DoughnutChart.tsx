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
        display: false
      }
    },
    title: {
      display: false,
      text: title
    }
  };

  const data = {
    labels: labels,
    datasets: [
      {
        label: '# of Votes',
        data: events || [12, 19, 3, 5],
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
