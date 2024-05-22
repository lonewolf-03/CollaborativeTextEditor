let operationsQueue = [];


let ws;

let content = '';
let documentVersion = 0;
let name = '';
let owner_id = 0;
let doc_id = 0;

let currPos = 0;




// Function to send message to server
function send(otMessage) {
    while(ws.readyState !== 1){

    }
    console.log("message sent");
	ws.send(JSON.stringify(otMessage));
}

function getCursorPosition(el) {
    //var el = $(this).get(0);
    let pos = 0;
    let posEnd = 0;
    const selection = window.getSelection();
    const range = selection.getRangeAt(0);
    const clonedRange = range.cloneRange();
    clonedRange.selectNodeContents(document.getElementById('document-body'));
    clonedRange.setEnd(range.endContainer, range.endOffset);
    const cursorPosition = clonedRange.toString().length;
    // return both selection start and end;
    console.log("cursore position : " + cursorPosition); 
    console.log(cursorPosition + 1);
    return [ cursorPosition, cursorPosition + 1 ];
};

document.getElementById('document-body').addEventListener('keydown', (e) => {
    console.log("key press detected");
    let kCd = e.keyCode || e.whiclet
    let position = getCursorPosition(e.target);
    let deleted = '';
    let action;
    let chr = '';
    let pos = position[0];
    /* Ignore these keys - 
    Up: 38,Down: 40,Right: 39,Left: 37,Esc: 27
    Ctrl: 17,Alt: 18, Shift: 16*/
    let ignoreKeys = [ 38, 40, 39, 37, 27, 17, 18, 16, 13 ];
    if (kCd == 8) { // Backspace
        console.log("detected backspace");
        if (position[0] == position[1]) {
            if (position[0] == 0){
                deleted = '';
            }
            else{
                let val = e.target.innerText;
                deleted = val.substr(position[0] - 1, 1);
            }
        } else {
            let val = e.target.innerText;
            deleted = val.substring(position[0], position[1]);
        }
        action = 'DELETE';
        chr = deleted;
        pos = position[0] - 1;
    } else if (kCd == 46) { // Delete
        console.log("detected delete");
        if (position[0] == position[1]) {
            let val = e.target.innerText;
            if (position[0] === val.length)
                deleted = '';
            else
                deleted = val.substr(position[0], 1);
        } else {
            let val = e.target.innerText;
            deleted = val.substring(position[0], position[1]);
        }
        action = 'DELETE';
        chr = deleted;
    } else if (ignoreKeys.includes(kCd)) {
        console.log("detected ignored character");
        console.log("Ignored - " + chr);
        action = 'ignore';
    } else {
        console.log("detected another character");
        action = 'INSERT';
        chr = String.fromCharCode(kCd);
        if (!e.shiftKey) {
            chr = chr.toLowerCase();
        }
    }

    console.log("queing or sending");
    queueOrSend(action, chr, pos, documentVersion);

});

function queueOrSend(action, chr, pos, versionBeforeThisOp){ 
	if (action && action !== 'ignore' && pos >= 0) {
		let timestamp = Date.now();
		let otMessage = {
			"Action" : action,
			"character" : chr,
			"pos" : pos,
			"versionBeforeUpdate" : versionBeforeThisOp,
			"timestamp" : timestamp,
			"docId" : doc_id
		};
		// If there are no operations in queue then send directly, else add to queue.
		if (operationsQueue.length === 0) {
            console.log("queue is empty. sending.");
			send(otMessage);
		}
		operationsQueue.push(otMessage);
	}
}


function processReceivedOperation(operationRec) {
    console.log("Inside processor");
	if (operationRec.isAck) {
		// If its just acknowledgement of earlier operation from this session, then update version.
		updateVersion(operationRec.versionAfterThisOp);

		// Remove acknowledged operation from operationsQueue
		for (let i = operationsQueue.length - 1; i >= 0; i--) {
			if (operationsQueue[i].timestamp === operationRec.timestamp) {
				console.log("Ack received so removing = "
					+ operationsQueue[i].timestamp
					+ " data.timestamp: " + operationRec.timestamp);
				operationsQueue.splice(i, 1);
			}
		}

		// If there are other operations in queue then send next operation to server.
		let nextOpToSend = operationsQueue.shift();
        console.log("operations queue: " + operationsQueue);
		if (nextOpToSend) {
			send(nextOpToSend);
		}

	} else {
        console.log("Not ack ;-(");
		operationRec = transform(operationRec);
	    applyOperation(operationRec);

		updateVersion(operationRec.versionAfterThisOp);
	}
}
/**
 * If there are any operations awaiting in queue, 
 * then transform currentOp from awaiting operations.
 */
