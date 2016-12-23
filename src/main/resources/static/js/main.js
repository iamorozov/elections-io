'use strict';
// Create map with center on USA.
var map = L.map('map', {
    center: [39.50, -98.35],
    zoom: 4,
    // maxBounds: new L.latLngBounds(new L.LatLng(39.5, -98.35), new L.LatLng(61.2, 2.5))
});

const DEFAULT_BLUE = '#398bfb';

// Add tile layer to map.
L.tileLayer('http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="http://cartodb.com/attributions">CartoDB</a>',
    maxZoom: 7
}).addTo(map);

var iam;
var statesLayer = L.geoJson().addTo(map);
statesLayer.addData(states);
var statesMap = {};
var neighborStates = [];
var chosenLayer = null;
var reader = new jsts.io.GeoJSONReader();
statesLayer.eachLayer(layer => {
    layer.setStyle({
        color: 'grey',
        fillColor: 'grey'
    });
    if (!layer.toGeoJSON().properties.NAME)
        return;
    const stateCodeName = statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')];
    statesMap[stateCodeName] = {
        layer: layer,
        // 0 - neutral, 1 - democrats, 2 - republicans
        party: "NEUTRAL",
        score: 0
    };
    layer.on('click', () => onStateClick(layer));
});

function onStateClick(layer) {
    if (!iam)
        return;
    if (iam == "REPUBLICAN" && neighborStates.indexOf(layer) == -1 && statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party != 'REPUBLICAN')
        return;
    if (iam == "DEMOCRAT" && neighborStates.indexOf(layer) == -1 && statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party != 'DEMOCRAT')
        return;
    if (chosenLayer != null) {
        if (chosenLayer == layer) {
            neighborStates.forEach(layer => {
                if (statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party == 'NEUTRAL')
                    layer.setStyle({
                        fillColor: 'grey',
                        color: 'grey'
                    });
                else if (statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party == 'REPUBLICAN')
                    layer.setStyle({
                        fillColor: 'red',
                        color: 'red'
                    });
                else
                    layer.setStyle({
                        fillColor: 'blue',
                        color: 'blue'
                    });
            });
            neighborStates = [];
            chosenLayer = null;
        } else if (neighborStates.indexOf(layer) != -1) {
            neighborStates.forEach(layer => {
                if (statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party == 'NEUTRAL')
                    layer.setStyle({
                        fillColor: 'grey',
                        color: 'grey'
                    });
                else if (statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party == 'REPUBLICAN')
                    layer.setStyle({
                        fillColor: 'red',
                        color: 'red'
                    });
                else
                    layer.setStyle({
                        fillColor: 'blue',
                        color: 'blue'
                    });
            });
            stompClient.send('/app/state-changed', {}, JSON.stringify({
                'from': statesEnum[chosenLayer.toGeoJSON().properties.NAME.replace(' ', '_')],
                'to': statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')],
                'party': iam
            }));
            chosenLayer = null;
            neighborStates = [];
        }
        return;
    }
    chosenLayer = layer;
    var chosenLayerGeometry = reader.read(chosenLayer.toGeoJSON()).geometry;
    statesLayer.eachLayer(layer => {
        if (chosenLayer != layer) {
            if (reader.read(layer.toGeoJSON()).geometry.intersects(chosenLayerGeometry)) {
                neighborStates.push(layer);
                if (statesMap[statesEnum[layer.toGeoJSON().properties.NAME.replace(' ', '_')]].party == 'NEUTRAL')
                    layer.setStyle({
                        fillColor: 'orange',
                        color: 'orange'
                    });
            }
        }
    });
}

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
stompClient.connect({}, frame => {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "/app/start", false);
    xmlHttp.send(null);
    iam = JSON.parse(xmlHttp.responseText)["party"];
    stompClient.subscribe('/game/status', function (game) {
        var newState = JSON.parse(game.body);
        for (var state in newState['modifiedStates']) {
            statesMap[state].layer.setStyle({
                color: newState['modifiedStates'][state]["party"] == "REPUBLICAN" ? 'red' : 'blue',
                fillColor: newState['modifiedStates'][state]["party"] == "REPUBLICAN" ? 'red' : 'blue'
            });
            if (!statesMap[state].layer.getTooltip())
                statesMap[state].layer.bindTooltip(newState['modifiedStates'][state]['score'].toString(), {
                    permanent: true,
                    direction: 'center',
                    opacity: 1
                });
            else
                statesMap[state].layer.setTooltipContent(newState['modifiedStates'][state]['score'].toString());
            statesMap[state].party = newState['modifiedStates'][state]["party"];
            statesMap[state].score = newState['modifiedStates'][state]['score'];
            if (state == 'CA' && statesMap[state].party == 'REPUBLICAN')
                alert("Republicans win!");
            if (state == 'FL' && statesMap[state].party == 'DEMOCRAT')
                alert("Democrats win");
        }
    });
    stompClient.send("/app/start");
});

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