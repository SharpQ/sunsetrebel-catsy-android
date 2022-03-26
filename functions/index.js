const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fieldValue = admin.firestore.FieldValue; 
admin.initializeApp();

exports.publicEventsUsersWriteListener = functions.firestore
  .document('publicEvents/{eventId}/usersJoined/{userId}')
  .onWrite((change, context) => {
	// ref to the parent document
	const eventId = context.params.eventId;
    const docRef = admin.firestore().collection('publicEvents').doc(eventId);

    if (!change.before.exists) {
      // New document Created : add one to count
      docRef.update({ eventParticipants: fieldValue.increment(1) });
    } else if (change.before.exists && change.after.exists) {
      // Updating existing document : Do nothing
    } else if (!change.after.exists) {
      // Deleting document : subtract one from count
      docRef.update({ eventParticipants: fieldValue.increment(-1) });
    }

    return;
  });

exports.privateEventsUsersWriteListener = functions.firestore
  .document('privateEvents/{eventId}/usersJoined/{userId}')
  .onWrite((change, context) => {
	// ref to the parent document
	const eventId = context.params.eventId;
    const docRef = admin.firestore().collection('privateEvents').doc(eventId);

    if (!change.before.exists) {
      // New document Created : add one to count
      docRef.update({ eventParticipants: fieldValue.increment(1) });
    } else if (change.before.exists && change.after.exists) {
      // Updating existing document : Do nothing
    } else if (!change.after.exists) {
      // Deleting document : subtract one from count
      docRef.update({ eventParticipants: fieldValue.increment(-1) });
    }

    return;
  });