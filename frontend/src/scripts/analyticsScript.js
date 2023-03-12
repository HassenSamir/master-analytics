const API_KEY = ''; // Replace API_KEY with your own API key

const API_URI = 'http://localhost:8080/api/events/';

// Define the maximum time in milliseconds to send events
const MAX_TIME = 10000;

// Define the maximum number of events to send in a single request
const MAX_CLICKS = 10;
const MAX_PAGES_CHANGE = 1;
const MAX_RESIZE = 1;

// Define the event types
const clickType = 'click';
const pageChangeType = 'page_change';
const resizeType = 'resize';
//const customType = 'custom';

// Define arrays to store the events
let clickEvents = [];
let pageChangeEvents = [];
let resizeEvents = [];
//let customEvents = [];

// Define intervals to send the events
let clickInterval;
let resizeInterval;
let pageChangeInterval;
//let customInterval;

// Define a timeout to debounce resize events
let resizeTimeout;

// Send a batch of events to the API
function sendEvents(eventType, eventArr, interval) {
  if (eventArr.length > 0) {
    fetch(`${API_URI}${eventType}/${API_KEY}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(eventArr)
    })
      .then((response) => {
        if (response.status >= 400 && response.status < 500) {
          console.log(response);
          throw new Error(
            'Bad request: Please check your API_KEY and the format of the sent events'
          );
        } else if (response.status >= 500 && response.status < 600) {
          throw new Error('Internal server error: Please try again later');
        } else {
          eventArr = [];
          clearInterval(interval);
          interval = null;
          console.log('Batched request sent successfully');
        }
      })
      .catch((error) => {
        console.error('Error sending POST request:', error);
      });
  }
}

function handleEvents(eventType, eventArr, interval, maxEvent) {
  if (eventArr.length >= maxEvent) {
    sendEvents(eventType, eventArr, interval);
  } else if (!interval) {
    interval = setInterval(function () {
      sendEvents(eventType, eventArr, interval);
    }, MAX_TIME);
  }
}

// Track click events
window.addEventListener('click', function (event) {
  let element = event.target;

  // Build an array representing the element's DOM path
  const path = [];
  while (element) {
    const tag = element.tagName;
    let className;
    if (tag === 'svg') {
      className = element.className.baseVal.trim();
    } else {
      className = element.className.trim();
    }
    if (tag && tag !== 'BODY') {
      path.push(tag.toLowerCase() + (className ? '.' + className.replace(/\s+/g, '.') : ''));
    }
    element = element.parentElement;
  }

  // Add the click event to the clickEvents array
  clickEvents.push({
    innerText: event.target.innerText || '',
    cssSelector: path.join(' > '),
    time: Date.now()
  });

  handleEvents(clickType, clickEvents, clickInterval, MAX_CLICKS);
});

// Track page change events
window.addEventListener('popstate', function (event) {
  // Add the page change event to the array
  pageChangeEvents.push({
    oldPage: (event.state && event.state.from) || document.referrer || '',
    newPage: document.location.pathname,
    clientTime: new Date()
  });

  handleEvents(pageChangeType, pageChangeEvents, pageChangeInterval, MAX_PAGES_CHANGE);
});

// Track resize events
window.addEventListener('resize', function () {
  // Clear the resize timeout on every resize event
  clearTimeout(resizeTimeout);

  // Set a new timeout to capture the new screen size after 5 seconds
  resizeTimeout = setTimeout(function () {
    resizeEvents.push({
      screenWidth: window.innerWidth,
      screenHeight: window.innerHeight,
      clientTime: new Date().toISOString()
    });

    handleEvents(resizeType, resizeEvents, resizeInterval, MAX_RESIZE);
  }, 5000);
});

/*
// Track your custom events
window.addEventListener('your event to track', function (event) {
// Apply your custom event tracking logic here
const customObj = {}; // create your custom event object to send to the API
customEvents.push(customObj); // add the custom event object to the customEvents array

// Call handleEvents function to send events to the API 
handleEvents(customType, customEvents, customInterval, MAX_CUSTOM_EVENTS);
});
*/
