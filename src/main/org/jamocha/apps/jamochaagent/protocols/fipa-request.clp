; ===================================================
; Definition of rules that handle the protocol
; fipa-request for all possible performatives.
; ===================================================



(defrule fipa-request-request
	"Fires for agent-message-evaluation-result facts with protocol request and performative request."
	?initiator <- (agent-message-evaluation-result
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-request")
		(previous-performative "request")
		(result ?result)
		(processed FALSE)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers (prepare-receivers ?oldMessage))
	
	(if
		(eq ?error "")
	; no error occured
	then
		(assert (agent-message
			(receivers ?receivers)
			(performative "agree")
			(content (fact-slot-value ?oldMessage "content"))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
		
		; This fire is needed to immediately send the agree
		(fire)
		
		; Send the reply as inform to the sender of the initiating message and all
		; agents that listed in the reply-to field.
		(assert (agent-message
			(receivers ?receivers)
			(performative "inform")
			(content (str-cat (fact-slot-value ?oldMessage "content") crlf (clips2sl ?result)))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
		
	; an error occured
	else
		(assert (agent-message
			(receivers ?receivers)
			(performative "refuse")
			(content (str-cat (fact-slot-value ?oldMessage "content") crlf "\"" ?error "\""))
			(language (fact-slot-value ?oldMessage "language"))
			(encoding (fact-slot-value ?oldMessage "encoding"))
			(ontology (fact-slot-value ?oldMessage "ontology"))
			(protocol (fact-slot-value ?oldMessage "protocol"))
			(conversation-id (fact-slot-value ?oldMessage "conversation-id"))
			(in-reply-to (fact-slot-value ?oldMessage "reply-with"))
			(reply-with "")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		))
	)
	
	; Set the initiator to processed.
	(modify ?initiator (processed TRUE))
)

(defrule fipa-request-agree
	"Fires for agent-message-evaluation-result facts with protocol request and performative agree."
	?initiator <- (agent-message-evaluation-result
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-request")
		(previous-performative "agree")
		(result ?result)
		(processed FALSE)
	)
	
	=>
	
	(printout t ?result)
	; Set the initiator to processed.
	(modify ?initiator (processed TRUE))
)

(defrule fipa-request-inform
	"Fires for agent-message-evaluation-result facts with protocol request and performative inform."
	?initiator <- (agent-message-initiator
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-request")
		(previous-performative "inform")
		(result ?result)
		(processed FALSE)
	)
	
	=>
	(printout t ?result)
	;(sl2clips ?result)	
	; Set the initiator to processed.
	(modify ?initiator (processed TRUE))
)