function transform(currentOp) {
	for (let i = 0; i < operationsQueue.length; i++) {
		let pastOp = operationsQueue[i];
		console.log("transform check past op - " + pastOp)
		// If past operation was at index before currentOp then adjust position, else keep currentOp as is.
		if (pastOp != null && pastOp.pos <= currentOp.pos) {
			console.log("Position is before so change");
			let position = currentOp.pos;
			if ("INSERT" === pastOp.action) {
				position = position + 1;
			} else if ("DELETE" === pastOp.action) {
				position = position - 1;
			}
			console.log("New position - " + position);
			currentOp.pos = position;
		} else {
			console.log("Position not before so no change - ");
		}
	}
	console.log("transform after = " + JSON.stringify(currentOp));
	return currentOp;
}

function applyOperation(data) {
    // Apply operation to text area
    content = document.getElementById("document-body").innerText;
    let text = document.getElementById("document-body").value;
    if ("INSERT" === data.action) {
        console.log("inside apply" + data.pos);
        content = content.substring(0, data.pos) + data.chr
                + content.substring(data.pos);
    } else if ("DELETE" === data.action) {
        content = content.substring(0, data.pos)
                + content.substring(data.pos + 1);
    }
    document.getElementById("document-body").innerText = content;
	console.log("New content after aplying the operation : " + content);
}

function updateVersion(newVersion) {
	console.log("newVersion = " + newVersion);
	// Update global variable documentVersion & also set display version.
	documentVersion = newVersion;
}



    document.addEventListener("DOMContentLoaded", function() {
        var documentBody = document.getElementById("document-body");
        var wordCount = document.getElementById("word-count");
    
        // Update word count every time the user types something
        documentBody.addEventListener("input", function() {
            var text = documentBody.innerText;
            var words = text.trim().split(/\s+/).length;
            wordCount.innerText = "Word count: " + words;
        });
    
        // Reset word count when user clears the document area
        documentBody.addEventListener("keydown", function(event) {
            if (event.keyCode === 8 || event.keyCode === 46) { // Backspace or Delete key
                if (documentBody.innerText === "") {
                    wordCount.innerText = "Word count: 0";
                }
            }
        });
    
        // Disable context menu
        documentBody.oncontextmenu = function() {
            return false;
        };
    
        // Disable keyboard shortcuts for copying and pasting
        documentBody.onkeydown = function(event) {
            if(event.keyCode === 13){
                event.preventDefault();
            }
            if (event.ctrlKey || event.metaKey) {
                switch (event.keyCode) {
                    case 67: // Ctrl+C
                    case 86: // Ctrl+V
                    case 65: // Ctrl+A
                        event.preventDefault();
                        return false;
                }
            }
        };
    
        // Disable paste event
        documentBody.onpaste = function(event) {
            event.preventDefault();
            return false;
        };
    });

    const submitbutton = document.getElementById("submit-credentials");
    console.log(submitbutton);
    submitbutton.onclick = () => {
        let jwt = document.getElementById("jwt");
        let uid = document.getElementById("uid");
        let docid = document.getElementById("docid");
	    ws = new WebSocket("ws://localhost:8088/websocket/otserver?jwt=" + jwt.value + "&docid=" + docid.value + "&uid=" + uid.value);
        // Javascript callback function when connection is established. 
ws.onopen = function() {
	console.log("Openened connection to websocket");
}

// Javascript callback function when messages is received from server.
ws.onmessage = function(msg) {
	msgData = msg.data;
	console.log("On Message = " + msg + " msgData: " + msgData)
    let data;
    try{
        data = JSON.parse(msgData);
    }catch{
        console.log("problem found");
        console.log(msgData);
        return;
    }

    if(Object.keys(data).length === 5){ // the first message , a document
        document.getElementById("document-title").value = data.name;
        document.getElementById("document-body").innerText = data.content;
        if(data.readOnly){
            document.getElementById("document-body").onkeydown = (e) => {e.preventDefault();}
        }
        documentVersion = 0;
        name = data.name;
        doc_id = data.id;

    }else{ // it's an operation
        console.log("Message received. It's an operation");
        let sessionId = '';
        let chr = '';
        let versionBeforeThisOp = 0;
        let versionAfterThisOp = 0;
        let action = '';
        let pos = 0;
        let isAck = false;
        let timestamp = 0;
        let operation_doc_id = 0;

        sessionId = data.sessionId;
        chr = data.character;
        versionBeforeThisOp = parseInt(data.versionBeforeUpdate);
        versionAfterThisOp = parseInt(data.versionAfterUpdate);
        action = data.Action;
        pos = parseInt(data.pos);
        isAck = data.isAcknowledgement;
        timestamp = parseInt(data.timestamp);
        operation_doc_id = parseInt(data.docId);

        const operationRec = {
			sessionId : sessionId,
			chr : chr,
			versionBeforeThisOp : versionBeforeThisOp,
			versionAfterThisOp : versionAfterThisOp,
			action : action,
			pos : pos,
			isAck : isAck,
			timestamp : timestamp,
			operation_doc_id : operation_doc_id
		}
        console.log("message will be processed");
        console.log(operationRec);
	    processReceivedOperation(operationRec);
    } 
}

// Javascript callback function when connection is closed.
ws.onclose = function(msg) {
	console.log("Closed connection to websocket");
}

document.getElementById("exit").oncl

document.getElementById("exit").onclick = () => {
    ws.close();
};

    };


