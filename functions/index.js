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
	
	if (action == "ADD_FRIEND") {
		var requestObject = {
         senderId : senderId,
         recipientId : recipientId,
		 action : action,
		};
		
		return admin.firestore().collection('userProfiles').doc(recipientId).collection('incomeRequests').doc(senderId).set(requestObject);
	} else if (action == "REMOVE_FRIEND") {
		admin.firestore().collection('userProfiles').doc(recipientId).update({ 
			userFriends: fieldValue.arrayRemove(senderId) 
		});
		
		return admin.firestore().collection('userProfiles').doc(senderId).collection('outcomeRequests').doc(recipientId).delete();
	}
    return;
  });
  
exports.responseIncomeRequest = functions.firestore
  .document('userProfiles/{userId}/incomeRequests/{requestId}')
  .onDelete(async (snap, context) => {
    const requestType = snap.data().action;
	const senderId = context.params.userId;
	const requestId = context.params.requestId;

	if (requestType == "ADD_FRIEND") {
        var userRef = admin.firestore().collection('userProfiles').doc(senderId);
        await userRef.get().then(doc => {
            const userFriend = doc.data().userFriends;
            if (userFriend.includes(requestId)) {
                admin.firestore().collection('userProfiles').doc(requestId).update({
                    userFriends: fieldValue.arrayUnion(senderId)
                });
            }
            return admin.firestore().collection('userProfiles').doc(requestId).collection('outcomeRequests').doc(senderId).delete();
        });
	} else if (requestType == "EVENT_INVITE") {
        const eventAccessType = snap.data().eventAccessType;
        const eventId = snap.data().eventId;
        var userRef = admin.firestore().collection('userProfiles').doc(senderId);
        await userRef.get().then(doc => {
            var userEvents;
            var eventDocRef;
            const userFullName = doc.data().userFullName;
            const userProfileImg = doc.data().userProfileImg;
            const userLinkFacebook = doc.data().userLinkFacebook;
            const userLinkInstagram = doc.data().userLinkInstagram;
            const userLinkTelegram = doc.data().userLinkTelegram;
            const userLinkTikTok = doc.data().userLinkTikTok;

            if (eventAccessType == "PUBLIC" || eventAccessType == "SELECTIVE") {
                userEvents = doc.data().joinedPublicEvents;
                eventDocRef = admin.firestore().collection('publicEvents').doc(eventId).collection('usersJoined').doc(senderId);
            } else if (eventAccessType == "PRIVATE") {
                userEvents = doc.data().joinedPrivateEvents;
                eventDocRef = admin.firestore().collection('privateEvents').doc(eventId).collection('usersJoined').doc(senderId);
            }

            if (userEvents.includes(eventId)) {
                var requestObjects = {
                     userFullName : userFullName,
                     userId : senderId,
                     userProfileImg : userProfileImg,
                     userLinkFacebook : userLinkFacebook,
                     userLinkInstagram : userLinkInstagram,
                     userLinkTelegram : userLinkTelegram,
                     userLinkTikTok : userLinkTikTok,
                };
                return eventDocRef.set(requestObjects);
            }
        });
	}
    return;
  });

exports.publicEventInviteRequest = functions.firestore
  .document('publicEvents/{eventId}/invites/{inviteId}')
  .onCreate((snap, context) => {
	const inviteId = context.params.inviteId;
	const hostId = snap.data().hostId;
	const eventId = snap.data().eventId;
	const invitedUsersId = snap.data().invitedUsersId.values();
	const eventAccessType = snap.data().eventAccessType;

    if (invitedUsersId.length < 1 || invitedUsersId == null) {
        return;
    }

    for (const userId of invitedUsersId) {
        var requestObjects = {
             eventId : eventId,
             eventAccessType : eventAccessType,
             senderId : hostId,
             recipientId : userId,
             action : "EVENT_INVITE",
        };
        admin.firestore().collection('userProfiles').doc(userId).collection('incomeRequests').doc(inviteId).set(requestObjects);
    }

    return;
  });

exports.privateEventInviteRequest = functions.firestore
  .document('privateEvents/{eventId}/invites/{inviteId}')
  .onCreate((snap, context) => {
	const inviteId = context.params.inviteId;
	const hostId = snap.data().hostId;
	const eventId = snap.data().eventId;
	const invitedUsersId = snap.data().invitedUsersId.values();
	const eventAccessType = snap.data().eventAccessType;

    if (invitedUsersId.length < 1 || invitedUsersId == null) {
        return;
    }

    for (const userId of invitedUsersId) {
        var requestObjects = {
             eventId : eventId,
             eventAccessType : eventAccessType,
             senderId : hostId,
             recipientId : userId,
             action : "EVENT_INVITE",
        };
        admin.firestore().collection('userProfiles').doc(userId).collection('incomeRequests').doc(inviteId).set(requestObjects);
    }

    return;
  });