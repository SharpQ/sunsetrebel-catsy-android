const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fieldValue = admin.firestore.FieldValue; 
admin.initializeApp();

exports.publicEventsUsersWriteListener = functions.firestore
  .document('publicEvents/{eventId}/usersJoined/{userId}')
  .onWrite((change, context) => {
	const eventId = context.params.eventId;
    const docRef = admin.firestore().collection('publicEvents').doc(eventId);

	admin.firestore().collection('publicEvents').doc(eventId).get().then(function(doc) {
		if (doc.exists) {
			 if (!change.before.exists) {
			  // New document Created : add one to count
			  return docRef.update({ eventParticipants: fieldValue.increment(1) });
			} else if (change.before.exists && change.after.exists) {
			  // Updating existing document : Do nothing
			} else if (!change.after.exists) {
			  // Deleting document : subtract one from count
			  return docRef.update({ eventParticipants: fieldValue.increment(-1) });
			}
		}
	});
   
    return;
  });

exports.privateEventsUsersWriteListener = functions.firestore
  .document('privateEvents/{eventId}/usersJoined/{userId}')
  .onWrite((change, context) => {
	const eventId = context.params.eventId;
    const docRef = admin.firestore().collection('privateEvents').doc(eventId);

    admin.firestore().collection('privateEvents').doc(eventId).get().then(function(doc) {
		if (doc.exists) {
			 if (!change.before.exists) {
			  // New document Created : add one to count
			  return docRef.update({ eventParticipants: fieldValue.increment(1) });
			} else if (change.before.exists && change.after.exists) {
			  // Updating existing document : Do nothing
			} else if (!change.after.exists) {
			  // Deleting document : subtract one from count
			  return docRef.update({ eventParticipants: fieldValue.increment(-1) });
			}
		}
	});

    return;
  });
  
exports.sendFriendRequest = functions.firestore
  .document('userProfiles/{userId}/outcomeRequests/{anotherUserId}')
  .onCreate((snap, context) => {
	const senderId = context.params.userId;
	const recipientId = context.params.anotherUserId;
	const action = snap.data().action;
	
	if (action == "ADD") {
		var requestObject = {
         senderId : senderId,
         recipientId : recipientId,
		 action : action,
		};
		
		return admin.firestore().collection('userProfiles').doc(recipientId).collection('incomeRequests').doc(senderId).set(requestObject);
	} else if (action == "REMOVE") {
		admin.firestore().collection('userProfiles').doc(recipientId).update({ 
			userFriends: fieldValue.arrayRemove(senderId) 
		});
		
		return admin.firestore().collection('userProfiles').doc(senderId).collection('outcomeRequests').doc(recipientId).delete();
	}
    return;
  });
  
exports.responseFriendRequest = functions.firestore
  .document('userProfiles/{userId}/incomeRequests/{anotherUserId}')
  .onDelete(async (snap, context) => {
	const senderId = context.params.userId;
	const recipientId = context.params.anotherUserId;
	var userRef = admin.firestore().collection('userProfiles').doc(senderId);
    await userRef.get().then(doc => {
		const userFriend = doc.data().userFriends;
		if (userFriend.includes(recipientId)) {
			admin.firestore().collection('userProfiles').doc(recipientId).update({ 
				userFriends: fieldValue.arrayUnion(senderId) 
			});
		}
		return admin.firestore().collection('userProfiles').doc(recipientId).collection('outcomeRequests').doc(senderId).delete();
    });
  });