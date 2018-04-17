var express = require('express')
var path = require('path')
var app = express()
var bodyParser = require('body-parser')

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

app.set('port', (process.env.PORT || 5000))
app.use(express.static(path.join(__dirname, '/public')))

app.get('/', function (request, response) {
  response.send('This is the backend for DragonFriends!')
})

app.post('/classByCrn', function (request, response) {
  var crn = request.body.crn
  console.log('crn', crn)
  var course = queryByCrn(crn)
  course.then(function (doc) {
    if (!doc.exists) {
      console.log('No such document!')
      response.send(null)
    } else {
      console.log('Document data:', doc.data())
      response.send(doc.data())
    }
  })
    .catch(err => {
      console.log('Error getting the class', err)
      response.send(null)
    })
})

app.listen(app.get('port'), function () {
  console.log('Node app is running at localhost:' + app.get('port'))
})

// Initialize Cloud Firestore on Cloud Functions
const admin = require('firebase-admin')
var serviceAccount = require(path.join(__dirname, 'dragonfriends-service-account-key.json'))

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
})
var db = admin.firestore()

// var docRef = db.collection('users').doc('alovelace')
// docRef.set({
//   first: 'Ada',
//   last: 'Lovelace',
//   born: 1815
// })
function queryByCrn (crn) {
  var courseRef = db.collection('quarter').doc('spring1718').collection('courses').doc(crn)
  // var courseQuery = courseRef.where('crn', '==', '30040')
  return courseRef.get()
  // Promise.all([coursePromise]).then(function (res) {
  //   var doc = res[0]
  //   if (!doc.exists) {
  //     console.log('No such document!')
  //     return `No such document`
  //   } else {
  //     console.log('Document data:', doc.data())
  //     return doc.data()
  //   }
  // }).catch(err => {
  //   console.log('Error getting the class', err)
  //   return `Cannot find document`
  // })
}
