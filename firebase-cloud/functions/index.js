const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/**
 * Function that will send push notifications whenever something in
 * "/messages" is changed.
 */
exports.pushNotifyMessages = functions.database.ref('/messages')
    .onWrite(event => {
        /* Set the message to be the first value that was changed
           or added */
        const delta = event.data._delta;
        console.log(delta);

        /* We can assume that only one message and one chatroom will be
           sent because that's how the application works */
        
        let chatroom = delta[Object.keys(delta)[0]];
        let message = chatroom[Object.keys(chatroom)[0]];
        
        let payload = {
            notification: {
                title: "New Message",
                body: message["message"].toString()
            }
        };
        
        /* Return the action of sending a notification as a
           "promise", so that it can be run asynchronously by
           firebase */
        return admin.messaging()
            .sendToTopic(Object.keys(delta)[0], payload);
    });
