; ===================================================
; Initial data that is essential to run the JamochaAgent.
; ===================================================


; ===================================================
; Definition of the templates that are needed.
; ===================================================

(deftemplate agent-description
	"A template defining an agent."
    (slot name (type STRING))
    (slot local (type BOOLEAN))
)

(deftemplate agent-ontology
	"Definition of ontologies. (not needed at the moment)"
    (slot name (type STRING))
    (slot definition (type STRING))
)

(deftemplate agent-message
	"Definition of an (incoming) agent-message."
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
	(multislot user-properties)
	(silent slot content-clips (type STRING))
	(slot processed (type BOOLEAN)(default FALSE))
)

(deftemplate agent-message-initiator
	"Deftemplate for a message initiator."
	(slot receiver)
	(slot refering-message)
	(slot protocol (type STRING))
	(slot previous-performative (type STRING))
	(slot content (type STRING))
	(slot content-clips (type STRING))
	(slot processed (type BOOLEAN)(default FALSE))
	(slot error (type STRING)(default NIL))
)

; ===================================================
; definition of functions that are needed
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

; ===================================================
; Definition of rules that are needed
; ===================================================

(defrule incoming-message
	"Fires when a message in FIPA-SL arrives, that is addressed to a local Agent."
	; Only look for messages addressed to a local agent. There might be more than one
	; agent running locally using this rete engine.
	(agent-description
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet.
	?message <- (agent-message
		(receivers ?receivers)
		(language "fipa-sl")
		(protocol ?protocol)
		(processed FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver ?receivers) 0))
	
	=>
	
	; Process the message.
	(process-incoming-message ?message ?protocol)
	
	(fire)
)