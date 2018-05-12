window.addEventListener('load', function() {
  var content = document.querySelector('.content');
  var loadingSpinner = document.getElementById('loading');
  content.style.display = 'block';
  loadingSpinner.style.display = 'none';

  var apiUrl = 'http://localhost:4000';
  var requestedScopes = 'openid profile read:book write:book';

  var webAuth = new auth0.WebAuth({
    domain: AUTH0_DOMAIN,
    clientID: AUTH0_CLIENT_ID,
    redirectUri: AUTH0_CALLBACK_URL,
    audience: AUTH0_AUDIENCE,
    responseType: 'token id_token',
    scope: requestedScopes,
    leeway: 60
  });

  var homeView = document.getElementById('home-view');
  var pingView = document.getElementById('ping-view');

  // buttons and event listeners
  var loginBtn = document.getElementById('btn-login');
  var logoutBtn = document.getElementById('btn-logout');

  var homeViewBtn = document.getElementById('btn-home-view');
  var pingViewBtn = document.getElementById('btn-ping-view');

  var pingPrivate = document.getElementById('btn-ping-private');

  var pingMessage = document.getElementById('ping-message');
  var adminMessage = document.getElementById('ping-message');

  pingPrivate.addEventListener('click', function() {
    callAPI('/books', true, 'GET', function(err, response) {
      if (err) {
        alert(err);
        return;
      }
      // update message
      document.querySelector('#ping-view .ping-content').innerHTML = `<pre>${JSON.stringify(response, null, 2)}</pre>`;
    });
  });

  loginBtn.addEventListener('click', login);
  logoutBtn.addEventListener('click', logout);

  homeViewBtn.addEventListener('click', function() {
    homeView.style.display = 'inline-block';
    pingView.style.display = 'none';
  });


  pingViewBtn.addEventListener('click', function() {
    homeView.style.display = 'none';
    pingView.style.display = 'inline-block';
  });

  function login() {
    webAuth.authorize();
  }

  function setSession(authResult) {
    // Set the time that the access token will expire at
    var expiresAt = JSON.stringify(
      authResult.expiresIn * 1000 + new Date().getTime()
    );

    // If there is a value on the `scope` param from the authResult,
    // use it to set scopes in the session for the user. Otherwise
    // use the scopes as requested. If no scopes were requested,
    // set it to nothing
    const scopes = authResult.scope || requestedScopes || '';

    localStorage.setItem('access_token', authResult.accessToken);
    localStorage.setItem('id_token', authResult.idToken);
    localStorage.setItem('expires_at', expiresAt);
    localStorage.setItem('scopes', JSON.stringify(scopes));
  }

  function logout() {
    // Remove tokens and expiry time from localStorage
    localStorage.removeItem('access_token');
    localStorage.removeItem('id_token');
    localStorage.removeItem('expires_at');
    localStorage.removeItem('scopes');
    pingMessage.style.display = 'none';
    adminMessage.style.display = 'none';
    displayButtons();
  }

  function isAuthenticated() {
    // Check whether the current time is past the
    // access token's expiry time
    var expiresAt = JSON.parse(localStorage.getItem('expires_at'));
    return new Date().getTime() < expiresAt;
  }

  function displayButtons() {
    var loginStatus = document.querySelector('.container h4');
    if (isAuthenticated()) {
      loginBtn.style.display = 'none';
      logoutBtn.style.display = 'inline-block';
      pingViewBtn.style.display = 'inline-block';
      pingPrivate.style.display = 'inline-block';
      loginStatus.innerHTML =  `You are logged in!`;

      var loginContent = document.querySelector('.container .content-poc');
      loginContent.innerHTML =  `<b>Your id_token is </b> ${localStorage.getItem('id_token')}`;
      loginContent.innerHTML +=  `<br /><br />`;
      loginContent.innerHTML +=  `<b>Your access_token is </b> ${localStorage.getItem('access_token')}`;
    } else {
      homeView.style.display = 'inline-block';
      loginBtn.style.display = 'inline-block';
      logoutBtn.style.display = 'none';
      pingViewBtn.style.display = 'none';
      pingView.style.display = 'none';
      pingPrivate.style.display = 'none';
      loginStatus.innerHTML =
        'You are not logged in! Please log in to continue.';
    }
  }


  function handleAuthentication() {
    webAuth.parseHash(function(err, authResult) {
      if (authResult && authResult.accessToken && authResult.idToken) {
        window.location.hash = '';
        setSession(authResult);
        loginBtn.style.display = 'none';
        homeView.style.display = 'inline-block';
      } else if (err) {
        homeView.style.display = 'inline-block';
        console.log(err);
        alert(
          'Error: ' + err.error + '. Check the console for further details.'
        );
      }
      displayButtons();
    });
  }

  handleAuthentication();

  function callAPI(endpoint, secured, method, cb) {
    var url = apiUrl + endpoint;
    var xhr = new XMLHttpRequest();
    xhr.open(method, url);
    if (secured) {
      xhr.setRequestHeader(
        'Authorization',
        'Bearer ' + localStorage.getItem('access_token')
      );
    }
    xhr.onload = function() {
      if (xhr.status == 200) {
        var message = JSON.parse(xhr.responseText);
        cb(null, message);
      } else {
        cb(xhr.statusText);
      }
    };
    xhr.send();
  }

  displayButtons();
});
