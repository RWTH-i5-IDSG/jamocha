; ===================================================
; Definition of rules that handle the protocol
; fipa-request for all possible performatives.
; ===================================================



(defrule fipa-request-request
	"Fires for agent-message-initiators with protocol request and performative request."
	?initiator <- (agent-message-initiator
		(receiver ?receiver)
		(refering-message ?oldMessage)
		(protocol "fipa-request")
		(previous-performative "request")
		(content ?result)
		(processed FALSE)
	)
	
	=>
	
	; Send the reply as inform to the sender of the initiating message and all
	; agents that listed in the reply-to field.
	(agent-send-message 
		(fact-slot-value ?receiver "name") 
		(fact-slot-value ?oldMessage reply-to)
		"inform"
		(str-cat (fact-slot-value ?oldMessage content) crlf ?result) 
		(fact-slot-value ?oldMessage language)
		(fact-slot-value ?oldMessage encoding)
		(fact-slot-value ?oldMessage ontology)
		(fact-slot-value ?oldMessage protocol)
		(fact-slot-value ?oldMessage conversation-id)
		(fact-slot-value ?oldMessage reply-with)
		""
		NIL
	)
	
	; Set the initiator to processed.
	(modify ?initiator (processed TRUE))
)