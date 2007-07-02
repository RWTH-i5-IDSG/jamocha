; ===================================================
; Initial data that is essential to run the JamochaAgent.
; ===================================================

(defmodule Agent)

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


(deftemplate agent-evaluation-error
	"Template for an error during evaluation."
	(slot message)
	(slot error (type STRING))
)


(deftemplate agent-agree-result
	"Result-Template for agree performatives."
	(slot message)
	(multislot propositions)
)


(deftemplate agent-queryIf-result
	"Result-Template for query-if performatives."
	(slot message)
	(slot result (type BOOLEAN)(default FALSE))
)


(deftemplate agent-queryRef-result
	"Result-Template for query-ref performatives."
	(slot message)
	(slot refOp (type STRING))
	(multislot items)
)


(deftemplate agent-request-result
	"Result-Template for request performatives."
	(slot message)
	(slot result)
)

; ===================================================
; Definition of functions that are needed.
; ===================================================

(deffunction process-incoming-message
	"Processes incoming messages with any protocol and performative."
	(functiongroup AgentFunctions)
	(?message)
	
	; Assert the agent that sent the message if he is unknown.
	(bind ?agent
		(fact-id 
			(assert
				(agent-description
					(name (fact-slot-value ?message "sender"))
					(local FALSE)
				)
			)
		)
	)
	
	; Translate the Code according to the given performative.
	(bind ?clipsCode
	    (sl2clips
	    	(fact-slot-value ?message "performative")
	    	(fact-slot-value ?message "content")
	    )
	)
	
	; Set the error to an empty value.
	(bind ?error "")
	
	; We substitute the String %MSG% in the clips code with the actual message-fact
	; to give the performatives the possibility to use a reference in their results.
	(bind ?clipsCode
		(str-replace
			?clipsCode
			"%MSG%"
			(str-cat "(fact-id " (get-fact-id ?message) ")")
		)
	)
	
	; Evaluate the code. The performatives are responsible for creating their specific
	; result-facts.
	(eval-blocking ?clipsCode ?error)
	
	; Here we have to check for Errors that occured during evaluation.
	
	(if
		(neq ?error NIL)
	 then
	 	(if
			(> (str-length ?error) 0)
		 then
		 	(assert
		 		(agent-evaluation-error
		 			(message ?message)
		 			(error ?error)
		 		)
		 	)
		)
	)
	
	
	; Add the translated code to the initial message.
	(modify ?message (content-clips ?clipsCode))
	
	; Set the message to processed.
	(modify ?message (processed TRUE))
)


(deffunction process-outgoing-message
	"Processes (= sends) outgoing messages."
	(functiongroup AgentFunctions)
	(?message)
	
	; Call agent-send-message with the slot values.
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
	
	; Set the message to processed.
	(modify ?message (processed TRUE))
)


(deffunction strip-braces
	"Removes a brace from the left and the right end of a String."
	(functiongroup AgentFunctions)
	(?aString)
	(return 
		(sub-string 1 (- (str-length ?aString) 1) ?aString)
	)
)


(deffunction prepare-receivers
	"Puts together the receivers of a message by extracting the old sender and the reply-to content."
	(functiongroup AgentFunctions)
	(?message)
	(bind ?receivers
		(union$
			(create$ (fact-slot-value ?message "sender"))
			(fact-slot-value ?message "reply-to")
		)
	)
	; delete NIL values in the list.
	(return (delete-member$ ?receivers NIL))
)


(deffunction send-agree
	"Sends an agree to a message using the same protocol with optional propositions."
	(functiongroup AgentFunctions)
	(?message ?propositions)
	
	(bind ?receivers (prepare-receivers ?message))
	(bind ?newContent (fact-slot-value ?message "content"))
	(if
		(neq ?propositions NIL)
	 then
		(bind ?newContent
			(str-cat ?newContent ?propositions)
		)
	)
	
	(assert 
		(agent-message
			(receivers ?receivers)
			(performative "agree")
			(content ?newContent)
			(language (fact-slot-value ?message "language"))
			(encoding (fact-slot-value ?message "encoding"))
			(ontology (fact-slot-value ?message "ontology"))
			(protocol (fact-slot-value ?message "protocol"))
			(conversation-id (fact-slot-value ?message "conversation-id"))
			(in-reply-to (fact-slot-value ?message "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
	
	(fire)
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
	(process-incoming-message ?message)
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
)