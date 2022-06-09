const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fieldValue = admin.firestore.FieldValue; 
admin.initializeApp();

exports.publicEventUsers = functions.firestore
  .document('publicEvents/{eventId}/usersJoined/{userId}')
  .onWrite(async (change, context) => {
	const eventId = context.params.eventId;
    var docRef = admin.firestore().collection('publicEvents').doc(eventId);

	await docRef.get().then(doc => {
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
		return;
	});
  });

exports.privateEventUsers = functions.firestore
  .document('privateEvents/{eventId}/usersJoined/{userId}')
  .onWrite(async (change, context) => {
	const eventId = context.params.eventId;
    var docRef = admin.firestore().collection('privateEvents').doc(eventId);

    docRef.get().then(doc => {
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
		return;
	});
  });
  
exports.outcomeRequest = functions.firestore
  .document('userProfiles/{userId}/outcomeRequests/{anotherUserId}')
  .onCreate((snap, context) => {
    const action = snap.data().action;
	const senderId = context.params.userId;
	const senderName = snap.data().senderName;
	const senderProfileImg = snap.data().senderProfileImg;
	const recipientId = context.params.anotherUserId;
	const createTS = snap.data().createTS;
	
	if (action == "ADD_FRIEND") {
		var requestObject = {
             action : action,
             senderId : senderId,
             senderName: senderName,
             senderProfileImg: senderProfileImg,
             recipientId : recipientId,
             createTS: createTS
		};
		
		return admin.firestore().collection('userProfiles').doc(recipientId).collection('incomeRequests').doc(senderId).set(requestObject);
	} else if (action == "REMOVE_FRIEND") {
		admin.firestore().collection('userProfiles').doc(recipientId).collection('detailedInfo').doc(recipientId).update({
			userFriends: fieldValue.arrayRemove(senderId) 
		});
		
		return admin.firestore().collection('userProfiles').doc(senderId).collection('outcomeRequests').doc(recipientId).delete();
	}
    return;
  });

exports.incomeRequest = functions.firestore
  .document('userProfiles/{userId}/incomeRequests/{requestId}')
  .onDelete(async (snap, context) => {
    const requestType = snap.data().action;
	const senderId = context.params.userId;
	const requestId = context.params.requestId;

	if (requestType == "ADD_FRIEND") {
        var userRef = admin.firestore().collection('userProfiles').doc(senderId).collection('detailedInfo').doc(senderId);
        await userRef.get().then(doc => {
            const userFriend = doc.data().userFriends;
            if (userFriend.includes(requestId)) {
                admin.firestore().collection('userProfiles').doc(requestId).collection('detailedInfo').doc(requestId).update({
                    userFriends: fieldValue.arrayUnion(senderId)
                });
            }
            return admin.firestore().collection('userProfiles').doc(requestId).collection('outcomeRequests').doc(senderId).delete();
        });
	}
    return;
  });

exports.publicEventDeletion = functions.firestore
  .document('publicEvents/{eventId}')
  .onDelete(async (snap, context) => {
	const eventId = context.params.eventId;
	const hostId = snap.data().hostId;
	const eventAvatar = snap.data().eventAvatar;

	//Event avatar deletion
	if (eventAvatar != null) {
	    const bucket = admin.storage().bucket("catsy-28b85.appspot.com");
        const event_folder = "userProfiles/"+hostId+"/"+eventId;
        bucket.deleteFiles({
            prefix: event_folder
        });
	}
    return;
  });

exports.privateEventDeletion = functions.firestore
  .document('privateEvents/{eventId}')
  .onDelete(async (snap, context) => {
	const eventId = context.params.eventId;
	const hostId = snap.data().hostId;
	const eventAvatar = snap.data().eventAvatar;

	//Event avatar deletion
	if (eventAvatar != null) {
        const bucket = admin.storage().bucket("catsy-28b85.appspot.com");
        const event_folder = "userProfiles/"+hostId+"/"+eventId;
        bucket.deleteFiles({
            prefix: event_folder
        });
    }
    return;
  });