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
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://dragonfriends-eb4fc.firebaseio.com'

})

// Initialize references to Firestore and Realtime Database
var firestore = admin.firestore()
var db = admin.database()


//----------------------SAMPLE GET REQUESTS ---------------------------
app.get('/', (request, response) => {
  response.write("This is the backend for Dragon friends.")
  response.end()
})
app.get('/timestamp', (request, response) => {
  response.send(`${Date.now()}`)
})

// Set cache GET request handler
app.get('/timestamp-cached', (request, response) => {
  response.set('Cache-Control', 'public, max-age=300, s-max-age=600') // set cache with max age of 600 seconds
  response.send(`${Date.now()}`)
})


// ------------------- AUTH ----------
//TODO: verify email




//----------------------------- FIRESTORE --------------------------------------------------------

// ------------------GET COURSES BY CRN -------------------
app.post('/classByCrn', (request, response) => {
  var crn = request.body.crn
  console.log('crn', crn)
  var course = queryByCrn(crn)
  course.then((doc) => {
    if (!doc.exists) {
      console.log('No such document!')
      response.status(500).send(null)
      return null
    } else {
      console.log('Document data:', doc.data())
      response.send(doc.data())
      return doc.data()
    }
  })
    .catch(err => {
      console.log('Error getting the class', err)
      response.status(500).send(null)
      return null
    })
})

function queryByCrn (crn) {
  var courseRef = firestore.collection('quarter').doc('spring1718').collection('courses').doc(crn)
  return courseRef.get()
}


//Add user to the class
app.post('/addUserToClass', (request, response) => {
  var uid = request.body.uid
  var crn = request.body.crn

  // Add to user database
  var userRef = db.ref(`users/${uid}/classes/${crn}`)
  userRef.update({crn: crn})

  // 
  var classRef = firestore.collection('quarter').doc('spring1718').collection('courses').doc(crn).collection("uids").doc(uid)
  classRef.set({
    uid: uid
  }).then(res=> {
    response.send(res);
    return true;
  })
  .catch(err=> {
    response.send(err.message);
  })
  
   
})

//Get all the roster information
app.post('/getRosterInfo', (request, response) => {
  console.log("request body", request.body)
  var crn = request.body.crn
  console.log("Crn", crn)

  //Initialize the promises
  var getClassPromise = firestore.collection('quarter').doc('spring1718').collection('courses').doc(crn).collection("uids").get() //get class Data promise

  //Get all the users inside of the class
  var getUserPromise = getClassPromise.then(classData => {
    console.log("class Data", classData);
    var uidsPromiseArr = []
    classData.forEach(doc=> {
      var uid = doc.data().uid
      console.log("Found uid", doc.data())
      var getUserProfilePromise = getUserProfile(uid)
      uidsPromiseArr.push(getUserProfilePromise)
    })
    return Promise.all(uidsPromiseArr)

  })

  //Execute the promise
  getUserPromise.then(res => {
    var rosterData = {}
    for (var i = 0; i<res.length; i++){
      var user = res[i].val()
      console.log("user", user)
      rosterData[user.uid] = res[i]
    }
    console.log("rosterData", rosterData);
    response.send(rosterData)
    return true
    
  })
  .catch(err=> {
    console.log("error getting roster info", err)
    response.status(500).send("Error getting roster info")
    return null
  })
  
})

//----------------------------- REALTIME DATABASE --------------------------------------------------------
//Return user profile 
app.post('/getUserProfile', (request, response) => {
  var uid = request.body.uid
  var getUserProfilePromise = getUserProfile(uid)
  var getClassDataPromise = getUserProfilePromise.then(snap => {
    if (snap.val()){
      var userClasses = snap.val().classes

      getClassesPromise = []
      for (var i in userClasses){
        var crn = userClasses[i].crn
        console.log("crn", crn)
        var promise = queryByCrn(crn)
        getClassesPromise.push(promise)
      }

      return Promise.all(getClassesPromise)
      
    }
    else {
      response.status(500).send("User does not exist")
      return null
    }
    
  })
  .catch(err => {
    console.log(err)
    response.status(500).send("Error getting user profile!")
  })

  Promise.all([getUserProfilePromise, getClassDataPromise]).then(res => {
    var userData = res[0].val()
    console.log("userData", userData)
    var classData = res[1]
    console.log("class data", classData)
    for (var i = 0; i<classData.length; i++){
      var crn = classData[i].data().crn
      console.log("Setting crn", crn)
      userData["classes"][crn] = classData[i].data()
    }
    response.send(userData)
    return true
  })
  .catch(err => {
    console.log(err)
    response.status(500).send("Error getting user profile")
  })
 
})


function getUserProfile(uid){
  return db.ref(`users/${uid}`).once("value")
}



app.listen(3000, () => {
  console.log("Listening on port 3000");
})



exports.app = functions.https.onRequest(app)
