'use strict';
// Create map with center on USA.
var map = L.map('map', {
    center: [39.50, -98.35],
    zoom: 5
});

// Add tile layer to map.
L.tileLayer('http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="http://cartodb.com/attributions">CartoDB</a>',
    maxZoom: 7
}).addTo(map);

var statesLayer = L.geoJson().addTo(map);
statesLayer.addData(states);


// L.geoJson(states, {
//     style: {
//         color: '#808080'
//     },
//     onEachFeature: function(feature) {
//         statesFeatures.push(feature);
//     }
// }).addTo(map);


// L.polygon([
//     [30.509, -0.08],
//     [60.503, -0.06],
//     [51.51, 30]
// ]).addTo(map).bindTooltip("0", {
//     permanent: true,
//     direction: 'center',
//     opacity: 0.85
// }).openTooltip();

var stompClient = null;
var socket = new SockJS('/elections-io');
stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    // console.log(frame);
    // console.log('Connected: ' + frame);
    // stompClient.subscribe('/game/greetings', function (greeting) {
    //     console.log(JSON.parse(greeting.body).content);
    //     // console.log(greeting);
    // });
    stompClient.subscribe('/update-states', function (states) {
        updateStates(states);
    });
});

function updateStates(states) {
    console.log("States updated");
}

// function setConnected(connected) {
//     $("#connect").prop("disabled", connected);
//     $("#disconnect").prop("disabled", !connected);
//     if (connected) {
//         $("#conversation").show();
//     }
//     else {
//         $("#conversation").hide();
//     }
//     $("#greetings").html("");
// }
//
// function connect() {
//     var socket = new SockJS('/elections-io');
//     stompClient = Stomp.over(socket);
//     stompClient.connect({}, function (frame) {
//         setConnected(true);
//         console.log('Connected: ' + frame);
//         stompClient.subscribe('/game/greetings', function (greeting) {
//             showGreeting(JSON.parse(greeting.body).content);
//         });
//     });
// }

// function disconnect() {
//     if (stompClient != null) {
//         stompClient.disconnect();
//     }
//     setConnected(false);
//     console.log("Disconnected");
// }
//
// function sendName() {
//     stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
// }
//
// function showGreeting(message) {
//     $("#greetings").append("<tr><td>" + message + "</td></tr>");
// }
//
// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//     $( "#connect" ).click(function() { connect(); });
//     $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });