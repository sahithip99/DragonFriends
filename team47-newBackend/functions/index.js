const functions = require('firebase-functions')
const express = require('express')
const path = require('path')

const app = express()
var bodyParser = require('body-parser')
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())


// Initialize Cloud Firestore on Cloud Functions
const admin = require('firebase-admin')
var serviceAccount = require(path.join(__dirname, 'dragonfriends-service-account-key.json'))
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
})
var db = admin.firestore()

app.get('/timestamp', (request, response) => {
  response.send(`${Date.now()}`)
})

// Set cache GET request handler
app.get('/timestamp-cached', (request, response) => {
  response.set('Cache-Control', 'public, max-age=300, s-max-age=600') // set cache with max age of 600 seconds
  response.send(`${Date.now()}`)
})

// ------------------GET COURSES BY CRN -------------------
app.post('/classByCrn', (request, response) => {
  var crn = request.body.crn
  console.log('crn', crn)
  var course = queryByCrn(crn)
  course.then((doc) => {
    if (!doc.exists) {
      console.log('No such document!')
      response.send(null)
      return null
    } else {
      console.log('Document data:', doc.data())
      response.send(doc.data())
      return doc.data()
    }
  })
    .catch(err => {
      console.log('Error getting the class', err)
      response.send(null)
      return null
    })
})

function queryByCrn (crn) {
  var courseRef = db.collection('quarter').doc('spring1718').collection('courses').doc(crn)
  return courseRef.get()
}

exports.app = functions.https.onRequest(app)