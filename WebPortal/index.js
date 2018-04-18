firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // javascript:window.location.href='index.html'

  } else {
	//javascript:window.location.href='login.html'  	
 	
  }
});

function logout(){
	firebase.auth().signOut().then(function() {
  // Sign-out successful.
  javascript:window.location.href='login.html'
}).catch(function(error) {
  // An error happened.
});
	
}

//////////
// $(document).ready(function(){

var markers = [];

function getAllCoods(){

  return new Promise((resolve,reject) => {
    var rootRef = firebase.database().ref().child("dangerCoods");
    rootRef.on("child_added", snap => {
    var coods = snap.child("coods").val();
    var iconImage = snap.child("iconImage").val();
    var content = snap.child("content").val();
    var cc = {"coods":coods, "iconImage":iconImage, "content": content};
    window.markers.push(cc);
    // console.log(markers)
    resolve("done") 
    });
  });
}

async function showMarkers(){
  const pr = await getAllCoods();
  console.log("the final then" + markers); 
}

showMarkers();
