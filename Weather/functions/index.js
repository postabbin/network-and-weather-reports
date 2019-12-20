////////////////////////////////////////////////////////////////////////////////////////////////////

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fetch = require('node-fetch');

////////////////////////////////////////////////////////////////////////////////////////////////////

admin.initializeApp();

////////////////////////////////////////////////////////////////////////////////////////////////////

const thekkumUrl = 'http://dataservice.accuweather.com/currentconditions/v1/2871429/historical?apikey=TEjIT8YK9vAhzqDlyhGQDnrAAMIAB7an&details=true';
const thiruvanUrl = 'http://dataservice.accuweather.com/currentconditions/v1/2875251/historical?apikey=pe9LA5an3tGJxLOBzYtoGH777tPgGtGV&details=true';

let db = admin.firestore();

////////////////////////////////////////////////////////////////////////////////////////////////////

exports.fetchThiruvanReport = functions.pubsub.schedule('every 1 hours synchronized').timeZone('Asia/Kolkata').onRun(async (context) => {
    var res = await fetch(thiruvanUrl);
    var json = await res.json();
    var report = json[0];

    var newReport = new Object();
    newReport.timestamp = report.EpochTime;
    newReport.temperature = report.Temperature.Metric.Value;
    newReport.humidity = report.RelativeHumidity;

    var epochDate = new Date((newReport.timestamp + 19800) * 1000);

    if (epochDate.getHours() === 23) {
        newReport.precipitation = report.PrecipitationSummary.Past24Hours.Metric.Value;
    }

    await db.collection('thiruvan').add(newReport);

    return null;
});

exports.fetchThekkumReport = functions.pubsub.schedule('every 1 hours synchronized').timeZone('Asia/Kolkata').onRun(async (context) => {
    var res = await fetch(thekkumUrl);
    var json = await res.json();
    var report = json[0];

    var newReport = new Object();
    newReport.timestamp = report.EpochTime;
    newReport.temperature = report.Temperature.Metric.Value;
    newReport.humidity = report.RelativeHumidity;

    var epochDate = new Date((newReport.timestamp + 19800) * 1000);

    if (epochDate.getHours() === 23) {
        newReport.precipitation = report.PrecipitationSummary.Past24Hours.Metric.Value;
    }

    await db.collection('thekkum').add(newReport);

    return null;
});