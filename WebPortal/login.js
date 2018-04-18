firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    javascript:window.location.href='index.html'
  } else {

  }
});

function login() {
	var userEmail = document.getElementById("email_feild").value;
	var userPass = document.getElementById("password_feild").value;
	window.alert(userEmail + " " + userPass)
	javascript:window.location.href='index.html';
		firebase.auth().signInWithEmailAndPassword(userEmail, userPass).catch(function(error) {
	  		// Handle Errors here.
	 	 	var errorCode = error.code;
	  		var errorMessage = error.message;
	  		window.alert("Error : " + errorMessage)
			});
	javascript:window.location.href='index.html';
}

function logout(){
	firebase.auth().signOut().then(function() {
  // Sign-out successful.
  javascript:window.location.href='login.htm'
}).catch(function(error) {
  // An error happened.
});
	
}
