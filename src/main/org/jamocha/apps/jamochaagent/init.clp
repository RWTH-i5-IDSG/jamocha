; ===================================================
; Initial data that is essential to run the JamochaAgent.
; ===================================================


; ===================================================
; Definition of the templates that are needed.
; ===================================================

(deftemplate agent-description
	"A template defining an agent."
	; Name / address of an agent.
    (slot name (type STRING))
    
    ; Flag indicating if this agent is running locally or not.
    (slot local (type BOOLEAN))
)

(deftemplate agent-message
	"Definition of an agent-message."
	(slot sender (type STRING))
	(multislot receivers)
	(multislot reply-to)
	(slot performative (type STRING))
	(slot content (type STRING))
	(slot language (type STRING))
	(slot encoding (type STRING))
	(slot ontology (type STRING))
	(slot protocol (type STRING))
	(slot conversation-id (type STRING))
	(slot in-reply-to (type STRING))
	(slot reply-with (type STRING))
	(slot reply-by (type LONG))
	(silent slot content-clips (type STRING))
	(silent slot timestamp (type LONG))
	(slot incoming (type BOOLEAN)(default TRUE))
	(slot processed (type BOOLEAN)(default FALSE))
)

(deftemplate agent-message-initiator
	"Deftemplate for a message initiator."
	; The receiver of the new message to send.
	(slot receiver) ; type: agent-description
		
	; The message that was the cause for this initiator and acts as reference to this
	; initiator. It might  be NIL, if this initiator is used to start a new
	; interaction sequence.
	(slot refering-message) ; type: agent-message
		
	; The Interaction Protocol that is currently in use for communication.
	(slot protocol (type STRING))
	
	; This slot is the performative used in the refering message. It might be empty,
	; if this initiator is used to start a new interaction sequence. Although this
	; information can also be obtained through the refering message this helps to keep
	; the rules checking for the performative small.
	(slot previous-performative (type STRING))
	
	; In this slot the content in SL for the new message to send is hold.
	(slot content (type STRING))
	
	; In this slot the content in CLIPS for the new message to send is hold.
	(slot content-clips (type STRING))
	
	; This flag indicates whether this initiator was processed and the new message was
	; send or not. It is not essential for the rule engine but for the user to see
	; what is happening.
	(slot processed (type BOOLEAN)(default FALSE))
	
	; If an error occured during evaluation of the content of the refering message
	; it will be placed here.
	(slot error (type STRING)(default NIL))
)

(deftemplate done
	(slot action (type STRING))
)

(deftemplate result
	(slot action (type STRING))
	(slot result-ref (type STRING))
)

; ===================================================
; Definition of functions that are needed.
; ===================================================

(deffunction process-incoming-message
	"Processes incoming messages with any protocol and performative."
	(functiongroup AgentFunctions)
	(?message ?protocol)
	
	; Assert the agent that sent the message if he is unknown.
	(bind ?agent (fact-id 
		(assert (agent-description (name (fact-slot-value ?message "sender"))(local FALSE)))
	))
	
	; Translate the Code according to the given performative.
	(bind ?clipsCode
	    (sl2clips
	    	(fact-slot-value ?message "performative")
	    	(fact-slot-value ?message "content")
	    )
	)
	
	; Add the translated code to the initial message.
	(modify ?message (content-clips ?clipsCode))
	
	; Set the message to processed.
	(modify ?message (processed TRUE))
	
	; Evaluate the code in the rete engine.
	(bind ?error "")
	(bind ?*message* ?message)
	(bind ?result (eval ?clipsCode ?error))
	
	; Assert a new agent message initiator that will initiate a new message according
	; to the used performative and protocol.
	(assert (agent-message-initiator
		(receiver ?agent)
		(refering-message ?message)
		(protocol ?protocol)
		(previous-performative (fact-slot-value ?message "performative"))
		(content (clips2sl ?result))
		(content-clips ?result)
		(error ?error)
	))
)


(deffunction process-outgoing-message
	"Processes (= sends) outgoing messages."
	(functiongroup AgentFunctions)
	(?message)
	(agent-send-message 
		(fact-slot-value ?message "receivers")
		(fact-slot-value ?message "reply-to")
		(fact-slot-value ?message "performative")
		(fact-slot-value ?message "content") 
		(fact-slot-value ?message "language")
		(fact-slot-value ?message "encoding")
		(fact-slot-value ?message "ontology")
		(fact-slot-value ?message "protocol")
		(fact-slot-value ?message "conversation-id")
		(fact-slot-value ?message "in-reply-to")
		(fact-slot-value ?message "reply-with")
		(fact-slot-value ?message "reply-by")
	)
	(modify ?message (processed TRUE))
)

; ===================================================
; Definition of rules that are needed.
; ===================================================

(defrule incoming-message
	"Fires when a message in FIPA-SL arrives, that is addressed to a local Agent."
	; Only look for messages addressed to the local agent.
	(agent-description
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(receivers ?receivers)
		(language "fipa-sl")
		(protocol ?protocol)
		(incoming TRUE)
		(processed FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver ?receivers) 0))
	
	=>
	
	; Process the message.
	(process-incoming-message ?message ?protocol)
	
	(fire)
)


(defrule outgoing-message
	"Fires when a message in FIPA-SL has to be send."
	; Only extract messages that have not been sent yet and that are outgoing.
	?message <- (agent-message
		(language "fipa-sl")
		(incoming FALSE)
		(processed FALSE)
	)
	
	=>
	
	; Process the message.
	(process-outgoing-message ?message)
	
	(fire)
)