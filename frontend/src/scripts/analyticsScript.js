// click
window.addEventListener('click', function (event) {
  let element = event.target;
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

  //if (event.target.tagName.toLowerCase() === "button") {}
  // Envoyer un événement à l'API
  /*console.log({
    innerText: event.target.innerText || '',
    cssSelector: path.join(' > '),
    time: Date.now()
  });*/
});

// page changes
window.addEventListener('popstate', function () {
  // Envoyer un événement à l'API
  /* console.log({
    eventType: 'pageChange',
    url: window.location.href,
    timestamp: Date.now()
  });*/
});

// window resize
window.addEventListener('resize', function () {
  // Envoyer un événement à l'API
  /*console.log({
    eventType: 'windowResize',
    width: window.innerWidth,
    height: window.innerHeight,
    timestamp: Date.now()
  });*/
});

//pro­vide a glob­al func­tion to al­low send­ing cus­tom events to the API.
/*function sendCustomEvent(eventObj) {
  // Format the event data into a JSON string
  const url = 'https://example.com/api/custom-event';

  // Send the data to the API using a POST request
  fetch(url, {
    method: 'POST',
    body: JSON.stringify({
      ...eventObj
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  })
    .then((response) => {
      // Handle the API response
    })
    .catch((error) => {
      // Handle the error
    });
}*/
