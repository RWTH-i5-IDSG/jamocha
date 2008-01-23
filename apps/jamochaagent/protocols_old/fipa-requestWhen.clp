; ===================================================
; Definition of rules that handle the protocol
; fipa-request-when for all possible performatives.
; ===================================================

(defrule fipa-requestWhen-error
	"Fires for agent-evaluation-error facts with protocol request and performative request."
	?message <- (agent-message
		(protocol "fipa-request-when")
		(performative "request-when")
	)
	(agent-evaluation-error
		(message ?message)
		(error ?error)
	)
	
	=>
	
	(bind ?receivers (prepare-receivers ?message))
	
	(assert 
		(agent-message
			(receiver ?receivers)
			(performative "refuse")
			(content (str-cat (fact-slot-value ?message "content") crlf "\"" ?error "\""))
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
)

(defrule fipa-requestWhen
	"Fires for agent-message facts with protocol request-when and performative request-when that are not linked by an error."
	?message <- (agent-message
		(protocol "fipa-request-when")
		(performative "request-when")
		(processed TRUE)
	)
	(not
		(agent-evaluation-error
			(message ?message)
		)
	)

	=>
	
	(send-agree ?message "" (fact-slot-value ?message "content"))
	
)

(defrule fipa-requestWhen-result
	"Fires for agent-requestWhen-result facts with protocol request-when and performative request-when."
	?message <- (agent-message
		(protocol "fipa-request-when")
		(performative "request-when")
	)
	?msgTemplate <- (agent-message
		(is-template TRUE)
	)
	(not (agent-requestWhen-result
		(message ?message)
		(result ?msgTemplate)
	))
	
	=>
	
	(bind ?receiverList (agent-identifiers-to-names ?receivers))
		
	(modify ?msgTemplate (is-template FALSE))
)